import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.*;
import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jHandle {

    private static Driver driver;

    public static void openConnection() {
        driver = DBConnection.getInstance().driver;
    }

    //Transaction execution to create a new customer
	  private static Integer createCustomerNode(Transaction tx, Customer customer) {
			tx.run("CREATE (c:Customer {email: $email, age: $age, username: $username})", 
					parameters("email",customer.getEmail(), "age",customer.getAge(),"username",customer.getUsername()));
			System.out.println("Node added for customer's email: "+customer.getEmail());
			return 1;
	  }
	
    //Insert a a new registered customer in the graph
    public static void addCustomer(Customer customer) {
    	try(Session session = driver.session()) {
    		session.writeTransaction(new TransactionWork<Integer>() {
	    		@Override
	    		public Integer execute(Transaction tx) {
	    			return createCustomerNode(tx,customer);
	    		}
    		});
    	}
    }
  
    //Transaction execution to update the customer's preferences
    private static Boolean updateLikes(Transaction tx, Customer customer) {
    	return false;
    }

    public static void updatePreferences(Customer customer ) {
    	try(Session session = driver.session()) {
    		session.writeTransaction(new TransactionWork<Boolean>() {
    			@Override
    			public Boolean execute(Transaction tx) {
    				return updateLikes(tx,customer);
    			}
    		});
    	}
    }
  
    // Method in order to update the Age field of the customer in the graph database
    public static void updateCustomerAge(Customer customer) {
        try (Session session = driver.session()) {
            session.run("MATCH (c:Customer)\n" +
                    "WHERE c.email = $email AND c.username = $username\n" +
                    "SET c.age = $age", parameters("email", customer.getEmail(), "username", customer.getUsername(), "age", customer.getAge()));
        }
    }

    // Transaction method in order to retrieve the customers that have common preference of the passed customer
    private static List<Customer> matchCustomers(Transaction tx, Customer customer) {
        List<Customer> customersList = new ArrayList<Customer>();
        StatementResult result = tx.run("MATCH (c:Customer)-[:LIKES]->(p:Preference)<-[:LIKES]-(n:Customer)\n" +
                "WHERE c.email = $email AND c.username = $username\n" +
                "RETURN n.email AS email, n.username AS username, collect(p.type) AS preferences, count(p.type) AS counter ORDER BY counter DESC", parameters("email", customer.getEmail(), "username", customer.getUsername()));
        while (result.hasNext()) {
            Record record = result.next();
            customersList.add(new Customer(null, null, record.get("email").asString(), null, record.get("username").asString(), 0, record.get("preferences").asList(Values.ofString())));
        }
        return customersList;
    }

    //  Session method in order to retrieve the customers that have common preference of the passed customer
    public static List<Customer> retrieveSuggestedCustomers(Customer customer) {
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<List<Customer>>() {
                @Override
                public List<Customer> execute(Transaction tx) {
                    return matchCustomers(tx, customer);
                }
            });
        }catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
    }

    // Transaction execution to find the number of customers that likes a certain preference
    private static HashMap<String, Integer> countCustomersPreference(Transaction tx) {
    	HashMap<String, Integer> result = new HashMap<String, Integer>();
        StatementResult records = tx.run("MATCH (p:Preference)<-[r:LIKES]-() RETURN p.type AS type, COUNT(r) AS count");
        while(records.hasNext()) {
        	Record record = records.next();
        	result.put(record.get("type").asString(), record.get("count").asInt());
        }
        for(Map.Entry<Utils.cityNames, String> attribute : Utils.cityAttributes.entrySet()) {
        	if(!result.containsKey(attribute.getValue())) {
        		result.put(attribute.getValue(), 0);
        	}
        }
        return result;
    }
	
 	// Transaction execution to find the list of preferences of a customer
 	private static List<String> matchPreferenceNodes(Transaction tx, String email) {
 		List<String> pref = new ArrayList<String>();
 		StatementResult result = tx.run("MATCH (c:Customer {email:$email})-[:LIKES]->(Preference) RETURN Preference.type",
 				parameters("email",email));
 		while(result.hasNext()) {
 			pref.add(result.next().get(0).asString());
 		}
 		return pref;
 	}
 	
 	// Transaction execution to find the avg customers' age relative to a characteristic
 	private static int getCharacteristicAges(Transaction tx, String preference) {
 		StatementResult result = tx.run("MATCH (p:Preference {type:$type})<-[:LIKES]-(customer) return avg(customer.age)",
 				parameters("type", preference));
 		Double avgAge = result.single().get("avg(customer.age)", 0.0);
			//System.out.println("preference avg age: "+preference+" "+avgAge);
			return (int) Math.round(avgAge);
 	}

    // For each city characteristics, computes the number of customers that have that preference
    public static HashMap<String, Integer> aggregateCustomersPreferences() {
        HashMap<String, Integer> result = null;
        try(Session session = driver.session()) {
            result = session.readTransaction(new TransactionWork<HashMap<String, Integer>>() {
                @Override
                public HashMap<String, Integer> execute(Transaction transaction) {
                    return countCustomersPreference(transaction);
                }
            });
        }catch(Exception ex) {
			ex.printStackTrace();
		}
        return result;
    }

    // For each city characteristics, computes the average age of customers
    public static HashMap<String, Integer> getAverageAgeCharacteristics() {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        try(Session session = driver.session()) {
            for(Map.Entry<Utils.cityNames, String> attribute : Utils.cityAttributes.entrySet()) {
                int avgAge = session.readTransaction(new TransactionWork<Integer>() {
                    @Override
                    public Integer execute(Transaction transaction) {
                        return getCharacteristicAges(transaction, attribute.getValue());
                    }
                });
                result.put(attribute.getValue(), avgAge);
            }
        }catch(Exception ex) {
			ex.printStackTrace();
		}
        return result;
    }
    
    // Get the preferences of a specific Customer
 	public static List<String> getPreferences(String email) {
 		try(Session session = driver.session()) {
 			return session.readTransaction(new TransactionWork<List<String>>() {
 				@Override
 				public List<String> execute(Transaction tx) {
 					return matchPreferenceNodes(tx,email);
 				}
 			});
 		}
 	}
    
    public static void finish() {
        if(driver != null) {
            driver.close();
            System.out.println("Closed connection with the Neo4j DB");
        }
    }
}
