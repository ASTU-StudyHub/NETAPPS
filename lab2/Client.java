void main() throws Exception {
    String text = IO.readln("Строка: ");
    Socket socket = new Socket("127.0.0.1", 5050);
    socket.getOutputStream().write(text.getBytes());
    socket.close();
}
