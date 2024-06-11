package com.mathedwin.main.utils;

import com.mathedwin.main.model.Message;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Objects.isNull;

public class MessageUtils {

    public static Message mapResultToMessage(ResultSet resultSet) throws SQLException {
        Message message = new Message();
        message.setId(resultSet.getInt("id"));
        message.setUserId(resultSet.getInt("user_id"));
        message.setContent(resultSet.getString("content"));
        message.setTimestamp(resultSet.getTimestamp("timestamp"));
        return message;
    }

    public static void validateMessage(Message message) {
        validateContent(message.getContent());
        validateUserId(message.getUserId());
    }

    private static void validateContent(String content) {
        if (isNull(content) || content.isEmpty()) {
            throw new IllegalArgumentException("The content of the message cannot be null or empty");
        }
    }

    private static void validateUserId(int userId) {
        if (isInvalidId(userId)) {
            throw new IllegalArgumentException("The id is invalid");
        }
    }

    public static void validateMessageId(int messageId) {
        if (isInvalidId(messageId)) {
            throw new IllegalArgumentException("The id: " + messageId + " is invalid");
        }
    }

    public static boolean isInvalidId(int userId) {
        return userId <= 0;
    }
}
