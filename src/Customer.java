import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import java.util.ArrayList;

public class Customer extends User {
    private final SimpleStringProperty username;
    private SimpleIntegerProperty age;
    private List<String> preferences;

    public Customer() {
        super();
        this.username = new SimpleStringProperty("");
        this.age = new SimpleIntegerProperty(0);
        preferences = new ArrayList<String>();
    }

    public Customer(String name, String surname, String email, String password, String username, int age, List<String> preferences) {
        super(name, surname, email, password, "customer");
        this.username = new SimpleStringProperty(username);
        this.age = new SimpleIntegerProperty(age);
        this.preferences = preferences;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public int getAge() {return age.get();}

    public SimpleIntegerProperty ageProperty() {return age;}

    public void setAge(int age) {this.age.set(age);}
}
