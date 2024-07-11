package org.mathedwin.softdev.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mathedwin.softdev.database.DBConnection;
import org.mathedwin.softdev.exceptions.DatabaseException;
import org.mathedwin.softdev.model.Message;
import org.mathedwin.softdev.repository.IMessageService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mathedwin.softdev.database.queries.MessagesQueries.*;
import static org.mathedwin.softdev.utils.MessageUtils.*;

public class MessageService implements IMessageService {

    public static final Logger LOGGER = LogManager.getLogger(MessageService.class);
    private final DBConnection dbConnection;

    private static final int DEFAULT_LAST_MESSAGES = 10;

    public MessageService() {
        this.dbConnection = new DBConnection();
    }

    @Override
    public void saveMessage(Message message) {

        validateMessage(message);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_MESSAGE_QUERY, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setInt(1, message.getUserId());
            statement.setString(2, message.getContent());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setId(generatedKeys.getInt(1));
                } else {
                    throw new DatabaseException("Error saving the message in database.");
                }
            }
            Message savedMessage = getMessageById(message.getId()).orElseThrow();
            LOGGER.info("The message has been saved in the database: {}.", savedMessage);

        } catch (SQLException exception) {
            String messageError = String.format("Error saving the message: %s.", message);
            LOGGER.error(messageError, exception);
            throw new DatabaseException("Error saving message, no ID obtained.");
        }
    }


    @Override
    public Optional<Message> getMessageById(int id) {

        validateMessageId(id);

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_MESSAGE_BY_ID)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapResultToMessage(resultSet));
            }
        } catch (SQLException exception) {
            String messageError = String.format("Error getting message with ID: %d", id);
            LOGGER.error(messageError, exception);
            throw new DatabaseException(messageError, exception);
        }

    }

    @Override
    public List<Message> getLastMessages() {

        List<Message> messages = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_LAST_MESSAGES)) {

            statement.setInt(1, DEFAULT_LAST_MESSAGES);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Message message = mapResultToMessage(resultSet);
                    messages.add(message);
                }
            }

        } catch (SQLException exception) {
            String messageError = "Error retrieving messages.";
            LOGGER.error(messageError, exception);
            throw new DatabaseException(messageError, exception);
        }

        return messages;
    }
}
