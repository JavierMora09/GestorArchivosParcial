/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ServidorMagane;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileManagerSocketServer {
    public static void main(String[] args) {
        int port = 5000;
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor de sockets listo en el puerto " + port);
            
            while (true) {
                // Aceptar conexión de cliente
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado: " + socket.getInetAddress());
                
                // Crear un hilo para manejar cada cliente de forma independiente
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor de sockets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Leer la solicitud del cliente
            String request = in.readLine();
            System.out.println("Solicitud del cliente: " + request);

            // Procesar la solicitud (ejemplo: listar archivos en un directorio)
            if (request.equals("LIST")) {
                File folder = new File(".");
                for (File file : folder.listFiles()) {
                    out.println(file.getName());
                }
            }

            // Cerrar la conexión con el cliente
            socket.close();
        } catch (IOException e) {
            System.err.println("Error manejando al cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
