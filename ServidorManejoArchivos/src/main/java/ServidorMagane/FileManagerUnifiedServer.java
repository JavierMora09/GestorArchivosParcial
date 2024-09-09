/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ServidorMagane;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class FileManagerUnifiedServer {
    public static void main(String[] args) {
        try {
            // Lanzar el servidor RMI en un hilo separado
            Thread rmiServerThread = new Thread(() -> {
                try {
                    LocateRegistry.createRegistry(1099);
                    FileManagerService service = new FileManagerServiceImpl();
                    Naming.rebind("FileManagerService", service);
                    System.out.println("Servidor RMI listo...");
                } catch (Exception e) {
                    System.err.println("Error iniciando el servidor RMI: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            // Lanzar el servidor de Sockets en un hilo separado
            Thread socketServerThread = new Thread(() -> {
                int port = 5000;
                try (ServerSocket serverSocket = new ServerSocket(port)) {
                    System.out.println("Servidor de Sockets listo en el puerto " + port);
                    while (true) {
                        Socket socket = serverSocket.accept();
                        new ClientHandler(socket).start();
                    }
                } catch (IOException e) {
                    System.err.println("Error en el servidor de Sockets: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            // Iniciar ambos hilos
            rmiServerThread.start();
            socketServerThread.start();

        } catch (Exception e) {
            System.err.println("Error iniciando los servidores: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
