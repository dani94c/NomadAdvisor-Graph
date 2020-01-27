import java.util.HashMap;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CityInterface {
	
	NomadAdvisor nomadAdvisor;
	City selectedCity;
	
    @FXML private AnchorPane primaryPane;
    @FXML private Label cityTitle;
    @FXML private Button logOutButton;
    @FXML private Button profileButton;
    @FXML private HBox notificationBox;
    @FXML private Text notificationMsg;

    // Search panel by city name
    @FXML private Text cityLabel;
    @FXML private TextField cityText;
    @FXML private Button cityNameButton;
    
    // Search panel by preferences
	ObservableList<String> levels = 
            FXCollections.observableArrayList (
                "","1","2","3","4","5");
	ObservableList<String> tempRange = 
            FXCollections.observableArrayList (
                "","<0","0-15","15-25",">25");
	
	// Preferences form
    @FXML private VBox preferencesBox;
    @FXML private GridPane preferencesPane;
    @FXML private Text prefTitle;
    @FXML private Text temperatureLabel;
    @FXML private ComboBox<String> temperatureBox;
    @FXML private Text qualityLifeLabel;
    @FXML private ComboBox<String> qualityLifeBox;
    @FXML private Text englishLabel;
    @FXML private ComboBox<String> englishBox;
    @FXML private Text friendlyLabel;
    @FXML private ComboBox<String> friendlyBox;
    @FXML private Text nigthlifeLabel;
    @FXML private ComboBox<String> nigthlifeBox;
    @FXML private Text costLabel;
    @FXML private TextField costField;
    @FXML private Text healthLabel;
    @FXML private ComboBox<String> healthBox;
    @FXML private Text safetyLabel;
    @FXML private ComboBox<String> safetyBox;
    @FXML private Text walkabilityLabel;
    @FXML private ComboBox<String> walkabilityBox;
    @FXML private Text airQualityLabel;
    @FXML private ComboBox<String> airQualityBox;
    @FXML private Text wifiLabel;
    @FXML private ComboBox<String> wifiBox;
    @FXML private Button cityPrefButton;
    
    // City Table
    private ObservableList<City> cityList = 
    		FXCollections.observableArrayList();
    @FXML private TableView<City> cityTable;
    @FXML private TableColumn<City, String> countryColumn;
    @FXML private TableColumn<City, String> cityColumn;
    @FXML private TableColumn<City, String> characteristicsColumn;
    @FXML private Button hotelButton;
    
    // Initialize all the interface's elements
    @FXML void initialize() {
    	initializeCityTable();
    	initializeComboBox();	
    	hotelButton.setDisable(true);
    }
    
    // Update the interface's elements
    public void initInterface() {
    	updateCityTable();
    	resetInterfaceElements();
    	notificationMsg.setText("");
    	costField.setText("");
    	hotelButton.setDisable(true);
    }
    
    // Search cities by Name
    @FXML void cityNameButton(ActionEvent event) {
    	String name = cityText.getText().trim();
    	List<City> cities = NomadHandler.getCity(name);
    	if(cities == null)
    		notificationMsg.setText("Oops, something went wrong!");
    	else {
    		hotelButton.setDisable(true);
    		cityListUpdate(cities);
    	}
    }
    
    // Search cities by attributes
    @FXML void cityPrefButton(ActionEvent event) {
    	// pref has as keys the names in the document cities except the temperature
    	HashMap<String,Integer> preferences = buildPrefMap(temperatureBox.getValue(),qualityLifeBox.getValue(), englishBox.getValue(),
    			friendlyBox.getValue(), nigthlifeBox.getValue(), costField.getText(), healthBox.getValue(), safetyBox.getValue(),
    			walkabilityBox.getValue(), airQualityBox.getValue(), wifiBox.getValue());
    	if(preferences != null) {
    		List<City> cities = NomadHandler.getCity(preferences);
        	if(cities == null)
        		notificationMsg.setText("Oops, something went wrong!");
        	else {
        		hotelButton.setDisable(true);
        		cityListUpdate(cities);
        	}
        		
    	}	
    }
    
    // Go to the customer's personal area interface
    @FXML void profileButton(ActionEvent event) {
    	nomadAdvisor.changeScene("personalAreaInterface");
    }

    // Go back to the Login interface
    @FXML void logOutButton(ActionEvent event) {
    	nomadAdvisor.changeScene("loginInterface");
    }
    
    // Search the hotels related to the selected city and go to the relative interface
    @FXML void hotelButton(ActionEvent event) {
    	// take the field selected from the car table
    	nomadAdvisor.setCity(cityTable.getSelectionModel().getSelectedItem());
        System.out.println("Selected city name: "+nomadAdvisor.getCity().getCityName());
        nomadAdvisor.changeScene("hotelInterface");
    } 
    
    // Initialize the city table
    private void initializeCityTable() {
    	// Initialize the city table
    	cityColumn.setCellValueFactory(new PropertyValueFactory<City, String>("cityName")); 
    	countryColumn.setCellValueFactory(new PropertyValueFactory<City, String>("countryName")); 
    	characteristicsColumn.setCellValueFactory(new PropertyValueFactory<City, String>("characteristics")); 
    	characteristicsColumn.setCellFactory(Utils.WRAPPING_CELL_FACTORY);

    	cityTable.setItems(cityList);
    }
    
    // Update city table
    private void updateCityTable() {
    	// Retrieve the preferences of the logged Customer and retrieve the cities more appropriate to him
    	List<String> userPreferences = ((Customer) nomadAdvisor.getUser()).getPreferences();
    	HashMap<String,Integer> pref = new HashMap<String,Integer>();
    	List<City> cities; 
    	if(userPreferences != null) {	// the Customer have set some preferences in his personal area
    		for(int i = 0; i < userPreferences.size(); i++) {
    			if(userPreferences.get(i).equals(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE))) {
    				pref.put("temp_lower",15);
    				pref.put("temp_greater", 25);
    			} else if(userPreferences.get(i).equals(Utils.cityAttributes.get(Utils.cityNames.COST)))
    				pref.put(Utils.cityAttributes.get(Utils.cityNames.COST), 2000);
    			else 
    				pref.put(userPreferences.get(i), 3);    			
    		}
    		cities = NomadHandler.getCity(pref);
    	} else {
    		// retrieve all the cities (first 30)
    		cities = NomadHandler.getCity("");
    	}
    	if(cities == null)
    		notificationMsg.setText("Oops, something went wrong!");
    	else
    		cityListUpdate(cities);
    	
    	// Add the listener on the cityTable
    	addCityTableListener();
    }
    
    public void cityListUpdate(List<City> cities) {
    	cityList.clear();
    	cityList.addAll(cities);
    }
    
    // Combobox initialization
    private void initializeComboBox() {
    	ComboBox[] cb = {wifiBox, airQualityBox, walkabilityBox, safetyBox, healthBox, 
    			nigthlifeBox, friendlyBox, englishBox, qualityLifeBox, };
    	setLevels(cb);
    	temperatureBox.getItems().removeAll(temperatureBox.getItems());
    	temperatureBox.getItems().addAll(tempRange);
    }
    
    // Set the levels of the comboboxes
    private void setLevels(ComboBox[] cb) {
    	for(int i = 0; i<cb.length; i++) {
    		cb[i].getItems().removeAll(cb[i].getItems());
    		cb[i].getItems().addAll(levels);
    	}
    }
    
    
    
    // Add a Listener to the city table, listen when a city is selected
    void addCityTableListener() {
    	// Listen when the customer select a city from city table
        cityTable.getSelectionModel().selectedIndexProperty().addListener((num) ->
        {     
        	// select an empty field
            if(cityTable.getSelectionModel().getSelectedItem() == null) {
               cityTable.getSelectionModel().clearSelection();
            } 
            else
                hotelButton.setDisable(false);            
        });
    }
    
    // return the HashMap with all the preferences and the relative values
    HashMap<String,Integer> buildPrefMap(String temp, String qLife, String eng, String friendly, String nigthLife, 
    		String cost, String health, String safety, String walk, String airQ, String wifi) {
    	HashMap<String,Integer> preferences = new HashMap<String,Integer>();
    	try {
        	if(temp != null && !temp.equals("")) {
        		if(temp.equals("<0")) {
        			preferences.put("temp_lower", -60);
        			preferences.put("temp_greater", 0);
        		} else if(temp.equals(">25")) {
        			preferences.put("temp_lower", 25);
        			preferences.put("temp_greater", 60);
        		} else {
        			String[] values = temp.split("-");
        			preferences.put("temp_lower", Integer.parseInt(values[0]));
        			preferences.put("temp_greater", Integer.parseInt(values[1]));
        		}	
        	}
        	qLife = qualityLifeBox.getValue();
        	if(qLife != null && !qLife.equals(""))
        		preferences.put(Utils.cityAttributes.get(Utils.cityNames.QUALITY_LIFE),Integer.parseInt(qLife));
        	
        	eng = englishBox.getValue();
        	if(eng != null && !eng.equals(""))
        		preferences.put(Utils.cityAttributes.get(Utils.cityNames.ENGLISH),Integer.parseInt(eng));
        	
        	friendly = wifiBox.getValue();
        	if(friendly != null && !friendly.equals(""))
        		preferences.put(Utils.cityAttributes.get(Utils.cityNames.WIFI),Integer.parseInt(friendly));
        	
        	nigthLife = friendlyBox.getValue();
        	if(nigthLife != null && !nigthLife.equals(""))
        		preferences.put(Utils.cityAttributes.get(Utils.cityNames.FOREIGNERS),Integer.parseInt(nigthLife));
        	
        	cost = nigthlifeBox.getValue();
        	if(cost != null && !cost.equals(""))
        		preferences.put(Utils.cityAttributes.get(Utils.cityNames.NIGHTLIFE),Integer.parseInt(cost));
        	
        	health = costField.getText();
        	if(health != null && !health.equals(""))
        		preferences.put(Utils.cityAttributes.get(Utils.cityNames.COST),Integer.parseInt(health));
        	
        	safety = healthBox.getValue();
        	if(safety != null && !safety.equals(""))
        		preferences.put(Utils.cityAttributes.get(Utils.cityNames.HEALTHCARE),Integer.parseInt(safety));
        	
        	walk = safetyBox.getValue();
        	if(walk != null && !walk.equals(""))
        		preferences.put(Utils.cityAttributes.get(Utils.cityNames.SAFETY),Integer.parseInt(walk));
        	
        	airQ = walkabilityBox.getValue();
        	if(airQ != null && !airQ.equals(""))
        		preferences.put(Utils.cityAttributes.get(Utils.cityNames.WALKABILITY),Integer.parseInt(airQ));
        	
        	wifi = airQualityBox.getValue();
        	if(wifi != null && !wifi.equals(""))
        		preferences.put(Utils.cityAttributes.get(Utils.cityNames.AIR_QUALITY),Integer.parseInt(wifi));
        	System.out.println("preferences: "+preferences);
        	return preferences;
    		
    	}catch(NumberFormatException ex) {
    		notificationMsg.setText("Please, insert only numbers in the cost field");
    		System.out.println("error in building the hash map containing the preferences: "+ex.getMessage());
    		return null;
    	}
    }
    
    // Reset all the TextField, ComboBoxes and buttons in the interface
    private void resetInterfaceElements() {
    	notificationMsg.setText("");
    	costField.setText("");
    	cityText.clear();
    	ComboBox[] cb = {wifiBox, airQualityBox, walkabilityBox, safetyBox, healthBox, 
    			nigthlifeBox, friendlyBox, englishBox, qualityLifeBox, };
    	for(int i = 0; i<cb.length; i++) {
    		cb[i].setValue("");
    	}
    	temperatureBox.setValue("");
    	hotelButton.setDisable(true);
    }
    
    // Set the nomadAdvisor object
    public void setNomadAdvisor(NomadAdvisor nomadAdvisor) {
    	this.nomadAdvisor = nomadAdvisor;
    }
}
