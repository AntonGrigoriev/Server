package Chat.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import Chat.models.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLHistory {

    private static final String STORAGE_LOCATION = System.getProperty("user.home") +  File.separator + "history.xml";

    public static synchronized boolean doesStorageExist() {
        File file = new File(STORAGE_LOCATION);
        return file.exists();
    }

    public static synchronized void createStorage() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("messages");
        doc.appendChild(rootElement);

        Transformer transformer = getTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
        transformer.transform(source, result);
    }

    private static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

    public static synchronized List<Message> getSubMessagesByIndex(int index) throws ParserConfigurationException, SAXException, IOException {
        List<Message> tasks = new ArrayList<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        NodeList taskList = root.getElementsByTagName("message");
        for (int i = index; i < taskList.getLength(); i++) {
            Message message = new Message();
            Element taskElement = (Element) taskList.item(i);
            message.setId(taskElement.getAttribute("id"));
            message.setClientId(taskElement.getElementsByTagName("clientId").item(0).getTextContent());
            message.setName(taskElement.getElementsByTagName("name").item(0).getTextContent());
            message.setMessage(taskElement.getElementsByTagName("text").item(0).getTextContent());
            message.setTime(taskElement.getElementsByTagName("time").item(0).getTextContent());
            message.setInfo(taskElement.getElementsByTagName("info").item(0).getTextContent());
            tasks.add(message);
        }
        return tasks;
    }

    public static synchronized void addData(Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        Element taskElement = document.createElement("message");
        root.appendChild(taskElement);

        taskElement.setAttribute("id", message.getId());

        Element clientId = document.createElement("clientId");
        clientId.appendChild(document.createTextNode(message.getClientId()));
        taskElement.appendChild(clientId);

        Element name = document.createElement("name");
        name.appendChild(document.createTextNode(message.getName()));
        taskElement.appendChild(name);

        Element text = document.createElement("text");
        text.appendChild(document.createTextNode(message.getMessage()));
        taskElement.appendChild(text);

        Element time = document.createElement("time");
        time.appendChild(document.createTextNode(message.getTime()));
        taskElement.appendChild(time);

        Element info = document.createElement("info");
        info.appendChild(document.createTextNode(message.getInfo()));
        taskElement.appendChild(info);

        DOMSource source = new DOMSource(document);

        Transformer transformer = getTransformer();

        StreamResult result = new StreamResult(STORAGE_LOCATION);
        transformer.transform(source, result);
    }

    public static synchronized int getStorageSize() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        return root.getElementsByTagName("message").getLength();
    }

}
