/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackUps;

import java.io.*;
import java.net.*;

public class MainServer {
    private static final int PORT = 5000; // Puerto para el servidor principal
    private static final String BACKUP_SERVER_ADDRESS = "localhost"; // Dirección IP del servidor secundario
    private static final int BACKUP_SERVER_PORT = 6000; // Puerto del servidor secundario

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor principal iniciado en el puerto " + PORT);

            while (true) {
                // Aceptar conexión del cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                // Manejar la solicitud del cliente en un hilo separado
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase para manejar la comunicación con el cliente
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                // Aquí se recibe un archivo del cliente
                InputStream inputStream = clientSocket.getInputStream();
                DataInputStream dis = new DataInputStream(inputStream);
                
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();
                
                FileOutputStream fos = new FileOutputStream("servidor_principal/" + fileName);
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;
                
                while (totalRead < fileSize && (bytesRead = dis.read(buffer, 0, buffer.length)) > 0) {
                    fos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
                
                fos.close();
                System.out.println("Archivo recibido: " + fileName);

                // Enviar el archivo al servidor secundario
                sendFileToBackupServer(fileName);

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Método para enviar el archivo al servidor secundario
        private void sendFileToBackupServer(String fileName) {
            try (Socket backupSocket = new Socket(BACKUP_SERVER_ADDRESS, BACKUP_SERVER_PORT)) {
                System.out.println("Enviando archivo al servidor secundario: " + fileName);

                File file = new File("servidor_principal/" + fileName);
                FileInputStream fis = new FileInputStream(file);
                OutputStream outputStream = backupSocket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputStream);

                dos.writeUTF(fileName); // Enviar el nombre del archivo
                dos.writeLong(file.length()); // Enviar el tamaño del archivo

                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = fis.read(buffer)) > 0) {
                    dos.write(buffer, 0, bytesRead);
                }
                
                fis.close();
                System.out.println("Archivo enviado al servidor secundario: " + fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
