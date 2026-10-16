void main() throws Exception {
    ServerSocket server = new ServerSocket(5050);
    while (true) {
        Socket conn = server.accept();
        IO.println(conn.getRemoteSocketAddress() + " " + new String(conn.getInputStream().readAllBytes()));
        conn.close();
    }
}
