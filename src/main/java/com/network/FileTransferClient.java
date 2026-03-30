package com.network;

import java.io.*;
import java.net.*;

public class FileTransferClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    public boolean uploadFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File does not exist: " + filePath);
            return false;
        }

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());
             FileInputStream fileIn = new FileInputStream(file)) {

            // Send file name
            out.writeUTF(file.getName());

            // Send file size
            out.writeLong(file.length());

            // Send file data
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            // Read response
            boolean success = in.readBoolean();
            String message = in.readUTF();
            
            if (!success) {
                System.err.println("Upload failed: " + message);
            } else {
                System.out.println("Upload successful: " + message);
            }
            
            return success;

        } catch (IOException e) {
            System.err.println("Error uploading file: " + e.getMessage());
            return false;
        }
    }

    public boolean downloadFile(String fileName, String savePath) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            // Request file download
            out.writeUTF("DOWNLOAD");
            out.writeUTF(fileName);

            // Read response
            boolean success = in.readBoolean();
            if (!success) {
                String error = in.readUTF();
                System.err.println("Download failed: " + error);
                return false;
            }

            // Read file size
            long fileSize = in.readLong();

            // Create save directory if it doesn't exist
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // Save file
            File saveFile = new File(saveDir, fileName);
            try (FileOutputStream fileOut = new FileOutputStream(saveFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytesRead = 0;
                
                while (totalBytesRead < fileSize && 
                       (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error downloading file: " + e.getMessage());
            return false;
        }
    }
} 