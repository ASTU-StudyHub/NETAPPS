void main() {
    String network = "192.168.1.";
    int[] ports = {21, 22, 23, 53, 80, 139, 443, 445, 3389, 8080};

    IO.println("Сканирование " + network + "0/24");
    for (int i = 1; i <= 254; i++) {
        String ip = network + i;
        new Thread(() -> scan(ip, ports)).start();
    }
}

void scan(String ip, int[] ports) {
    boolean refused = false;
    List<Integer> open = new ArrayList<>();
    for (int port : ports) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ip, port), 1500);
            open.add(port);
            socket.close();
        } catch (ConnectException e) {
            refused = true;
        } catch (IOException e) {}
    }
    if (!open.isEmpty() || refused) IO.println(ip + " открытые порты: " + open);
}
