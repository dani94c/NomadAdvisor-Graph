import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jHandle {
	private static Driver driver;
	
	public static void openConnection() {
		driver = DBConnection.getInstance().driver;
	}
	
	// Transaction execution to find the number of customers that likes a certain preference
    private static int countCustomersPreference(Transaction tx, String preference) {
        StatementResult result = tx.run("MATCH (p:Preference {type: $type})<-[l:LIKES]-() RETURN COUNT(l)",
        		parameters("type", preference));
        return result.single().get(0).asInt();
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
    
    public static void finish() {
    	if(driver != null) {
    		driver.close();
    		System.out.println("Closed connection with the Neo4j DB");
    	}
    }
}
