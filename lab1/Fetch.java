void main() throws Exception {
    String host = "example.com";

    IO.println("HTTP: " + new String(URI.create("http://" + host).toURL().openStream().readAllBytes()));
    IO.println("FILE: " + new String(Path.of("Fetch.java").toUri().toURL().openStream().readAllBytes()));
    IO.println("DNS: " + Arrays.toString(InetAddress.getAllByName(host)));
}
