package com.inferno.crypto.io.file;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.io.IOException;

public class TextFileHandler implements FileHandler {

    private final String encoding;

    public TextFileHandler(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public byte[] read(File file) throws IOException {
        return Files.readString(file.toPath(), Charset.forName(encoding)).getBytes(encoding);
    }

    @Override
    public void write(File file, byte[] data) throws IOException {
        Files.writeString(file.toPath(), new String(data, encoding), Charset.forName(encoding));
    }

    @Override
    public boolean validate(File file) {
        return file.exists() && file.isFile() && file.canRead();
    }

    @Override
    public String getFileType() {
        return "TEXT";
    }

    public List<String> readLines(File file) throws Exception {
        return Files.readAllLines(file.toPath(), Charset.forName(encoding));
    }

    public void writeLines(File file, List<String> lines) throws Exception {
        Files.write(file.toPath(), lines, Charset.forName(encoding));
    }
}
