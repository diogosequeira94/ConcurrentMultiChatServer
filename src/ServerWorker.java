import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ServerWorker extends Thread {

    private Socket clientSocket;
    private Servidor server;
    private OutputStream outputStream;
    private int serverID;
    private String name;

    public ServerWorker (Servidor server, Socket clientSocket, int serverID){
        this.server = server;
        this.clientSocket = clientSocket;
        this.serverID = serverID;
    }

    @Override
    public void run() {
        try {

            //We will need the input name for the client
            outputStream = clientSocket.getOutputStream();
            String name = "\nWhat is your name?\n";
            outputStream.write(name.getBytes());

            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader scanner = new BufferedReader(new InputStreamReader(inputStream));
            this.name = scanner.readLine();

            clientHandler();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clientHandler() throws IOException, InterruptedException {

        //OutputStream so the client can read
        this.outputStream = clientSocket.getOutputStream();


        //We also want an Input from the Client
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        List<ServerWorker> workerList = server.getWorkerList();

        while((line = reader.readLine()) != null){

            String[] words = line.split(" ");

            //Se o comando for Quit enviar mensagem

            if("/quit".equalsIgnoreCase(line)){

                for (ServerWorker worker : workerList) {
                    if(!worker.name.equalsIgnoreCase(name)){
                        worker.goodByeMessage(name);

                    }
                }

                break;

            } else if (words[0].startsWith("/alias")){

                String currentName = this.name;
                setNewName(words[1]);
                String newName = this.name;

                for (ServerWorker worker : workerList){
                   worker.shoutNewName(currentName, newName);
                }

            } else if("/list".equalsIgnoreCase(line)){
                for (ServerWorker worker : workerList) {
                    if(worker.name.equalsIgnoreCase(name)){
                        worker.printList();

                    }
                }

            }
            //Iterar pelo array e dizer que todos os workers vao ver esta mensagem:

            for(ServerWorker worker : workerList){
                if(!line.startsWith("/")){

                    if(!worker.name.equalsIgnoreCase(name) && !worker.clientSocket.isClosed()){

                        worker.sendMessage(line,name);
                    }
                }



            }

        }


        System.out.println("Closing connection with " + name);
        //We need to remove the Client from the array
        workerList.remove(this);
        clientSocket.close();

    }

    private void printList() throws IOException{

        List<ServerWorker> workerList = server.getWorkerList();
        StringBuilder workers = new StringBuilder();

        for(ServerWorker worker : workerList){
            workers.append(worker.name + "\n");
        }

        workers.insert(0, "List of Online users: \n");
        outputStream.write(workers.toString().getBytes());



    }


        private void shoutNewName(String oldName, String newName) throws IOException{

            String shout = "*****" + oldName + " changed its name to " + newName + "*****\n";

            outputStream.write(shout.getBytes());

        }


        private void setNewName(String newName) {

            this.name = newName;
        }



        private void goodByeMessage(String name) throws IOException{
            String goodBye = "*****" + name + " has left the chat*****\n";

            outputStream.write(goodBye.getBytes());

        }


        private void sendMessage(String message, String name) throws IOException {

            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

            if(message != null){

                message = timeStamp + " " + name + ": " + message + "\n";

                outputStream.write(message.getBytes());

            }


        }

}
