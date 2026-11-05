void main() throws Exception {
    Socket socket;
    try {
        String[] address = IO.readln("Сервер (IP:порт): ").strip().split(":");
        socket = new Socket(address[0], Integer.parseInt(address[1]));
    } catch (Exception e) {
        IO.println("Не удалось подключиться: нужен адрес вида IP:порт и запущенный сервер");
        return;
    }
    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    out.println(Objects.toString(IO.readln("Имя (без пробелов): "), "").strip());
    IO.println("Сообщение: <адресат> <текст>, адресат: * (всем), #группа или имя");
    IO.println("Файл: <адресат> /file <путь>   Группа: /group #группа имя1 имя2 ...");
    new Thread(() -> read(socket)).start();

    String line;
    while ((line = IO.readln()) != null) {
        String[] parts = line.split(" ", 3);
        if (parts.length == 3 && parts[1].equals("/file")) {
            Path path = Path.of(parts[2].strip());
            if (!Files.isRegularFile(path)) {
                IO.println("Нет файла " + path);
                continue;
            }
            line = parts[0] + " /file " + Base64.getEncoder().encodeToString(Files.readAllBytes(path)) + " " + path.getFileName();
        }
        out.println(line);
    }
    socket.close();
}

void read(Socket socket) {
    try {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            String[] parts = line.split(": ", 2);
            String[] file = parts[parts.length - 1].split(" ", 3);
            if (file.length == 3 && file[0].equals("/file")) {
                String name = "получено_" + file[2];
                Files.write(Path.of(name), Base64.getDecoder().decode(file[1]));
                line = parts[0] + ": файл " + name;
            }
            IO.println(line);
        }
    } catch (IOException e) {
    }
    IO.println("Соединение закрыто");
    System.exit(0);
}
