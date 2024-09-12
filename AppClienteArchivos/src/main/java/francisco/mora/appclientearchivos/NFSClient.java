/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package francisco.mora.appclientearchivos;

import java.io.File;

public class NFSClient {
    public static void main(String[] args) {
        String nfsPath = "/mnt/nfs/compartido"; // Ruta montada del servidor NFS

        File directory = new File(nfsPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] filesList = directory.listFiles();

            System.out.println("Contenido del directorio NFS:");
            for (File file : filesList) {
                if (file.isDirectory()) {
                    System.out.println("[Carpeta] " + file.getName());
                } else if (file.isFile()) {
                    System.out.println("[Archivo] " + file.getName());
                }
            }
        } else {
            System.out.println("El directorio NFS no existe o no es un directorio.");
        }
    }
}
