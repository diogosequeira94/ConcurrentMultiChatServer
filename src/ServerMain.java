public class ServerMain {

    public static void main(String[] args) {

        int port = 8080;
        Servidor server = new Servidor(port);
        server.start();

    }

}
