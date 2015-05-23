package Chat.servlets;

import Chat.dao.MessageDAO;
import Chat.dao.MessageDAOImplement;
import Chat.models.Message;
import Chat.utils.MessageExchange;

import org.xml.sax.SAXException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.log4j.Logger;

@WebServlet("/chat")
public class MessageServlet extends HttpServlet {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static Logger logger = Logger.getLogger(MessageServlet.class.getName());
    private static MessageDAO messageDAO;

    @Override
    public void init() throws ServletException {
        try {
            messageDAO = new MessageDAOImplement();
            List<Message> history = messageDAO.select(0);
            for (Message message:history){
                System.out.println(message.getTime() + " " + message.getName() + " : " + message.getMessage() + "\r\n");
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("GET");
        String token = request.getParameter("token");
        logger.info("Token: " + token);
        try {
            if (token != null && !"".equals(token)) {
                int index = MessageExchange.getIndex(token);
                logger.info("Index: " + index);
                String tasks;
                if (index == messageDAO.getHistorySize()){
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    logger.info("Status: " + HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
                tasks = formResponse(index);
                logger.info("Response: " + tasks);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(tasks);
                out.flush();
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                logger.info("Status: " + HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SAXException | ParserConfigurationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("POST");
        try {
            Message message = MessageExchange.getClientMessage(request.getReader().readLine());
            message.setTime(dateFormat.format(new Date()));
            logger.info(message.toString());
            System.out.println("Get message:\r\n");
            System.out.println(message.getTime() + " " + message.getName() + " : " + message.getMessage() + "\r\n");
            messageDAO.add(message);
            response.setStatus(HttpServletResponse.SC_OK);
            logger.info("Status: " + HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("PUT");
        try {
            Message message = MessageExchange.getClientMessage(request.getReader().readLine());
            message.setTime(dateFormat.format(new Date()));
            logger.info(message.toString());
            System.out.println("Get PUT request:\r\n");
            System.out.println(message.getTime() + " " + message.getName() + " : " + message.getMessage() + "\r\n");
            messageDAO.add(message);
            response.setStatus(HttpServletResponse.SC_OK);
            logger.info("Status: " + HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("DELETE");
        try {
            Message message = MessageExchange.getClientMessage(request.getReader().readLine());
            message.setTime(dateFormat.format(new Date()));
            logger.info(message.toString());
            System.out.println("Get DELETE request:\r\n");
            System.out.println(message.getTime() + " " + message.getName() + " : " + message.getMessage() + "\r\n");
            messageDAO.add(message);
            response.setStatus(HttpServletResponse.SC_OK);
            logger.info("Status: " + HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error(e);
        }
    }

    @SuppressWarnings("unchecked")
    private String formResponse(int index) throws SAXException, IOException, ParserConfigurationException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messages", messageDAO.select(index));
        jsonObject.put("token", MessageExchange.getToken(messageDAO.getHistorySize()));
        return jsonObject.toJSONString();
    }

}
