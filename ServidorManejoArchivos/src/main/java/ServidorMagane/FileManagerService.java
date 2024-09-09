/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ServidorMagane;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileManagerService extends Remote {
    List<String> listFiles(String directory) throws RemoteException;
    byte[] downloadFile(String filePath) throws RemoteException;
    boolean renameFile(String oldName, String newName) throws RemoteException;
}
