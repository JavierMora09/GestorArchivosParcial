/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackUps;

import java.io.*;
import java.net.*;

public class BackupServer {
    private static final int PORT = 6000; // Puerto del servidor secundario

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor secundario escuchando en el puerto " + PORT);

            while (true) {
                // Aceptar conexión del servidor principal
                Socket socket = serverSocket.accept();
                System.out.println("Servidor principal conectado");

                // Recibir archivo
                InputStream inputStream = socket.getInputStream();
                DataInputStream dis = new DataInputStream(inputStream);
                
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();
                
                FileOutputStream fos = new FileOutputStream("servidor_secundario/" + fileName);
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;

                while (totalRead < fileSize && (bytesRead = dis.read(buffer, 0, buffer.length)) > 0) {
                    fos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }

                fos.close();
                System.out.println("Archivo recibido y almacenado: " + fileName);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
