/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ServidorMagane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class FileManagerServiceImpl extends UnicastRemoteObject implements FileManagerService {

    protected FileManagerServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public List<String> listFiles(String directory) throws RemoteException {
        List<String> fileList = new ArrayList<>();
        File folder = new File(directory);
        
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                fileList.add(file.getName());
            }
        }
        
        return fileList;
    }

    @Override
    public byte[] downloadFile(String filePath) throws RemoteException {
        try {
            File file = new File(filePath);
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();
            return fileData;
        } catch (IOException e) {
            throw new RemoteException("Error downloading file", e);
        }
    }

    @Override
    public boolean renameFile(String oldName, String newName) throws RemoteException {
        File oldFile = new File(oldName);
        File newFile = new File(newName);
        
        if (oldFile.exists()) {
            return oldFile.renameTo(newFile);
        }
        
        return false;
    }
}
