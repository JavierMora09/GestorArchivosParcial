/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package francisco.mora.appclientearchivos;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

public class NFSFileManagerClient extends JFrame {

    private JTree fileTree;  // Árbol de carpetas
    private JList<String> fileList;  // Lista de archivos

    public NFSFileManagerClient() {
        setTitle("Gestor de Archivos NFS");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Layout
        setLayout(new BorderLayout());

        // Panel de la barra de búsqueda
        JPanel topPanel = new JPanel(new BorderLayout());
        JTextField searchBar = new JTextField("Buscar...");
        topPanel.add(searchBar, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Árbol de carpetas
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("NFS Server");
        fileTree = new JTree(root);
        JScrollPane treeScrollPane = new JScrollPane(fileTree);
        add(treeScrollPane, BorderLayout.WEST);

        // Lista de archivos
        DefaultListModel<String> fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        JScrollPane fileListScrollPane = new JScrollPane(fileList);
        add(fileListScrollPane, BorderLayout.CENTER);

        // Cargar archivos desde el servidor NFS
        cargarArchivosDesdeNFS("/mnt/nfs_share", fileListModel);

        setVisible(true);
    }

    private void cargarArchivosDesdeNFS(String rutaNFS, DefaultListModel<String> fileListModel) {
        // Verificar que el directorio NFS esté accesible
        File nfsDir = new File(rutaNFS);
        if (nfsDir.exists() && nfsDir.isDirectory()) {
            System.out.println("Accediendo al directorio NFS: " + rutaNFS);
            File[] archivos = nfsDir.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isFile()) {
                        fileListModel.addElement(archivo.getName());  // Agrega el nombre del archivo a la lista
                    } else if (archivo.isDirectory()) {
                        fileListModel.addElement("[Carpeta] " + archivo.getName());  // Agrega las carpetas
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo acceder al servidor NFS", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NFSFileManagerClient());
    }
}
