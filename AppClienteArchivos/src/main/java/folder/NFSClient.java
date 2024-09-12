/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package folder;

import java.io.*;
import java.net.*;

public class NFSClient {

    public static void main(String[] args) {
        String serverAddress = "192.168.1.31";  // Dirección IP del cliente intermediario
        int port = 8080;

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Conectado al servidor intermediario en " + serverAddress);

            // Recibe la lista de archivos y carpetas
            String fileList;
            while ((fileList = in.readLine()) != null) {
                System.out.println(fileList);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}