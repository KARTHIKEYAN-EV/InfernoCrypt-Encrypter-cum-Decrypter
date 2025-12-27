package com.inferno.crypto.io.file;

import java.io.File;
import java.io.IOException;

public interface FileHandler {
    byte[] read(File file) throws IOException;
    void write(File file, byte[] data) throws IOException;
    boolean validate(File file);
    String getFileType();
}
