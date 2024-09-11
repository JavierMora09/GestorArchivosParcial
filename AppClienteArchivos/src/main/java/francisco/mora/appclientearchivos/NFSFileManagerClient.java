/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package francisco.mora.appclientearchivos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.tree.DefaultMutableTreeNode;

public class NFSFileManagerClient extends JFrame {

    private JTree fileTree;  // Árbol de carpetas
    private JList<String> fileList;  // Lista de archivos
    private DefaultListModel<String> fileListModel;  // Modelo de la lista de archivos
    private String currentDir = "/mnt/nfs_share";  // Directorio actual montado desde NFS

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
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        JScrollPane fileListScrollPane = new JScrollPane(fileList);
        add(fileListScrollPane, BorderLayout.CENTER);

        // Menú de clic derecho para renombrar o descargar archivos
        JPopupMenu fileMenu = new JPopupMenu();
        JMenuItem renameItem = new JMenuItem("Renombrar");
        JMenuItem downloadItem = new JMenuItem("Descargar");

        fileMenu.add(renameItem);
        fileMenu.add(downloadItem);

        fileList.setComponentPopupMenu(fileMenu);

        // Acción de renombrar
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFile = fileList.getSelectedValue();
                if (selectedFile != null) {
                    String newName = JOptionPane.showInputDialog("Nuevo nombre para el archivo:");
                    if (newName != null && !newName.trim().isEmpty()) {
                        renombrarArchivo(selectedFile, newName);
                    }
                }
            }
        });

        // Acción de descargar
        downloadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFile = fileList.getSelectedValue();
                if (selectedFile != null) {
                    descargarArchivo(selectedFile);
                }
            }
        });

        // Cargar archivos desde el servidor NFS
        cargarArchivosDesdeNFS(currentDir);

        setVisible(true);
    }

    private void cargarArchivosDesdeNFS(String rutaNFS) {
        fileListModel.clear();  // Limpiar la lista de archivos antes de cargar nuevos
        File nfsDir = new File(rutaNFS);
        if (nfsDir.exists() && nfsDir.isDirectory()) {
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

    private void renombrarArchivo(String archivoActual, String nuevoNombre) {
        File archivo = new File(currentDir + "/" + archivoActual);
        File archivoRenombrado = new File(currentDir + "/" + nuevoNombre);
        if (archivo.exists()) {
            if (archivo.renameTo(archivoRenombrado)) {
                JOptionPane.showMessageDialog(this, "Archivo renombrado con éxito");
                cargarArchivosDesdeNFS(currentDir);  // Recargar la lista de archivos
            } else {
                JOptionPane.showMessageDialog(this, "Error al renombrar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "El archivo seleccionado no existe", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void descargarArchivo(String nombreArchivo) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona la ubicación para guardar el archivo");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoDestino = fileChooser.getSelectedFile();
            File archivoOrigen = new File(currentDir + "/" + nombreArchivo);

            try {
                Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "Archivo descargado con éxito");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al descargar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NFSFileManagerClient());
    }
}
