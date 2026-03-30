package com;

import javax.swing.SwingUtilities;

import com.network.FileTransferServer;

public class App {
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;
    private static FileTransferServer fileServer;

    public static void main(String[] args) {
        // Start servers in background thread
        new Thread(() -> {
            try {
                startServers();
            } catch (Exception e) {
                System.err.println("Failed to start servers: " + e.getMessage());
                System.exit(1);
            }
        }).start();
        
        
        SwingUtilities.invokeLater(() -> new LogIn());
    }

    private static void startServers() throws Exception {
        System.out.println("Starting File Transfer Server...");
        int retryCount = 0;
        boolean fileServerStarted = false;

        while (!fileServerStarted && retryCount < MAX_RETRIES) {
            try {
                fileServer = new FileTransferServer();
                fileServer.start();
                fileServerStarted = true;
                System.out.println("File Transfer Server started on port 8888");
            } catch (Exception e) {
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    System.out.println("Failed to start File Transfer Server, retrying in " + (RETRY_DELAY_MS/1000) + " seconds...");
                    Thread.sleep(RETRY_DELAY_MS);
                } else {
                    throw new Exception("Failed to start File Transfer Server after " + MAX_RETRIES + " attempts: " + e.getMessage());
                }
            }
        }

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down servers...");
            if (fileServer != null) {
                try {
                    fileServer.stop();
                } catch (Exception e) {
                    System.err.println("Error stopping file server: " + e.getMessage());
                }
            }
        }));
    }
}
