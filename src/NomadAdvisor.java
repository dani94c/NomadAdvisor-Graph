import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
//import javafx.event.*;
//import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class NomadAdvisor extends Application {
    
    private LoginInterface loginInterface;
    private HotelInterface hotelInterface;
    private PersonalAreaInterface personalAreaInterface;
    private CityInterface cityInterface;
    private EmployeeInterface employeeInterface;
    private Stage stage;
    private FXMLLoader fxmlLoaderHotel;
    private FXMLLoader fxmlLoaderLogin;
    private FXMLLoader fxmlLoaderCity;
    private FXMLLoader fxmlLoaderPersonalArea;
    private FXMLLoader fxmlLoaderEmployee;
    private Scene loginScene;
    private Scene personalAreaScene;
    private Scene cityScene;
    private Scene hotelScene;
    private Scene employeeScene;
    private User loggedUser;
    private City selectedCity;

    
    public void start(Stage stage) {
    	NomadHandler.openConnection();
    	Neo4jHandle.openConnection();
        this.stage = stage;
        this.stage.setTitle("Nomad Advisor");
        try {
        	// Initialize FXML loaders
        	fxmlLoaderCity = new FXMLLoader(NomadAdvisor.class.getResource("resources/CityInterface.fxml"));
        	fxmlLoaderLogin = new FXMLLoader(NomadAdvisor.class.getResource("resources/LoginInterface.fxml"));
        	fxmlLoaderHotel = new FXMLLoader(NomadAdvisor.class.getResource("resources/HotelInterface.fxml"));
        	fxmlLoaderPersonalArea = new FXMLLoader(NomadAdvisor.class.getResource("resources/PersonalAreaInterface.fxml"));
        	fxmlLoaderEmployee = new FXMLLoader(NomadAdvisor.class.getResource("resources/EmployeeInterface.fxml"));
        	// Create Scenes
        	this.loginScene = new Scene(fxmlLoaderLogin.load());
        	this.cityScene = new Scene(fxmlLoaderCity.load());
        	this.hotelScene = new Scene(fxmlLoaderHotel.load());
        	this.personalAreaScene = new Scene(fxmlLoaderPersonalArea.load());
        	this.employeeScene = new Scene(fxmlLoaderEmployee.load());
          
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    // Get controllers
	    loginInterface = (LoginInterface) fxmlLoaderLogin.getController();
	    cityInterface = (CityInterface) fxmlLoaderCity.getController();
	    hotelInterface = (HotelInterface) fxmlLoaderHotel.getController();
	    personalAreaInterface = (PersonalAreaInterface) fxmlLoaderPersonalArea.getController();
	    employeeInterface = (EmployeeInterface) fxmlLoaderEmployee.getController();
	    // Set the NomadAdvisor reference
	    cityInterface.setNomadAdvisor(this);
	    loginInterface.setNomadAdvisor(this);
	    hotelInterface.setNomadAdvisor(this);
	    personalAreaInterface.setNomadAdvisor(this);
	    employeeInterface.setNomadAdvisor(this);
	
	    changeScene("loginInterface");
	    this.stage.setOnCloseRequest((WindowEvent we) -> {
        	NomadHandler.closeConnection();
        });
	    this.stage.show();
    }

    public void changeScene(String newScene) {
        switch (newScene) {
            case "loginInterface":
                this.stage.setScene(loginScene);
                break;
            case "personalAreaInterface":
            	personalAreaInterface.initInterface();
            	this.stage.setScene(personalAreaScene);
            	break;
            case "cityInterface":
            	cityInterface.initInterface();
            	this.stage.setScene(cityScene);
            	break;
            case "hotelInterface":
            	hotelInterface.initInterface();
            	this.stage.setScene(hotelScene);
            	break;
            case "employeeInterface":
            	employeeInterface.initInterface();
            	this.stage.setScene(employeeScene);
            	employeeInterface.setParentStage(stage);
            	break;
            default:
                System.out.println("Not Implemented");
        }
    }
    
    public void setUser(User user) {
    	loggedUser = user;
    }
    
    public User getUser() {
    	return loggedUser;
    }
    
    public void setCity(City city) {
    	selectedCity = city;
    }
    
    public City getCity() {
    	return selectedCity;
    }
}

