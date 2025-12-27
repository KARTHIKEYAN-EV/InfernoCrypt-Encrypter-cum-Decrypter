package com.inferno.crypto.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class AppConfig {

    private Properties properties = new Properties();

    public void loadConfig(String configFile) throws Exception {
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public void saveConfig() throws Exception {
        try (FileOutputStream fos = new FileOutputStream("app.properties")) {
            properties.store(fos, "Application Configuration");
        }
    }

    public String getDefaultAlgorithm() {
        return properties.getProperty("default.algorithm", "AES");
    }

    public int getDefaultKeySize() {
        return Integer.parseInt(properties.getProperty("default.keysize", "128"));
    }

    public String getDefaultMode() {
        return properties.getProperty("default.mode", "GCM");
    }
}
