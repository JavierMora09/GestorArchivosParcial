/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ServidorMagane;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class FileManagerServer {
    public static void main(String[] args) {
        try {
            // Crear el registro de RMI en el puerto 1099
            LocateRegistry.createRegistry(1099);
            
            // Instanciar el servicio
            FileManagerService service = new FileManagerServiceImpl();
            
            // Registrar el servicio con un nombre
            Naming.rebind("FileManagerService", service);
            
            System.out.println("Servidor de gestión de archivos listo y funcionando...");
        } catch (Exception e) {
            System.err.println("Error iniciando el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

