import com.mongodb.Block;
import com.mongodb.MongoWriteException;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;

import java.awt.Desktop.Action;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;

import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoDBHandle {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> userCollection;
    private static MongoCollection<Document> cityCollection;
    private static MongoCollection<Document> hotelCollection;
    private static MongoCollection<Document> reviewCollection;

    public static void openConnection() {
        mongoClient = DBConnection.getInstance().mongoClient;
        /* new WriteConcern(1,100): 1 represents the w:1 so the ack is returned after the write is done on the primary server
        *  100 represents the time in ms. If the application doesn't receive any response from the DB after
        *  this period of time, it generates an error. 
        */
        database = mongoClient.getDatabase("task2").withWriteConcern(new WriteConcern(1,100));
        userCollection = database.getCollection("user");
        cityCollection = database.getCollection("city");
        hotelCollection = database.getCollection("hotel");
        reviewCollection = database.getCollection("review");
    }

    public static void finish() {
        if(mongoClient != null) {
            mongoClient.close();
            System.out.println("Closed connection with the DB");
        }
    }
  
    // Login interface

    /* It creates an Employee or a Customer object, but it returns a User object. In this way it can be used to read both Customers and Employees.
     * 
     * return: null in case of errors, the desired user. 
     * In msg set the result of the method, this is needed for the loginInterface
     */
    public static User readUser(User user, StringBuilder msg) {
    	Customer customer;
    	Employee employee;
    	try {
    		Document document = userCollection.find(Filters.and(Filters.eq("email", user.getEmail()),Filters.eq("password", user.getPassword()))).first();
    		// Check if the user exists
    		if(document == null) {
    			msg.append("Wrong email or password");
    			return null;
    		}
    		System.out.println("Document retrieved in readUser: " + document.toString());
    		// Check if the role field exists
			if(document.getString("role") != null) {
				msg.append("Success!");
				if(document.getString("role").equals("customer"))	// customer
	    			return customer = new Customer(document.getString("name"), document.getString("surname"), user.getEmail(), user.getPassword(),
							document.getString("username"), document.getInteger("age", 0), (List<String>) document.get("preferences"));
	    		else // employee
	    			return employee = new Employee(document.getString("name"), document.getString("surname"), user.getEmail(), user.getPassword());
			}
    	}catch(Exception ex) {
        	msg.append("Oops! Something went wrong");
    		System.out.println("Error during readUser: "+ex.getMessage());
    		return null;
    	}
    	// if role is null
    	msg.append("Oops! Something went wrong. Do again the registration");
    	return null;
    }

    /* Create new object. 
	 * Return 0 -> everything is ok
	 * Return 1 -> object already exists or a unique value already exists
	 * Return 2 -> DB error
	 */
    public static int createCustomer(Customer customer) {
    	try {
    	Document doc = new Document("role",customer.getRole())
    			.append("name", customer.getName())
    			.append("surname", customer.getSurname())
    			.append("email", customer.getEmail())
    			.append("password", customer.getPassword())
    			.append("username", customer.getUsername());
    	userCollection.insertOne(doc);
    	}catch(Exception ex) {
    		System.out.println("Error inserting a new customer: "+ex.getMessage());
    		// Duplicate key
    		if(ex.toString().contains("E11000")) {
    			return 1;
    		}
    		// Other general errors
    		return 2;
    	}
        return 0;
    }
    
    // Customer Interface (City)
    
    /* Retrieve all the cities
     * return: null if an error occurs, empty list if no city is found or the full list
     */
    public static List<City> selectCities() {
    	List<City> cities = new ArrayList<City>();
    	MongoCursor<Document> cursor = cityCollection.find().limit(30).iterator();
    	try {
    		while(cursor.hasNext()) {
    			 Document dc = cursor.next();
    			 City city = buildCity(dc);
    			 if(city != null)
    				 cities.add(city);
    		}
    	}catch(Exception ex) {
    		System.out.println("Error: "+ex);
    		return null;
    	}
        return cities;
    }
    
    /* Search the first 30 cities that satisfies the preferences inserted
     * return: null if an error occurs, empty list if no city is found or the full list
     */
    public static List<City> selectCities(HashMap<String,Integer> pref) {
    	List<City> cities = new ArrayList<City>();
    	List<Bson> filters = new ArrayList<Bson>();
    	// set the filter for the research
    	if(pref.get("temp_lower") != null) {	// if the filter's temperature is set
    		filters.add(Filters.and(
					Filters.gte(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE), pref.get("temp_lower")),
					Filters.lte(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE), pref.get("temp_greater"))
					));
    		pref.remove("temp_lower");
    		pref.remove("temp_greater");
    	}
	    	
    	for(Map.Entry<String, Integer> entry: pref.entrySet()) {
    		if(entry.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.COST))) { // filter's cost
    			filters.add(Filters.lte(entry.getKey(), entry.getValue()));
    		} else
    			filters.add(Filters.gte(entry.getKey(),entry.getValue()));	// all the other filters
    	}
    	
    	// Show the first 30 cities
    	MongoCursor<Document> cursor = cityCollection.find(Filters.and(filters)).limit(30).iterator();
    	try {
    		while(cursor.hasNext()) {
    			 Document dc = cursor.next();
    			 City city= buildCity(dc);
    			 if( city != null)
    				 cities.add(city);
    		}
    	}catch(Exception ex) {
    		System.out.println("Error: "+ex);
    		return null;
    	}
    	return cities;
    }
    
	/* Find the cities by a given name
	 * return: null if an error occurs, empty list if no city is found or the full list
	 */
    public static List<City> selectCities(String name) {
    	List<City> cities = new ArrayList<City>();
    	MongoCursor<Document> cursor = cityCollection.find(Filters.eq("_id.city", name)).limit(30).iterator();
    	try {
    		while(cursor.hasNext()) {
    			 Document dc = cursor.next();
    			 City city = buildCity(dc);
    			 if(city != null)
    				 cities.add(city);
    		}
    	}catch(Exception ex) {
    		System.out.println("Error: "+ex);
    		return null;
    	}
        return cities;
    }

    // initialize city with the values taken from the DB
    static City buildCity(Document dc) {
    	HashMap<String,Integer> charact = new HashMap<String,Integer>();
    	// put all the characteristics in the HashMap
    	for(String key: dc.keySet()) {
    		// exclude the ID field
    		if(!key.equals(Utils.ID)) {
    			charact.put(key, dc.getInteger(key));
    		}    			
		}
    	Document id = (Document)dc.get(Utils.ID);
    	String cityName = " ";
    	String countryName = " ";
    	// Check if the id is not null
    	if(id != null) {
    		cityName = id.getString(Utils.CITY);
        	countryName = id.getString(Utils.COUNTRY);
        	if(cityName == null || countryName == null) 
        		return null;
    	}
    	return new City(charact, cityName, countryName);
    }

    //Retrieve the hotels of a certain City (specifying also the country)
    public static List<Hotel> selectHotels(String city, String country) {
        List<Hotel> hotels = new ArrayList<>();
        //Query the Hotel Collection DB for a City name and a Country
        MongoCursor<Document> cursor = hotelCollection.find(Filters.and(Filters.eq("_id.city", city), Filters.eq("_id.country", country))).limit(30).iterator();
        try{
            while(cursor.hasNext()){ //Iterates on the documents
                Document d = cursor.next();
                Document d_hotel = (Document) d.get("_id");
                Hotel h = new Hotel(d_hotel.getString("name"), d_hotel.getString("city"), d_hotel.getString("country"), d.getString("address"), d.getString("websites"));
                hotels.add(h);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return hotels;
    }

    // Customer Interface (Hotel)
	// Retrieve the reviews for a certain hotel
    public static List<Review> selectReviews(String hotelName, String city, String country) {
        List<Review> reviews = new ArrayList<>();
        MongoCursor<Document> cursor = reviewCollection.find(Filters.and(Filters.eq("hotelId.name", hotelName), Filters.eq("hotelId.city", city), Filters.eq("hotelId.country", country))).sort(Sorts.descending("date")).limit(30).iterator();
        try{
            while(cursor.hasNext()){
                Document d = cursor.next();
                Document d_hotel = (Document) d.get("hotelId");

                DateFormat osLocalizedDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(osLocalizedDateFormat.format(d.getDate("date")));

                String username = d.getString("username")==null?"Anonymous":d.getString("username");
                Review r = new Review(username, d.getInteger("rating"), d.getString("text"), date, d_hotel.getString("name"), d_hotel.getString("city"), d_hotel.getString("country"));
                reviews.add(r);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return reviews;
    }

    //Insert the review in the DB
    public static boolean createReview(Review review) {
        Document rv = new Document("username", review.getUsername())
                        .append("rating", review.getRating())
                        .append("date", review.getDate())
                        .append("hotelId", new Document("name", review.getHotelName()).append("city", review.getCityName()).append("country", review.getCountryName()));
        if(review.getText() != null)
        	rv.append("text", review.getText());
        try {
            reviewCollection.insertOne(rv);
        } catch (MongoWriteException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // Personal Area
    // Update customer preferences
    public static boolean updatePreferences(Customer customer) {
    	Document updateDoc = null;
    	if(customer.getPreferences().size() == 0) // If there are no preferences, the field is removed
    		updateDoc = new Document("$unset", new Document(Utils.PREFERENCES, 1));
    	else
    		updateDoc = new Document("$set", new Document(Utils.PREFERENCES, customer.getPreferences())); // Set the chosen preferences
    	UpdateResult result = userCollection.updateOne(Filters.eq("email", customer.getEmail()), updateDoc);
    	if(result.getModifiedCount() == 0) {
    		System.out.println("Customer preferences update operation failed: There's nothing to change");
    		return false;
    	}
        return true;
    }
  
    // Employee Interface
    //retrieve all the registered customers
    public static List<Customer> selectCustomers() {
    	List<Customer> customers = new ArrayList<Customer>();
    	MongoCursor<Document> cursor = userCollection.find(Filters.eq("role","customer")).iterator();
    	try {
    		while(cursor.hasNext()) {
    			Document document = cursor.next();
    			String name = document.getString("name")==null?"":document.getString("name");
				String surname = document.getString("surname") == null ? "" : document.getString("surname");
				Customer c = new Customer(name, surname, document.getString("email"), document.getString("password"),
						document.getString("username"), 0, (List<String>) document.get("preferences"));
				customers.add(c);
			}
		} catch (Exception ex) {
			System.out.println("Error: " + ex);
			return null;
		}
		return customers;
	}

	public static int updateCustomerAge(Customer customer) {
		Document updatedField = new Document("age", customer.getAge());
		UpdateResult result = userCollection.updateOne(Filters.eq("email", customer.getEmail()), new Document("$set", updatedField));
		if (result.getModifiedCount() == 0) {
			System.out.println("Customer update operation failed");
			return 2;
		}
		return 0;
	}

	// Delete also hotels and reviews connected
	public static boolean deleteCity(City city) {
		List<Hotel> hotels = selectHotels(city.getCityName(), city.getCountryName());
		Document cityId = new Document(
				"city", city.getCityName())
				.append("country", city.getCountryName());
		for (int i = 0; i < hotels.size(); i++) {
			if (!deleteHotel(hotels.get(i).getHotelName(), city.getCityName(), city.getCountryName()))
				return false;
    	}
    	// If no errors occurs is possible to delete the city
    	try {
			DeleteResult deleteResult = cityCollection.deleteOne(Filters.eq("_id",cityId));
			System.out.println("Result of deletion of city "+city.getCityName()+" is: "+deleteResult.getDeletedCount());
		}catch(MongoWriteException e) {
			System.out.println("Delete operation interrupted");
			return false;
		}
    	return true;
    }
    
    // delete also the reviews connected
    public static boolean deleteHotel(String hotelName, String cityName, String country) {
    	Document hotelId = new Document("name",hotelName)
    						.append("city", cityName)
    						.append("country", country);
		//check for the correct deletion of all the associated reviews
		try {
			DeleteResult deleteResult = reviewCollection.deleteMany(Filters.eq("hotelId",hotelId));
			System.out.println("For the hotel "+hotelName+" # of reviews deleted: "+deleteResult.getDeletedCount());
			}catch(MongoWriteException e) {
				System.out.println("Delete operation interrupted");
				return false;
		}
		// check for the correct deletion of the hotel
		try {
			DeleteResult deleteResult = hotelCollection.deleteOne(Filters.eq("_id",hotelId));
			System.out.println("Result of deletion of hotel "+hotelName+" is: "+deleteResult.getDeletedCount());
			}catch(MongoWriteException e) {
				System.out.println("Delete operation interrupted");
				return false;
			}
		return true;
    }

    /* Create new object. 
	 * Return 0 -> everything is ok
	 * Return 1 -> object already exists
	 * Return 2 -> DB error
	 */
    public static int createCity(City cityAdded) {
    	try {
	    	Document cityDoc = new Document(
	    			"_id", new Document(
	    					"city", cityAdded.getCityName())
	    					.append("country", cityAdded.getCountryName()));
	    	for(Map.Entry<String,Integer> attribute: cityAdded.getHashedCharacteristics().entrySet()) 
	    		cityDoc.append(attribute.getKey(), attribute.getValue());
	    	cityCollection.insertOne(cityDoc);
    	} catch(Exception ex) {
    		if(ex.toString().contains("E11000")) { // City already exists, so it is updated
    			return updateCity(cityAdded);
    		}
    		return 2;
    	}
        return 0;
    }

	/* Update a city.
  	 * Return 0 -> everything is ok
  	 * Return 1 -> object already exists or nothing has been modified
  	 * Return 2 -> DB error
  	 */
    public static int updateCity(City city) {
    	Document id = new Document(
				"city", city.getCityName())
				.append("country", city.getCountryName());
    	Document updatedFields = new Document();
    	for(Map.Entry<String,Integer> attribute: city.getHashedCharacteristics().entrySet()) 
    		updatedFields.append(attribute.getKey(), attribute.getValue());
    	UpdateResult result = cityCollection.updateOne(Filters.eq("_id", id), new Document("$set", updatedFields));
    	if(result.getMatchedCount() == 0) {
    		System.out.println("City update operation failed: The city does not exists");
    		return 2;
    	}
    	else if(result.getModifiedCount() == 0) {
    		System.out.println("City update operation failed: there's nothing to change");
    		return 1;
    	}
    	
        return 0;
    }

    public static boolean deleteHotel(Hotel hotel) {
        return false;
    }

    /*
     * Create a new hotel
     * Return:
     * 0 if everything goes right
     * 1 if the hotel already exists
     * 2 if an error occurs
     * */
    public static int createHotel(Hotel hotel) {
    	try {
	    	Document hotelDoc = new Document(
	    			"_id", new Document(
	    					"name", hotel.getHotelName())
	    					.append("city", hotel.getCityName())
	    					.append("country", hotel.getCountryName()))
	    			.append("address", hotel.getAddress());
	    	if(hotel.getWebsite() != null)
	    		hotelDoc.append("websites", hotel.getWebsite());
	    	hotelCollection.insertOne(hotelDoc);
    	} catch(Exception ex) {
    		if(ex.toString().contains("E11000")) { // Hotel already exists, so it is updated
    			return updateHotel(hotel);
    		}
    		return 2;
    	}
        return 0;
    }
    
    // Updates the fields address and websites of an hotel
    private static int updateHotel(Hotel hotel) {
    	Document id = new Document(
				"name", hotel.getHotelName())
				.append("city", hotel.getCityName())
				.append("country", hotel.getCountryName());
    	Document updatedFields = new Document("address", hotel.getAddress()); // Fields to update
    	if(hotel.getWebsite() != null)
    		updatedFields.append("websites", hotel.getWebsite());
    	UpdateResult result = hotelCollection.updateOne(Filters.eq("_id", id), new Document("$set", updatedFields));
    	if(result.getModifiedCount() == 0) {
    		System.out.println("Hotel update operation failed: There's nothing to change");
    		return 2;
    	}
        return 0;
    }

    // For each city characteristics, computes the number of customers that have that preference
    public static HashMap<String, Integer> aggregateCustomersPreferences() {
    	HashMap<String, Integer> result = new HashMap<String, Integer>();
    	MongoCursor<Document> cursor = null;
    	try {
    		for(Map.Entry<Utils.cityNames, String> attribute : Utils.cityAttributes.entrySet()) {
    			// Count the customers that has the attribute
    			cursor = userCollection.aggregate(
    					Arrays.asList(
    							Aggregates.match(Filters.eq(Utils.PREFERENCES, Utils.cityAttributes.get(attribute.getKey()))),
    							Aggregates.count()
    							)).iterator();
    			if(cursor.hasNext()) {
    				result.put(Utils.cityAttributes.get(attribute.getKey()), (Integer) cursor.next().get("count"));
    			}
    			else {
    				result.put(Utils.cityAttributes.get(attribute.getKey()), 0);
    			}
    		}
    	}
    	catch(Exception ex) {
    		System.out.println("Customers' preferences aggregation exception: " + ex.getMessage());
    		return null;
    	}
    	finally {
    		if(cursor != null)
    			cursor.close();
    	}
        return result;
    }

    // For each city characteristics, computes the number of cities that have that characteristic
    public static HashMap<String, Integer> aggregateCitiesCharacteristics() {
    	HashMap<String, Integer> result = new HashMap<String, Integer>();
    	MongoCursor<Document> cursor = null;
    	try {
	    	for(Map.Entry<Utils.cityNames, String> attribute : Utils.cityAttributes.entrySet()) { // For each attribute
	    		if(attribute.getKey() == Utils.cityNames.COST) { // Count costs less or equal 2000
	    			cursor = cityCollection.aggregate(
	    	    			Arrays.asList(
	    	    					Aggregates.match(Filters.lte(Utils.cityAttributes.get(Utils.cityNames.COST), 2000)),
	    	    					Aggregates.count()
	    	    					)).iterator();
	    			if(cursor.hasNext()) {
	    				result.put(Utils.cityAttributes.get(Utils.cityNames.COST), (Integer) cursor.next().get("count"));
	    			}
	    			else {
	    				result.put(Utils.cityAttributes.get(Utils.cityNames.COST), 0);
	    			}
	    		}
	    		else if(attribute.getKey() == Utils.cityNames.TEMPERATURE) { // Count temperatures between 15 and 25
	    			cursor = cityCollection.aggregate(
	    	    			Arrays.asList(
	    	    					Aggregates.match(Filters.and(
	    	    							Filters.gte(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE), 15),
	    	    							Filters.lte(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE), 25)
	    	    							)),
	    	    					Aggregates.count()
	    	    					)).iterator();
	    			if(cursor.hasNext()) {
	    				result.put(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE), (Integer) cursor.next().get("count"));
	    			}
	    			else {
	    				result.put(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE), 0);
	    			}
	    		}
	    		else { // Count attributes greater or equal to 3
	    			cursor = cityCollection.aggregate(
	    					Arrays.asList(
	    	    					Aggregates.match(Filters.gte(Utils.cityAttributes.get(attribute.getKey()), 3)),
	    	    					Aggregates.count()
	    	    					)).iterator();
	    			if(cursor.hasNext()) {
	    				result.put(Utils.cityAttributes.get(attribute.getKey()), (Integer) cursor.next().get("count"));
	    			}
	    			else {
	    				result.put(Utils.cityAttributes.get(attribute.getKey()), 0);
	    			}
	    		}
	    	}
    	}
    	catch(Exception ex) {
    		System.out.println("Cities' characteristics aggregation exception: " + ex.getMessage());
    		return null;
    	}
    	finally {
    		if(cursor != null)
    			cursor.close();
    	}
        return result;
    }
}
