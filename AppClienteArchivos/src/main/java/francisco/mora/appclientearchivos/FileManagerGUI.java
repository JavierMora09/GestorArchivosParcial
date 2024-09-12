/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package francisco.mora.appclientearchivos;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class FileManagerGUI extends JFrame {

    private DefaultListModel<String> fileListModel;
    private JList<String> fileList;
    private JTextField searchField;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private DefaultMutableTreeNode rootNode;
    private JTree folderTree;

    public FileManagerGUI() {
        // Configuración de la ventana
        setTitle("Gestor de Archivos NFS");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear panel principal
        JPanel panel = new JPanel(new BorderLayout());

        // Barra de búsqueda
        searchField = new JTextField(15);
        JButton searchButton = new JButton("Buscar");
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Buscar archivo:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Árbol de carpetas
       
        folderTree = new JTree(rootNode);
        folderTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                String path = getPathFromNode(selectedNode);
                loadFilesFromFolder(path);  // Cargar los archivos de la carpeta seleccionada
            }
        });

        JScrollPane treeScrollPane = new JScrollPane(folderTree);
        panel.add(treeScrollPane, BorderLayout.WEST);

        // Lista de archivos
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        JScrollPane fileScrollPane = new JScrollPane(fileList);
        panel.add(fileScrollPane, BorderLayout.CENTER);

        // Botón para renombrar archivo
        JButton renameButton = new JButton("Renombrar");
        renameButton.addActionListener(e -> renameFile());
        panel.add(renameButton, BorderLayout.SOUTH);

        // Botón para descargar archivo
        JButton downloadButton = new JButton("Descargar");
        downloadButton.addActionListener(e -> downloadFile());
        panel.add(downloadButton, BorderLayout.SOUTH);

        // Conectar al servidor intermediario y cargar carpetas
        connectToServer();
        loadFileTree();

        // Mostrar la ventana
        add(panel);
    }

    // Conectar al servidor intermediario
    private void connectToServer() {
        try {
            socket = new Socket("192.168.1.31", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Conectado al servidor");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectarse al servidor", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cargar el árbol de carpetas
    private void loadFileTree() {
        try {
            out.println("listar /");  // Listar desde la raíz

            String response;
            while (!(response = in.readLine()).equals("FIN_LISTA")) {
                // Asume que el servidor marca las carpetas con un "/" al final o de alguna manera distintiva.
                if (response.endsWith("/")) {
                    DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(response);
                    rootNode.add(folderNode);
                }
            }

            // Actualiza el modelo del árbol
            ((DefaultTreeModel) folderTree.getModel()).reload();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar carpetas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cargar archivos de la carpeta seleccionada
    private void loadFilesFromFolder(String path) {
        fileListModel.clear(); // Limpiar la lista antes de cargar los archivos

        try {
            out.println("listar " + path);  // Enviar la ruta completa al servidor

            String response;
            while (!(response = in.readLine()).equals("FIN_LISTA")) {
                // Aquí podrías diferenciar si es archivo o carpeta, si necesitas agregar íconos en la lista
                fileListModel.addElement(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar archivos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Obtener la ruta de la carpeta desde el nodo
    private String getPathFromNode(DefaultMutableTreeNode node) {
        StringBuilder path = new StringBuilder();
        while (node != null) {
            path.insert(0, node.getUserObject().toString() + "/");
            node = (DefaultMutableTreeNode) node.getParent();
        }
        // Eliminar el último "/" para que coincida con la estructura esperada en el servidor
        if (path.length() > 0) {
            path.setLength(path.length() - 1);
        }
        return path.toString();
    }

    // Renombrar archivo seleccionado
    private void renameFile() {
        String selectedFile = fileList.getSelectedValue();
        if (selectedFile != null) {
            String newFileName = JOptionPane.showInputDialog(this, "Nuevo nombre para el archivo:");
            if (newFileName != null && !newFileName.trim().isEmpty()) {
                // Enviar comando al servidor para renombrar el archivo
                out.println("renombrar " + selectedFile + " " + newFileName);
                try {
                    String response = in.readLine();
                    JOptionPane.showMessageDialog(this, response);
                    loadFileTree(); // Refrescar árbol de carpetas
                    loadFilesFromFolder(getPathFromNode((DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent())); // Refrescar lista de archivos
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo para renombrar");
        }
    }

    // Descargar archivo seleccionado
    private void downloadFile() {
        String selectedFile = fileList.getSelectedValue();
        if (selectedFile != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecciona la ubicación para guardar el archivo");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileDestino = fileChooser.getSelectedFile();

                // Enviar comando 'descargar' al servidor
                out.println("descargar " + selectedFile);

                // Leer el archivo desde el servidor y guardarlo en el destino
                try (BufferedInputStream bis = new BufferedInputStream(socket.getInputStream()); FileOutputStream fos = new FileOutputStream(fileDestino)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    JOptionPane.showMessageDialog(this, "Archivo descargado correctamente");
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al descargar archivo", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo para descargar");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileManagerGUI gui = new FileManagerGUI();
            gui.setVisible(true);
        });
    }
}
