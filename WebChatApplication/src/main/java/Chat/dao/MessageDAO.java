package Chat.dao;

import Chat.models.Message;
import java.util.List;

public interface MessageDAO {

    void add(Message message);

    List<Message> select(int index);

    int getHistorySize();

}
