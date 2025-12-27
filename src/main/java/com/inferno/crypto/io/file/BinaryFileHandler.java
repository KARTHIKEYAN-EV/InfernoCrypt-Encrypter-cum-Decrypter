package com.inferno.crypto.io.file;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class BinaryFileHandler implements FileHandler {

    private final int bufferSize;

    public BinaryFileHandler(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public byte[] read(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    @Override
    public void write(File file, byte[] data) throws IOException {
        Files.write(file.toPath(), data);
    }

    @Override
    public boolean validate(File file) {
        return file.exists() && file.isFile();
    }

    @Override
    public String getFileType() {
        return "BINARY";
    }

    public List<byte[]> readChunks(File file, int chunkSize) throws Exception {
        byte[] data = read(file);
        List<byte[]> chunks = new ArrayList<>();

        for (int i = 0; i < data.length; i += chunkSize) {
            int end = Math.min(data.length, i + chunkSize);
            byte[] chunk = new byte[end - i];
            System.arraycopy(data, i, chunk, 0, chunk.length);
            chunks.add(chunk);
        }
        return chunks;
    }

    public void writeChunks(File file, List<byte[]> chunks) throws Exception {
        int totalSize = chunks.stream().mapToInt(c -> c.length).sum();
        byte[] combined = new byte[totalSize];

        int pos = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, combined, pos, chunk.length);
            pos += chunk.length;
        }
        write(file, combined);
    }
}
