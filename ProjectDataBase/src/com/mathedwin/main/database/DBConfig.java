package com.mathedwin.main.database;

import lombok.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class DBConfig {
    public static final Logger LOGGER = Logger.getLogger(DBConfig.class.getName());
    private String URL;
    private String USER;
    private String PASSWORD;

    public void loadProperties() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("database.properties")) {
            props.load(fis);
            setURL(props.getProperty("db.url"));
            setUSER(props.getProperty("db.user"));
            setPASSWORD(props.getProperty("db.password"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar el archivo properties", e);
        }
    }
}
