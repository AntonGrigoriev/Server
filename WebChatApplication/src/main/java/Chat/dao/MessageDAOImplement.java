package Chat.dao;

import Chat.db.ConnectionManager;
import Chat.models.Message;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageDAOImplement implements MessageDAO {

    private static Logger logger = Logger.getLogger(MessageDAOImplement.class.getName());

    @Override
    public void add(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            date = dateFormat.parse(message.getTime());
            dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO messages (id, user_id, name, text, time, info) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, message.getId());
            preparedStatement.setString(2, message.getClientId());
            preparedStatement.setString(3, message.getName());
            preparedStatement.setString(4, message.getMessage());
            preparedStatement.setString(5, dateFormat.format(date));
            preparedStatement.setString(6, message.getInfo());
            preparedStatement.executeUpdate();
        } catch (SQLException | ParseException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    @Override
    public List<Message> select(int index) {
        List<Message> messages = new ArrayList<>();
        Message message = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM messages WHERE token > " + index);
            while (resultSet.next()) {
                date = dateFormat.parse(resultSet.getString("time"));
                dateFormat.applyPattern("dd-MM-yyyy HH:mm:ss");
                message = new Message();
                message.setClientId(resultSet.getString("user_id"));
                message.setId(resultSet.getString("id"));
                message.setTime(dateFormat.format(date));
                message.setName(resultSet.getString("name"));
                message.setMessage(resultSet.getString("text"));
                message.setInfo(resultSet.getString("info"));
                messages.add(message);
            }
        } catch (SQLException | ParseException e) {
            logger.error(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
        return messages;
    }

    @Override
    public int getHistorySize() {
        int count = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM messages");
            while (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
        System.out.println(count);
        return count;
    }

}
