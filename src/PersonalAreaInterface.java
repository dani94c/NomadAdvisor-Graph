import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PersonalAreaInterface {

    private NomadAdvisor nomadAdvisor;

    //popup SuggestedNomadInterface
    private SuggestedNomadsInterface suggNomads;
    private FXMLLoader fxmlSuggNomads;
    private Scene suggNomadsScene;
    private Stage parentPersonalAreaStage;

    @FXML
    private Button logoutButton;
    @FXML
    private Button backButton;
    @FXML
    private CheckBox tempCheckBox;
    @FXML
    private Label nameLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label surnameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Separator upperSeparator;
    @FXML
    private Label preferencesLabel;
    @FXML
    private Separator lowerSeparator;
    @FXML
    private CheckBox airCheckBox;
    @FXML
    private CheckBox qualityLifeCheckBox;
    @FXML
    private CheckBox foreignersCheckBox;
    @FXML
    private CheckBox healthcareCheckBox;
    @FXML
    private CheckBox nightlifeCheckBox;
    @FXML
    private CheckBox costCheckBox;
    @FXML
    private CheckBox safetyCheckBox;
    @FXML
    private CheckBox walkabilityCheckBox;
    @FXML
    private CheckBox wifiCheckBox;
    @FXML
    private CheckBox englishCheckBox;
    @FXML
    private Button saveButton;
    @FXML
    private Label emailLabel;
    @FXML
    private Label outcomeLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Button updateAgeButton;
    @FXML
    private Button suggestedNomadButton;
    @FXML
    private TextField ageField;

    @FXML
        // Comes back to the city interface
    void comeBack(ActionEvent event) {
        clearAll();
        nomadAdvisor.changeScene("cityInterface");
    }

    @FXML
        // Update button pressed, the age attribute of the customer is updated in the database
    void updateAge(ActionEvent event) {
        Customer customer = (Customer) nomadAdvisor.getUser();
        if (ageField.getText().equals(""))
            outcomeLabel.setText("You have to insert an age value in order to update it");
        else if (ageField.getText().matches("^(?:1[01][0-9]|120|1[0-9]|[2-9][0-9])$") && (Integer.parseInt(ageField.getText()) != customer.getAge())) {
            outcomeLabel.setText(NomadHandler.updateAge(customer, Integer.parseInt(ageField.getText())));
        }
    }

    // Retrieve the instance of the stage related to this interface controller, handled by NomadAdvisor,
    //in order to be able to set it as the owner/parent of the popups interfaces.
    public void setParentStage(Stage stage) {
        this.parentPersonalAreaStage = stage;
    }

    @FXML
        // The SuggestedNomad popup will be showed, the interface stage is initialized
    void suggestedNomad(ActionEvent event) {
        Stage popupStage = new Stage();
        popupStage.setScene(suggNomadsScene);
        suggNomads.initInterface();
        popupStage.initOwner(parentPersonalAreaStage);
        /*
        popupStage.setOnCloseRequest((WindowEvent we) -> {

        });
         */
        popupStage.show();
    }

    @FXML
        // Logout and returns to login interface
    void logout(ActionEvent event) {
        clearAll();
        nomadAdvisor.changeScene("loginInterface");
    }

    @FXML
        // Saves the preferences added from the customer
    void savePreferences(ActionEvent event) {
        List<String> preferences = this.getPreferences();
        Customer customer = (Customer) nomadAdvisor.getUser();
        outcomeLabel.setText(NomadHandler.updatePreferences(customer, preferences));
    }

    // Sets the username label with the username of the logged user
    private void setUsernameLabel(String username) {
        if(username == null)
            usernameLabel.setText("");
        else
            usernameLabel.setText(username);
    }

    // Sets the name label with the name of the logged user
    private void setNameLabel(String name) {
        if(name == null)
            nameLabel.setText("Name: not available");
        else
            nameLabel.setText("Name: " + name);
    }

    // Sets the surname label with the surname of the logged user
    private void setSurnameLabel(String surname) {
        if(surname == null)
            surnameLabel.setText("Surname: not available");
        else
            surnameLabel.setText("Surname: " + surname);
    }

    // Sets the email label with the email of the logged user
    private void setEmailLabel(String email) {
        if (email == null)
            emailLabel.setText("Email: not available");
        else
            emailLabel.setText("Email: " + email);
    }

    private void setAgeLabel(int age) {
        if (age == 0) {
            outcomeLabel.setText("The age attribute is not available, please insert your age now");
            ageField.setText("");
        } else
            ageField.setText(String.valueOf(age));
    }

    // Select the checkbox fields depending on the preferences of the logged customer
    private void setPreferences(List<String> preferences) {
        if (preferences != null) {
            if (preferences.contains(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE)))
                tempCheckBox.setSelected(true);
            if (preferences.contains(Utils.cityAttributes.get(Utils.cityNames.AIR_QUALITY)))
                airCheckBox.setSelected(true);
            if (preferences.contains(Utils.cityAttributes.get(Utils.cityNames.QUALITY_LIFE)))
                qualityLifeCheckBox.setSelected(true);
            if (preferences.contains(Utils.cityAttributes.get(Utils.cityNames.FOREIGNERS)))
                foreignersCheckBox.setSelected(true);
            if (preferences.contains(Utils.cityAttributes.get(Utils.cityNames.HEALTHCARE)))
                healthcareCheckBox.setSelected(true);
            if (preferences.contains(Utils.cityAttributes.get(Utils.cityNames.NIGHTLIFE)))
                nightlifeCheckBox.setSelected(true);
            if(preferences.contains(Utils.cityAttributes.get(Utils.cityNames.COST))) costCheckBox.setSelected(true);
            if(preferences.contains(Utils.cityAttributes.get(Utils.cityNames.SAFETY))) safetyCheckBox.setSelected(true);
            if(preferences.contains(Utils.cityAttributes.get(Utils.cityNames.WALKABILITY))) walkabilityCheckBox.setSelected(true);
            if(preferences.contains(Utils.cityAttributes.get(Utils.cityNames.WIFI))) wifiCheckBox.setSelected(true);
            if(preferences.contains(Utils.cityAttributes.get(Utils.cityNames.ENGLISH))) englishCheckBox.setSelected(true);
        }
    }

    // Creating a list of preferences depending on what the customer selects on the interface
    private List<String> getPreferences() {
        List<String> preferences = new ArrayList<>();
        if(tempCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE));
        if(airCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.AIR_QUALITY));
        if(qualityLifeCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.QUALITY_LIFE));
        if (foreignersCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.FOREIGNERS));
        if (healthcareCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.HEALTHCARE));
        if (nightlifeCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.NIGHTLIFE));
        if (costCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.COST));
        if (safetyCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.SAFETY));
        if (walkabilityCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.WALKABILITY));
        if (wifiCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.WIFI));
        if (englishCheckBox.isSelected()) preferences.add(Utils.cityAttributes.get(Utils.cityNames.ENGLISH));
        return preferences;
    }

    private void setPopup() {
        try {
            fxmlSuggNomads = new FXMLLoader(PersonalAreaInterface.class.getResource("resources/SuggestedNomadsInterface.fxml"));
            suggNomadsScene = new Scene(fxmlSuggNomads.load());
            suggNomads = (SuggestedNomadsInterface) fxmlSuggNomads.getController();
            suggNomads.setNomadAdvisor(nomadAdvisor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the logged Customer from NomadAdvisor and sets the fields of the interface
    public void initInterface() {
        this.setPopup();
        outcomeLabel.setText("");
        Customer customer = null;
        if (nomadAdvisor != null)
            customer = (Customer) nomadAdvisor.getUser();
        if (customer != null) {
            this.setUsernameLabel(customer.getUsername());
            this.setNameLabel(customer.getName());
            this.setSurnameLabel(customer.getSurname());
            this.setEmailLabel(customer.getEmail());
            this.setAgeLabel(customer.getAge());
            this.setPreferences(customer.getPreferences());
        }

    }

    // Sets the reference to the NomadAdvisor object
    public void setNomadAdvisor(NomadAdvisor nomadAdvisor) {
        this.nomadAdvisor = nomadAdvisor;
    }

    // Clears all the fields of the interface
    private void clearAll() {
        outcomeLabel.setText("");
        tempCheckBox.setSelected(false);
        airCheckBox.setSelected(false);
        qualityLifeCheckBox.setSelected(false);
        foreignersCheckBox.setSelected(false);
        healthcareCheckBox.setSelected(false);
        nightlifeCheckBox.setSelected(false);
        costCheckBox.setSelected(false);
        safetyCheckBox.setSelected(false);
        walkabilityCheckBox.setSelected(false);
        wifiCheckBox.setSelected(false);
        englishCheckBox.setSelected(false);
    }

}
