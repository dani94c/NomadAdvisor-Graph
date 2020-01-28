import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;

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
	// Transaction execution to find the number of customers that likes a certain preference
    private static int countCustomersPreference(Transaction tx, String preference) {
        StatementResult result = tx.run("MATCH (p:Preference {type: $type})<-[l:LIKES]-() RETURN COUNT(l)",
        		parameters("type", preference));
        return result.single().get(0).asInt();
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
 		Double f = result.single().get("avg(customer.age)", 0.0);
			System.out.println("preference avg age: "+preference+" "+f);
			return (int) Math.round(f);
 	}

    // For each city characteristics, computes the number of customers that have that preference
    public static HashMap<String, Integer> aggregateCustomersPreferences() {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        try(Session session = driver.session()) {
            for(Map.Entry<Utils.cityNames, String> attribute : Utils.cityAttributes.entrySet()) {
                int count = session.readTransaction(new TransactionWork<Integer>() {
                    @Override
                    public Integer execute(Transaction transaction) {
                        return countCustomersPreference(transaction, attribute.getValue());
                    }
                });
                result.put(attribute.getValue(), count);
            }
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
    /*
    public static void main(String[] args) {

    }
    */
}
