import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class CityFormController {

    @FXML private VBox cityForm;
    @FXML private AnchorPane titleBox;
    @FXML private ImageView logo;
    @FXML private Label title;
    @FXML private Text logMsg;
    @FXML private Label cityName;
    @FXML private Label countryName;
    @FXML private Label charText;
    @FXML private TextField cityField;
    @FXML private TextField countryField;
    @FXML private Label wifiLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label qolLabel;
    @FXML private Label engLabel;
    @FXML private Label friendlyLabel;
    @FXML private Label nightLabel;
    @FXML private Label costLabel;
    @FXML private Label healtLabel;
    @FXML private Label safetyLabel;
    @FXML private Label walkLabel;
    @FXML private Label airLabel;
    @FXML private TextField tempField;
    @FXML private TextField costField;
    @FXML private ComboBox<String> wifiScore;
    @FXML private ComboBox<String> healthScore;
    @FXML private ComboBox<String> qolScore;
    @FXML private ComboBox<String> safetyScore;
    @FXML private ComboBox<String> engScore;
    @FXML private ComboBox<String> friendlyScore;
    @FXML private ComboBox<String> walkScore;
    @FXML private ComboBox<String> nightScore;
    @FXML private ComboBox<String> airScore;
    
    @FXML private Button addButton;
    @FXML private Button updateButton;
    
    private ObservableList<String> levels = 
            FXCollections.observableArrayList (
                "","1","2","3","4","5");
	
	@FXML private void initialize() {
		initializeComboBox();
	}
	
	public void initInterface() {
		initializeComboBox();
		clean();
		logMsg.setText("");
	}
	
	// Combobox initialization
    private void initializeComboBox() {
    	ComboBox[] cb = {wifiScore, airScore, walkScore, safetyScore, healthScore, 
    			nightScore, friendlyScore, engScore, qolScore};
    	setLevels(cb);
    }
    
    // Set the levels of the comboboxes
    private void setLevels(ComboBox[] cb) {
    	for(int i = 0; i<cb.length; i++) {
    		cb[i].getItems().removeAll(cb[i].getItems());
    		cb[i].getItems().addAll(levels);
    		cb[i].setValue(" ");
    	}
    }
    
    @FXML
    void addCity(ActionEvent event) {
    	String name = cityField.getText().trim();
    	String country = countryField.getText().trim();
    	//temperature and cost inserted fields must be only integers
    	if(!(tempField.getText().matches("^-?\\d+$")) || !(costField.getText().matches("^\\d+$")))
    		logMsg.setText("Your fields contains unexpected values");
    	
    	else {
    		ComboBox[] cb = {wifiScore, airScore, walkScore, safetyScore, healthScore, 
        			nightScore, friendlyScore, engScore, qolScore};
    		int i;
    		for(i = 0; i<cb.length; i++) {
    			if(cb[i].getValue().equals(""))
    				break;
    		}
    		if(i != cb.length)
    			logMsg.setText("City incomplete! You must fill all the fields");
    		else {	
	    		int temp = Integer.parseInt(tempField.getText().trim());
		    	int cost = Integer.parseInt(costField.getText().trim());
		    	int wifi = Integer.parseInt(wifiScore.getValue().toString());
		    	int air = Integer.parseInt(airScore.getValue().toString());
		    	int walk = Integer.parseInt(walkScore.getValue().toString());
		    	int safety = Integer.parseInt(safetyScore.getValue().toString());
		    	int health = Integer.parseInt(healthScore.getValue().toString());
		    	int night = Integer.parseInt(nightScore.getValue().toString());
		    	int friendly = Integer.parseInt(friendlyScore.getValue().toString());
		    	int eng = Integer.parseInt(engScore.getValue().toString());
		    	int qol = Integer.parseInt(qolScore.getValue().toString());
		    	HashMap<String,Integer> characteristics = new HashMap<String,Integer>();
		    	characteristics.put(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE), temp);
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.COST), cost);
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.AIR_QUALITY),air);
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.SAFETY),safety);
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.QUALITY_LIFE), qol);
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.WALKABILITY), walk);
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.HEALTHCARE), health);		
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.NIGHTLIFE), night);
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.WIFI), wifi);
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.FOREIGNERS), friendly);
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.ENGLISH), eng);
			
				City cityAdded = new City(characteristics, name, country);
				logMsg.setText(NomadHandler.createCity(cityAdded));
				if(logMsg.getText().equals("Success!")) {
					clean();
				}
					
    		}
    	}	
    }
    
    @FXML
    void updateCity(ActionEvent event) {
    	String name = cityField.getText().trim();
    	String country = countryField.getText().trim();
    	//temperature and cost inserted fields must be only integers
    	if(!(tempField.getText().equals("")) && !(tempField.getText().matches("^-?\\d+$")) || 
    			!(costField.getText().equals("")) && !(costField.getText().matches("^\\d+$")))
    		logMsg.setText("Your fields contains unexpected values");
    	else {
    		HashMap<String,Integer> characteristics = new HashMap<String,Integer>();
    		if(!tempField.getText().equals("")) {
    			int temp = Integer.parseInt(tempField.getText().trim());
    			characteristics.put(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE), temp);
    		}
    		if(!costField.getText().equals("")) {
    			int cost = Integer.parseInt(costField.getText().trim());
    			characteristics.put(Utils.cityAttributes.get(Utils.cityNames.COST), cost);
    		}
    		if(!airScore.getValue().equals("")) {
    			int air = Integer.parseInt(airScore.getValue().toString());
    			characteristics.put(Utils.cityAttributes.get(Utils.cityNames.AIR_QUALITY),air);		
    		}
    		if(!safetyScore.getValue().equals("")) {
    			int safety = Integer.parseInt(safetyScore.getValue().toString());
    			characteristics.put(Utils.cityAttributes.get(Utils.cityNames.SAFETY),safety);
    		}
    		if(!qolScore.getValue().equals("")) {
    			int qol = Integer.parseInt(qolScore.getValue().toString());
    			characteristics.put(Utils.cityAttributes.get(Utils.cityNames.QUALITY_LIFE), qol); 			
    		}
    		if(!walkScore.getValue().equals("")) {
	    		int walk = Integer.parseInt(walkScore.getValue().toString());
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.WALKABILITY), walk);
	    	}
    		if(!healthScore.getValue().equals("")) {
	    		int health = Integer.parseInt(healthScore.getValue().toString());
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.HEALTHCARE), health);		
	    	}
    		if(!nightScore.getValue().equals("")) {
	    		int night = Integer.parseInt(nightScore.getValue().toString());
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.NIGHTLIFE), night);
	    	}
    		if(!wifiScore.getValue().equals("")) {
	    		int wifi = Integer.parseInt(wifiScore.getValue().toString());
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.WIFI), wifi);	
	    	}
    		if(!friendlyScore.getValue().equals("")) {
	    		int friendly = Integer.parseInt(friendlyScore.getValue().toString());
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.FOREIGNERS), friendly);
	    	}
    		if(!engScore.getValue().equals("")){
	    		int eng = Integer.parseInt(engScore.getValue().toString());
				characteristics.put(Utils.cityAttributes.get(Utils.cityNames.ENGLISH), eng);
	    	}
	    	System.out.println(characteristics);
	    	City updatingCity = new City(characteristics, name, country);
			logMsg.setText(NomadHandler.updateCity(updatingCity));
			if(logMsg.getText().equals("Success!")) {
				clean();
			}
    	}
    		
    }
    
    
    
    private void clean() {
    	cityField.clear();
    	countryField.clear();
    	tempField.clear();
    	costField.clear();
    	//logMsg.setText("");
    	ComboBox[] cb = {wifiScore, airScore, walkScore, safetyScore, healthScore, 
    			nightScore, friendlyScore, engScore, qolScore};
    	for(int i = 0; i<cb.length; i++)
    		cb[i].setValue("");	
    }

}
