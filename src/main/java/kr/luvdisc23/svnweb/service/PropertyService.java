package kr.luvdisc23.svnweb.service;

import java.io.IOException;
import java.util.Properties;

public class PropertyService {

    private static Properties properties = new Properties();

    public static String get(String name) {
        synchronized(properties) {
            if(properties.isEmpty()) {
                try {
                    properties.load(PropertyService.class.getResourceAsStream("/applications.properties"));
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Properties not found");
                }
            }
        }
        return properties.getProperty(name, "");
    }
}
