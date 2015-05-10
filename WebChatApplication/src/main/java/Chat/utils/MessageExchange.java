package Chat.utils;

import Chat.models.Message;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.List;

public class MessageExchange {

    private static JSONParser jsonParser = new JSONParser();

    public static String getToken(int index) {
        Integer number = index * 8 + 11;
        return "TN" + number + "EN";
    }

    public static int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public static Message getClientMessage(String data) throws ParseException {
        Message message = new Message();
        JSONObject messageJsonObject = (JSONObject) jsonParser.parse(data.trim());
        message.setClientId((String) messageJsonObject.get("cId"));
        message.setId((String) messageJsonObject.get("id"));
        message.setTime((String) messageJsonObject.get("time"));
        message.setName((String) messageJsonObject.get("name"));
        message.setMessage((String) messageJsonObject.get("message"));
        message.setInfo((String) messageJsonObject.get("info"));
        return message;
    }

}
