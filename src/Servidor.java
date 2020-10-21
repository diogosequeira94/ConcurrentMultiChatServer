import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Servidor extends Thread {

    private int port;
    //Tenho de ter uma lista de Workers
    private Vector<ServerWorker> workerList = new Vector<>();
    private OutputStream outputStream;
    private volatile int workerID;

    public Servidor(int port) {
        this.port = port;
    }

    public List<ServerWorker> getWorkerList() {
        return workerList;
    }

    public int getWorkerID() {
        return workerID;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            //The accept method creates the connection between the server and the client
            while (true) {
                System.out.println("Waiting to accept client connection...");
                Socket clientSocket = serverSocket.accept();
                workerID++;
                System.out.println("Connection Established\n");

                //We need to create another thread each time we have a client
                ServerWorker serverWorker = new ServerWorker(this, clientSocket, workerID);
                serverWorker.start();

                //Each time we establish a connection we create a user, an that user must be stored in a array of Users or Connections
                workerList.add(serverWorker);
            }
            //telnet localhost 8080 to check
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
