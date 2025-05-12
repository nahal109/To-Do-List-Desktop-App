package tm;

public class ToDoTM {
    private String id;
    private String description;
    private String user_id;

    public ToDoTM() {

    }

    public ToDoTM(String user_id, String description, String id) {
        this.user_id = user_id;
        this.description = description;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String toString() {
        return description;
    }
}
