import static java.lang.foreign.ValueLayout.*;

void main() throws Throwable {
    String iface = "en0";
    long BIOCSETIF = 0x8020426CL;
    long BIOCIMMEDIATE = 0x80044270L;
    long BIOCGBLEN = 0x40044266L;

    Linker linker = Linker.nativeLinker();
    SymbolLookup libc = linker.defaultLookup();
    MethodHandle open = linker.downcallHandle(libc.find("open").get(), FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT), Linker.Option.firstVariadicArg(2));
    MethodHandle ioctl = linker.downcallHandle(libc.find("ioctl").get(), FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_LONG, ADDRESS), Linker.Option.firstVariadicArg(2));
    MethodHandle read = linker.downcallHandle(libc.find("read").get(), FunctionDescriptor.of(JAVA_LONG, JAVA_INT, ADDRESS, JAVA_LONG));
    Arena arena = Arena.ofAuto();

    int fd = -1;
    for (int i = 0; i < 256 && fd < 0; i++) {
        fd = (int) open.invoke(arena.allocateFrom("/dev/bpf" + i), 0);
    }
    if (fd < 0) {
        IO.println("Не удалось открыть /dev/bpf — запустите через sudo");
        return;
    }

    MemorySegment ifreq = arena.allocate(32);
    ifreq.setString(0, iface);
    ioctl.invoke(fd, BIOCSETIF, ifreq);
    ioctl.invoke(fd, BIOCIMMEDIATE, arena.allocateFrom(JAVA_INT, 1));
    MemorySegment size = arena.allocate(JAVA_INT);
    ioctl.invoke(fd, BIOCGBLEN, size);
    MemorySegment buffer = arena.allocate(size.get(JAVA_INT, 0));

    while (true) {
        long n = (long) read.invoke(fd, buffer, buffer.byteSize());
        IO.println(n + " байт: " + HexFormat.of().formatHex(buffer.asSlice(0, Math.min(n, 64)).toArray(JAVA_BYTE)));
    }
}
