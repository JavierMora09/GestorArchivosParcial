/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package francisco.mora.appclientearchivos;
import java.io.*;
import java.net.*;

public class NFSCliente {

    public static void main(String[] args) {
        try (Socket socket = new Socket("192.168.1.31", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Enviar solicitud al cliente intermediario
            out.println("LIST_FILES");

            // Leer la respuesta
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
