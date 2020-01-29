import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class LoginInterface {
	NomadAdvisor nomadAdvisor;

    @FXML private AnchorPane primaryPane;

    @FXML private Separator separator;

    // Login form
    @FXML private Text loginNotificationMsg;
    @FXML private Label logLablel;
    @FXML private Label emailLabel;
    @FXML private TextField emailField;
    @FXML private Label pwdLabel;
    @FXML private PasswordField pwdField;
    @FXML private Button logButton;

    // Reg Label
    @FXML private Text regNotificationMsg;
    @FXML private Label regLabel;
    @FXML private Label nameRegLabel;
    @FXML private TextField nameField;
    @FXML private Label surnameLabel;
    @FXML private TextField surnameField;
    @FXML private Label ageLabel;
    @FXML private TextField ageField;
    @FXML private Label usernameLabel;
    @FXML private TextField usernameField;
    @FXML private Label emailRegLabel;
    @FXML private TextField emailRegField;
    @FXML private Label pwdRegLabel;
    @FXML private TextField pwdRegField;
    @FXML private Button regButton;

    @FXML void login(ActionEvent event) {
    	User user = new User();
    	user.setEmail(emailField.getText().trim());
    	user.setPassword(Utils.cryptPwd(pwdField.getText()));
    	StringBuilder msg = new StringBuilder();
    	user = NomadHandler.readUser(user, msg);
    	loginNotificationMsg.setText(msg.toString());
    	// Check if everything is ok
    	if(loginNotificationMsg.getText().equals("Success!")) {
    		// Clean the login fields
    		clearLogFields();
    		clearNotificationMessages();
    		// Switch to CityInterface if is a customer or to EmployeeInterface if is a employee
    		nomadAdvisor.setUser(user);
    		System.out.println("role: "+user.getRole());
    		if(user.getRole().equals("customer"))
    			nomadAdvisor.changeScene("cityInterface");
    		if(user.getRole().equals("employee"))
    			nomadAdvisor.changeScene("employeeInterface");
    	}	
    }

    @FXML void registration(ActionEvent event) {
    	// takes values from the reg form
    	if(!(ageField.getText().matches("^[0-9]+$"))) {
    		System.out.println("Age not integer");
    		regNotificationMsg.setText("Unexpected value for Age field");
    	}
    	else {
    		int age = Integer.parseInt(ageField.getText().trim());
	    	Customer customer = new Customer(nameField.getText().trim(), surnameField.getText().trim(), 
	    			emailRegField.getText().trim(), Utils.cryptPwd(pwdRegField.getText()),usernameField.getText().trim(),age,null);
	    	System.out.println("Registration form fields:"+" name: "+ nameField.getText().trim()+" surname: "+
	    			surnameField.getText().trim()+"age: "+ageField.getText().trim()+" email: "+emailRegField.getText().trim() +
	    			 " pwd crypt: "+Utils.cryptPwd(pwdRegField.getText())+" pwd: "+pwdRegField.getText()+" username: "+usernameField.getText().trim());
	    	regNotificationMsg.setText(NomadHandler.createCustomer(customer));
	    	if(regNotificationMsg.getText().equals("Success!"))
	    		clearRegFields();
    	}
    }
    
    private void clearRegFields() {
    	nameField.clear();
    	surnameField.clear();
    	ageField.clear();
    	emailRegField.clear();
    	pwdRegField.clear();
    	usernameField.clear();
    }
    
    private void clearLogFields() {
    	emailField.clear();
    	pwdField.clear();
    }
    
    private void clearNotificationMessages() {
    	regNotificationMsg.setText("");
    	loginNotificationMsg.setText("");
    }
    
    public void setNomadAdvisor(NomadAdvisor nomadAdvisor) {
    	this.nomadAdvisor = nomadAdvisor;
    }

}

