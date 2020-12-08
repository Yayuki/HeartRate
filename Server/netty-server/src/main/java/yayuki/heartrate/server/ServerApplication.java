package yayuki.heartrate.server;

public class ServerApplication {
    public static void main(String[] args) {
        NettyServer server = new NettyServer();
        server.start(10600);
    }
}
