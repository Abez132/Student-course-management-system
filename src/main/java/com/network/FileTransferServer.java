package com.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class FileTransferServer {
    private static final int PORT = 5000;
    private static final int MAX_CONNECTIONS = 10;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private volatile boolean running;

    public FileTransferServer() {
        executorService = Executors.newFixedThreadPool(MAX_CONNECTIONS);
        running = true;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("File transfer server started on port " + PORT);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.execute(new ClientHandler(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public List<String> listFiles() {
        List<String> files = new ArrayList<>();
        File uploadsDir = new File("uploads");
        if (uploadsDir.exists() && uploadsDir.isDirectory()) {
            File[] fileList = uploadsDir.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (file.isFile()) {
                        files.add(file.getName());
                    }
                }
            }
        }
        return files;
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                 DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

                // Read file name
                String fileName = in.readUTF();
                File file = new File("uploads/" + fileName);
                
                // Create uploads directory if it doesn't exist
                file.getParentFile().mkdirs();

                // Read file size
                long fileSize = in.readLong();
                
                // Read and write file data
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalBytesRead = 0;
                    
                    while (totalBytesRead < fileSize && 
                           (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                        fileOut.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                    }
                }

                // Send success response
                out.writeBoolean(true);
                out.writeUTF("File uploaded successfully");

            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
                try {
                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                    out.writeBoolean(false);
                    out.writeUTF("Error uploading file: " + e.getMessage());
                } catch (IOException ex) {
                    System.err.println("Error sending error response: " + ex.getMessage());
                }
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
} 