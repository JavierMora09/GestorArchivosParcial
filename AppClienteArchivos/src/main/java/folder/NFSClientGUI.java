/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package folder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class NFSClientGUI {

    private JFrame frame;
    private JList<String> folderList;
    private DefaultListModel<String> folderModel;
    private JTextArea fileDisplayArea;

    private String serverAddress = "192.168.1.31";  // Dirección IP del intermediario
    private int port = 8080;

    public NFSClientGUI() {
        frame = new JFrame("Cliente NFS");

        // Panel izquierdo para carpetas
        folderModel = new DefaultListModel<>();
        folderList = new JList<>(folderModel);
        folderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        folderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedFolder = folderList.getSelectedValue();
                if (selectedFolder != null) {
                    showFolderContent("/" + selectedFolder);  // Cargar contenido de la carpeta seleccionada
                }
            }
        });

        JScrollPane folderScrollPane = new JScrollPane(folderList);
        folderScrollPane.setPreferredSize(new Dimension(150, 400));

        // Panel derecho para mostrar archivos
        fileDisplayArea = new JTextArea(20, 40);
        fileDisplayArea.setEditable(false);
        JScrollPane fileScrollPane = new JScrollPane(fileDisplayArea);

        // Botón para renombrar la carpeta seleccionada
        JButton renameButton = new JButton("Renombrar Carpeta");
        renameButton.addActionListener(e -> renameSelectedFolder());

        // Botón para descargar el contenido de la carpeta o archivo
        JButton downloadButton = new JButton("Descargar Archivo");
        downloadButton.addActionListener(e -> downloadSelectedFile());

        // Botón para recargar carpetas
        JButton reloadButton = new JButton("Recargar Carpetas");
        reloadButton.addActionListener(e -> loadFolders("/"));

        // Panel para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(renameButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(reloadButton);  // Añadir botón de recargar

        // Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, folderScrollPane, fileScrollPane);
        splitPane.setDividerLocation(150);

        frame.getContentPane().add(splitPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Cargar las carpetas iniciales
        loadFolders("/");
    }

    // Método para renombrar la carpeta seleccionada
    private void renameSelectedFolder() {
        String selectedFolder = folderList.getSelectedValue();
        if (selectedFolder != null) {
            String newName = JOptionPane.showInputDialog(frame, "Nuevo nombre para la carpeta:", selectedFolder);
            if (newName != null && !newName.trim().isEmpty()) {
                try (Socket socket = new Socket(serverAddress, port);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    out.println("RENAME:/" + selectedFolder + ",/" + newName);
                    String response = in.readLine();
                    JOptionPane.showMessageDialog(frame, response);

                    // Recargar las carpetas
                    loadFolders("/");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Método para descargar un archivo
    private void downloadSelectedFile() {
        String selectedFolder = folderList.getSelectedValue();
        if (selectedFolder != null) {
            String fileName = JOptionPane.showInputDialog(frame, "Nombre del archivo a descargar:");
            if (fileName != null && !fileName.trim().isEmpty()) {
                try (Socket socket = new Socket(serverAddress, port);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedInputStream in = new BufferedInputStream(socket.getInputStream())) {

                    out.println("DOWNLOAD:/" + selectedFolder + "/" + fileName);

                    // Descargar el archivo y guardarlo localmente
                    File file = new File(fileName);
                    try (FileOutputStream fileOut = new FileOutputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) > 0) {
                            fileOut.write(buffer, 0, bytesRead);
                        }
                    }
                    JOptionPane.showMessageDialog(frame, "Archivo descargado: " + fileName);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Error al descargar el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }

    // Método para mostrar el contenido de una carpeta seleccionada
    private void showFolderContent(String folderPath) {
        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("LIST:" + folderPath);

            fileDisplayArea.setText("");  // Limpiar el área de texto
            String line;
            while ((line = in.readLine()) != null) {
                fileDisplayArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para cargar las carpetas iniciales
    private void loadFolders(String rootPath) {
        folderModel.clear();
        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("LIST:" + rootPath);

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("DIR:")) {
                    folderModel.addElement(line.substring(4));  // Añadir solo el nombre de la carpeta
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NFSClientGUI::new);
    }
}