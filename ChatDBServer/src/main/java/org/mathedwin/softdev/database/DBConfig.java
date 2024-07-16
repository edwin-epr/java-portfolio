package org.mathedwin.softdev.database;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class DBConfig {
    public static final Logger   LOGGER = LogManager.getLogger(DBConfig.class);
    private String URL;
    private String USER;
    private String PASSWORD;

    public void loadProperties() {
        Properties properties = new Properties();
        try (InputStream fileWithProperties = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            properties.load(fileWithProperties);
            setURL(properties.getProperty("db.url"));
            setUSER(properties.getProperty("db.user"));
            setPASSWORD(properties.getProperty("db.password"));
        } catch (IOException exception) {
            LOGGER.error("Error loading properties.", exception);
        }
        LOGGER.info("Properties successfully loaded!");
    }
}
