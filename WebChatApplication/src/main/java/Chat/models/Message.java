package Chat.models;

public class Message {

    private String id;
    private String cId;
    private String name;
    private String message;
    private String time;
    private String info;

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String value) {
        this.info = value;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String value) {
        this.time = value;
    }

    public String getClientId() {
        return this.cId;
    }

    public void setClientId(String value) {
        this.cId = value;
    }

    @Override
    public String toString() {
        return "{\"clientId\":\"" + this.cId + "\",\"id\":\"" + this.id + "\",\"time\":\"" + this.time
                + "\",\"name\":\"" + this.name + "\",\"message\":\"" + this.message + "\",\"info\":\"" + this.info + "\"}";
    }
}
