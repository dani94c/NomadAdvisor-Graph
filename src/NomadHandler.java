import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NomadHandler {
  
	// Search a specific user in the login phase
	public static User readUser(User user, StringBuilder msg) {
		if(user!=null) {
			if(user.getEmail().equals("") == false && user.getPassword() != null) {
				user = MongoDBHandle.readUser(user, msg);
				// if the user is a Customer -> retrieve the preferences from the graph DB
				if(user!= null && user.getRole().equals("customer")) {
					((Customer)user).setPreferences(Neo4jHandle.getPreferences(user.getEmail()));
					Utils.printUser(user);
				}
				return user;
			}
		}
		msg.append("You didn't complete all the login fields");
		return null;
	}
	
	// Insert a new Customer in the reg phase
	public static String createCustomer(Customer customer) {
		if(customer!=null) {
			// Check if all the fields are correctly inserted from the Customer
			if(customer.getUsername().equals("") == false && customer.getName().equals("") == false && 
					customer.getSurname().equals("") == false && customer.getEmail().equals("") == false &&
					customer.getPassword().equals("") == false) {
				int result = MongoDBHandle.createCustomer(customer);
				switch(result) {
				case 0:
					return "Success!";
				case 1:
					return "email or username already exists";
				default:
					return "Ooops, something went wrong. Please, try again later";
				}
			}
		}
		System.out.println("The registration fields are not correctly inserted");
        return "You didn't complete the registration fields";
	}
	
	// Retrieves all the registered customers for the Employee Interface
	public static List<Customer> getCustomers(){
		return MongoDBHandle.selectCustomers();
	}
	
	// Calls the database handler in order to insert a new City and returns a notification message to the interface
	public static String createCity(City addedCity) {
		if(addedCity != null) {
			//Check if all the text fields have been correctly inserted by the Employee
			if(addedCity.getCityName().equals("") == false && addedCity.getCountryName().equals("") == false) {
				int result = MongoDBHandle.createCity(addedCity);
				switch(result) {
				case 0:
					return "Success!";
				case 1:
					return "The city already exists. Data have been updated!";
				case 2:
					return "Oops! Something went wrong. Try again later!";
				}
			}
		}
		System.out.println("The city fields are not correctly inserted");
        return "You didn't complete the city fields";
	}
	
	// Updates characteristics of an existing city and returns a notification message to the interface
	public static String updateCity(City updatingCity) {
		if(updatingCity != null) {
			//Check if all the text fields have been correctly inserted by the Employee
			if(updatingCity.getCityName().equals("") == false && updatingCity.getCountryName().equals("") == false 
					&& updatingCity.getHashedCharacteristics().isEmpty() == false) {
				int result = MongoDBHandle.updateCity(updatingCity);
				switch(result) {
				case 0:
					return "Success!";
				case 1:
					return "City update operation failed: there's nothing to change!";
				case 2:
					return "City update operation failed: The city does not exists!";
				}
			}
		}
		System.out.println("The city fields are not correctly inserted");
        return "You didn't complete the city fields";
	}
	
	// Calls the database handler in order to delete a City and returns a notification message to the interface
	public static String deleteCity(City selectedCity) {
		if(selectedCity != null) {
			boolean result = MongoDBHandle.deleteCity(selectedCity);
			if(result)
				return "Success!";
		}
		return "Oops! Something went wrong. Try again later!";
	}
	
	// Calls the database handler in order to delete a Hotel and returns a notification message to the interface
	public static String deleteHotel(Hotel hotel) {
		if(hotel != null) {
			boolean result = MongoDBHandle.deleteHotel(hotel.getHotelName(),hotel.getCityName(),hotel.getCountryName());
			if(result)
				return "Success!";
		}
		return "Oops! Something went wrong. Try again later!";
	}
	
	// Search the city by a given preferences
	public static List<City> getCity(HashMap<String,Integer> pref) {
		if(pref.isEmpty())
			return MongoDBHandle.selectCities();
		return MongoDBHandle.selectCities(pref);
	}
	
	// Search the city by a given name
	public static List<City> getCity(String cityName) {
		if(cityName.isEmpty())
			return MongoDBHandle.selectCities();
		return MongoDBHandle.selectCities(cityName);
	}

	//Add a review for a certain hotel
    public static boolean addReview(Review newReview){
        return MongoDBHandle.createReview(newReview);
    }

    //Gets the reviews of a certain hotel
    public static List<Review> getReviews(Hotel hotel){
        return MongoDBHandle.selectReviews(hotel.getHotelName(), hotel.getCityName(), hotel.getCountryName());
    }

    //Get the hotels of a certain city
    public static List<Hotel> getHotels(City city){
    	return MongoDBHandle.selectHotels(city.getCityName(), city.getCountryName());
	}
	
    // Updates the customer preferences and return the message to show in the interface
	public static String updatePreferences(Customer customer, List<String> preferences) {
		customer.setPreferences(preferences);
		String result;
		if(MongoDBHandle.updatePreferences(customer)) {
			result = "Success!!!";
		}
		else {
			result = "There's nothing to change";
		}
		return result;
	}
	
	// Calls the database handler to create a new hotel and returns a string to show on the interface
	public static String createHotel(String name, String city, String country, String address, String website) {
		Hotel hotel = new Hotel(name, city, country, address, website);
		int result = MongoDBHandle.createHotel(hotel);
		switch(result) {
			case 0:
				return "Operation successfully completed";
			case 1:
				return "Operation failed: the hotel already exists";
			case 2:
				return "Ooops, something went wrong. Please, try again later";
		}
		return null;
	}
	
	// Computes the data to show in the pie charts
	public static List<HashMap<String, Integer>> computeChartsData() {
		List<HashMap<String, Integer>> pieChartsData = new ArrayList();
		pieChartsData.add(MongoDBHandle.aggregateCitiesCharacteristics());
		pieChartsData.add(Neo4jHandle.aggregateCustomersPreferences());
		pieChartsData.add(Neo4jHandle.getAverageAgeCharacteristics());
		if((pieChartsData.get(0) == null) || (pieChartsData.get(1) == null))
			return null;
		return pieChartsData;
	}
	
	public static void openConnection() {
		MongoDBHandle.openConnection();
		Neo4jHandle.openConnection();
	}
	
	public static void closeConnection() {
		MongoDBHandle.finish();
		Neo4jHandle.finish();
	}
}
