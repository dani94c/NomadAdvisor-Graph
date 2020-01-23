import java.io.IOException;
import java.util.List;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EmployeeInterface {
	
	private City selectedCity;
	private Hotel selectedHotel;
	private NomadAdvisor nomadAdvisor;
	
	//popup interfaces for add/update city/hotel and view statistics
	private CityFormController cityForm;
	private StatisticsInterface statInterface;
	private HotelFormInterface hotelForm;
	private FXMLLoader fxmlStatLoader;
	private FXMLLoader fxmlCityFormLoader;
	private FXMLLoader fxmlHotelFormLoader;
	private Scene statScene;
	private Scene cityFormScene;
	private Scene hotelFormScene;
	
	private Stage parentEmployeeStage;
	

    @FXML private AnchorPane backgroundPane;
    @FXML private ImageView logo;
    @FXML private Pane bodyPane;
    @FXML private ComboBox<String> chooseView;
    private ObservableList<String> viewList = 
    		FXCollections.observableArrayList(
    		"Customer View","City View");
    @FXML private Button statButton; 
   
    // Elements for the customers view
    @FXML private VBox customerPanel;
    @FXML private Label customerTitle;   
    // Customers Table Section
    private ObservableList<Customer> customerList = 
    		FXCollections.observableArrayList();
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> customerNameCol;
    @FXML private TableColumn<Customer, String> surnameCol;
    @FXML private TableColumn<Customer, String> usernameCol;
    @FXML private TableColumn<Customer, String> emailCol;
    
    // Elements for the Cities view
    @FXML private VBox cityPanel;
    @FXML private Label cityTitle;
    //City Table Section
    private ObservableList<City> cityList = 
    		FXCollections.observableArrayList();
    @FXML private TableView<City> cityTable;
    @FXML private TableColumn<City, String> cityNameCol;
    @FXML private TableColumn<City, String> cityCountryCol;
    @FXML private TableColumn<City, String> cityCharCol;
    
    // Research panel for searching by city name
    @FXML private HBox bottomPanel;
    @FXML private Button searchButton;
    @FXML private TextField cityNameField;
    
    @FXML private HBox buttonBox;
    @FXML private Button newCityButton;
    @FXML private Button deleteCityButton;
    
    // Hotels view
    @FXML private VBox hotelPanel;
    @FXML private Label hotelTitle;
    //Hotel Table Section
    private ObservableList<Hotel> hotelList = FXCollections.observableArrayList();
    @FXML private TableView<Hotel> hotelTable;
    @FXML private TableColumn<Hotel, String> hotelNameCol;
    @FXML private TableColumn<Hotel, String> hotelAddressCol;
    @FXML private TableColumn<Hotel, String> webColumn;
    
    @FXML private Button newHotelButton;
    @FXML private Button deleteHotelButton;

    @FXML private Text logMsg;
    @FXML private Button logoutButton;
    @FXML private Text title;
    
    // Initialize all the fxml elements in the interface and the popup interfaces handled by this interface
    @FXML private void initialize() {
    	chooseView.setItems(viewList);
    	initializeCustomerTable();
    	initializeCityTable();
    	initializeHotelTable();
    	
    	//retrieve the fxml for the popup windows related to city's form, hotel's form and statistics
    	try {
	    	fxmlStatLoader = new FXMLLoader(EmployeeInterface.class.getResource("resources/StatisticsInterface.fxml"));
	    	fxmlCityFormLoader = new FXMLLoader(EmployeeInterface.class.getResource("resources/CityForm.fxml"));
	    	fxmlHotelFormLoader = new FXMLLoader(EmployeeInterface.class.getResource("resources/HotelFormInterface.fxml"));
	    	statScene = new Scene(fxmlStatLoader.load());
	    	cityFormScene = new Scene(fxmlCityFormLoader.load());
	    	hotelFormScene = new Scene(fxmlHotelFormLoader.load());
    	} catch (IOException e) {		
			e.printStackTrace();
    	}
    	
    	//get popup controllers in order to be able to reload interface with the updated data
    	cityForm = (CityFormController)fxmlCityFormLoader.getController();
    	hotelForm = (HotelFormInterface)fxmlHotelFormLoader.getController();
    	statInterface = (StatisticsInterface)fxmlStatLoader.getController();	
    }

    // Initialize the tableView of the customers
    private void initializeCustomerTable() {
    	 customerNameCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
    	 surnameCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("surname"));
    	 usernameCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("username"));;
    	 emailCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("email"));
    
    	 customerTable.setItems(customerList);
    }
    
    // Initialize the tableView of the cities
    private void initializeCityTable() {
    	
    	cityNameCol.setCellValueFactory(new PropertyValueFactory<City, String>("cityName")); 
    	cityCountryCol.setCellValueFactory(new PropertyValueFactory<City, String>("countryName")); 
    	cityCharCol.setCellValueFactory(new PropertyValueFactory<City, String>("characteristics")); 
    	cityCharCol.setCellFactory(Utils.WRAPPING_CELL_FACTORY);

    	cityTable.setItems(cityList);
    	
    	// Add an event listener for the selection event of a row in the cities table
    	cityTable.getSelectionModel().selectedIndexProperty().addListener((num) ->
        {           
            if(cityTable.getSelectionModel().getSelectedItem() == null) {
               cityTable.getSelectionModel().clearSelection();

            }
            else {

                selectedCity = cityTable.getSelectionModel().getSelectedItem();
                logMsg.setText("");
                hotelTitle.setText(selectedCity.getCityName()+"'s Hotels List");
                
                //show hotels related to the selectedCity or delete the selected city if the Delete button is clicked
                //call to the hotelListUpdate with the choosen city for update the hotelTable
                hotelListUpdate(selectedCity);
	            newHotelButton.setDisable(false);
	            deleteHotelButton.setDisable(false);
           }
        });
    }
    
    // Initialize the table of the hotels
    private void initializeHotelTable() {
    	hotelNameCol.setCellValueFactory(new PropertyValueFactory<Hotel, String>("hotelName"));
        hotelAddressCol.setCellValueFactory(new PropertyValueFactory<Hotel, String>("address"));;
        webColumn.setCellValueFactory(new PropertyValueFactory<Hotel, String>("website"));
    
        hotelTable.setItems(hotelList);
        
        // Add an event listener for the selection event of a row of the hotels table
        hotelTable.getSelectionModel().selectedIndexProperty().addListener((num) ->
        {           
            if(hotelTable.getSelectionModel().getSelectedItem() == null) {
               hotelTable.getSelectionModel().clearSelection();

            }
            else {
                selectedHotel = hotelTable.getSelectionModel().getSelectedItem();
                logMsg.setText("");
            }
        });
    }

    // Reinitialize the interface at his starting point with updated data 
    public void initInterface() {
    	customerListUpdate();
    }
   
    // Retrieve the list of the customers to show in the related table
    private void customerListUpdate() {
    	List<Customer> customers = NomadHandler.getCustomers();
    	if(customers == null) 
    		System.out.println("Oops! Something went wrong!");
    	else {
    		customerList.clear();
    		customerList.addAll(customers);
    	}
    }
    
    // Retrieve the list of the cities to show in the related table
    private void cityListUpdate(String name){
    	List<City> cities = NomadHandler.getCity(name);
    	if(cities == null) 
    		System.out.println("Oops! Something went wrong!");
    	else {
    		cityList.clear();
    		cityList.addAll(cities);
    	}
    }
    
    // Retrieve the list of the hotels to show in the related table
    private void hotelListUpdate(City choosen){
    	List<Hotel> hotels = NomadHandler.getHotels(choosen);
    	if(hotels == null) 
    		System.out.println("Oops! Something went wrong!");
    	else {
    		hotelList.clear();
    		hotelList.addAll(hotels);
    	}
    }
    
    // Search and show a city in the table by name
    @FXML void searchCity(ActionEvent event) {
    	String cityName = cityNameField.getText().trim();
    	cityViewClean();
    	logMsg.setText("");
    	cityListUpdate(cityName);
    		
    } 
    
    // Open the city's form as a popup
    @FXML void addCity(ActionEvent event) {
    	
    	Stage popupStage = new Stage();

    	popupStage.setScene(cityFormScene);
    	cityForm.initInterface();
    	// Set this EmployeeInterface as Owner of the popup stage so that 
    	//closing the parent (owner) also the children are closed with it
    	popupStage.initOwner(parentEmployeeStage);
    	popupStage.setOnCloseRequest((WindowEvent we) -> {
    		cityListUpdate("");
    	});
    	popupStage.show();
    	
    }

 // Open the hotel's form as a popup
    @FXML void addHotel(ActionEvent event) {
    	Stage popupStage = new Stage();
    	popupStage.setScene(hotelFormScene);
    	// Set this EmployeeInterface as Owner of the popup stage so that 
    	//closing the parent (owner) also the children are closed with it
    	popupStage.initOwner(parentEmployeeStage);
    	hotelForm.initInterface(selectedCity.getCityName(), selectedCity.getCountryName());
    	popupStage.setOnCloseRequest((WindowEvent we) -> {
           	hotelListUpdate(selectedCity);
    	});
    	popupStage.show();

    }

    // Handle the data to show in the interface
    @FXML void changeView(ActionEvent event) {
    	ObservableList paneChildren = bodyPane.getChildren();
    	if(chooseView.getValue() == "Customer View") {
    		//retrieve the node related to the table to hide and than hide it
    		((VBox)paneChildren.get(paneChildren.indexOf(cityPanel))).setVisible(false);
    		((VBox)paneChildren.get(paneChildren.indexOf(hotelPanel))).setVisible(false);
    		cityViewClean();
    		logMsg.setText("");
    		
    		//show the right table
    		((VBox)paneChildren.get(paneChildren.indexOf(customerPanel))).setVisible(true);
    		customerListUpdate();

    	}
    	else {
    		//retrieve the node related to the table to hide and than hide it
    		((VBox)paneChildren.get(paneChildren.indexOf(customerPanel))).setVisible(false);
    		
    		//show the right table
    		((VBox)paneChildren.get(paneChildren.indexOf(cityPanel))).setVisible(true);
    		((VBox)paneChildren.get(paneChildren.indexOf(hotelPanel))).setVisible(true);
    		cityListUpdate("");
    	}
	
    }

    // Delete the selected city and reload the data in the city and hotel table
    @FXML void deleteCity(ActionEvent event) {
    	logMsg.setText(NomadHandler.deleteCity(selectedCity));
    	cityListUpdate("");
    	cityViewClean();    	
    }

    // Delete the selected hotel and reload the data in the hotel table
    @FXML void deleteHotel(ActionEvent event) {
    	logMsg.setText(NomadHandler.deleteHotel(selectedHotel));
    	hotelListUpdate(selectedCity);
    }

    // Open the statistics interface as a popup
    @FXML void showStatistics(ActionEvent event) {
    	Stage popupStage = new Stage();
    	popupStage.setScene(statScene);
    	// Set this EmployeeInterface as Owner of the popup stage so that 
    	//closing the parent (owner) also the children are closed with it
    	popupStage.initOwner(parentEmployeeStage);
    	statInterface.initInterface();

    	popupStage.show();
    }
    
    // Go back to the Login interface
    @FXML void logout(ActionEvent event) {
    	clean();
    	nomadAdvisor.changeScene("loginInterface");
    }
    
    // Retrieve the handler of the interfaces of the application
    public void setNomadAdvisor(NomadAdvisor nomadAdvisor) {
    	this.nomadAdvisor = nomadAdvisor;
    }
    
    // Retrieve the instance of the stage related to this interface controller, handled by NomadAdvisor, 
    //in order to be able to set it as the owner/parent of the popups interfaces.
    public void setParentStage(Stage stage) {
    	this.parentEmployeeStage = stage;
    }
   
    // Clear the view of the cities and hotels
    private void cityViewClean() {
    	cityNameField.setText("");
    	hotelList.clear();
		hotelTitle.setText("City's Hotels List");
		newHotelButton.setDisable(true);
        deleteHotelButton.setDisable(true);
    }
    
    // Clear the interface
    private void clean() {
    	chooseView.setValue("Customer View");
    	logMsg.setText("");	
    }

}
