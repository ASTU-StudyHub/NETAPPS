Map<String, PrintWriter> clients = new LinkedHashMap<>();
Map<String, Set<String>> groups = new HashMap<>();

void main() throws Exception {
    ServerSocket server = new ServerSocket(5051);
    while (true) {
        Socket conn = server.accept();
        IO.println("Подключение " + conn.getRemoteSocketAddress());
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        PrintWriter out = new PrintWriter(conn.getOutputStream(), true);
        new Thread(() -> handle(in, out)).start();
    }
}

void handle(BufferedReader in, PrintWriter out) {
    try {
        String name = in.readLine();
        if (join(name, out)) {
            String line;
            while ((line = in.readLine()) != null) {
                route(name, line);
            }
        }
    } catch (IOException e) {
    }
    leave(out);
}

synchronized boolean join(String name, PrintWriter out) {
    if (name == null || name.isBlank() || clients.containsKey(name)) {
        out.println("Имя пустое или занято");
        return false;
    }
    clients.put(name, out);
    update();
    return true;
}

synchronized void leave(PrintWriter out) {
    out.close();
    if (clients.values().remove(out)) {
        update();
    }
}

synchronized void update() {
    String line = "В сети: " + String.join(", ", clients.keySet());
    IO.println(line);
    send(clients.keySet(), line);
}

synchronized void send(Collection<String> names, String line) {
    for (String name : names) {
        if (clients.containsKey(name)) {
            clients.get(name).println(line);
        }
    }
}

synchronized void route(String name, String line) {
    String[] parts = line.split(" ", 2);
    String to = parts[0];
    String text = parts.length == 2 ? parts[1] : "";
    if (to.equals("/group")) {
        String[] words = (text + " " + name).split(" +");
        Set<String> members = new LinkedHashSet<>(List.of(words).subList(1, words.length));
        groups.put(words[0], members);
        send(members, name + " создал группу " + words[0] + ": " + String.join(", ", members));
    } else if (to.equals("*")) {
        send(clients.keySet(), name + " всем: " + text);
    } else if (groups.containsKey(to)) {
        send(groups.get(to), name + " в " + to + ": " + text);
    } else if (clients.containsKey(to)) {
        send(List.of(to), name + " лично: " + text);
    } else {
        send(List.of(name), "Нет адресата " + to);
    }
}
