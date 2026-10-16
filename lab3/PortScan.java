void main() {
    String host = "127.0.0.1";

    for (int port = 1; port <= 65535; port++) {
        try {
            new Socket(host, port).close();
            IO.println("Открыт порт " + port);
        } catch (IOException e) {}
    }
}
