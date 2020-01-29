import java.util.concurrent.TimeUnit;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class DBConnection {
    private static DBConnection instance;
    public static MongoClient mongoClient;
    public static Driver driver;

    private DBConnection() {
    	String user = "NomadUser";
    	String database = "task2";
    	String password = "NomadAdvisor";
    	String replica1 = "127.0.0.1:27017";
    	String replica2 = "127.0.0.1:27018";
    	String replica3 = "127.0.0.1:27019";
    	//Parameter used to set the MaxTransactionRetryTime
    	int retryTime = 30;
    	
        mongoClient = MongoClients.create("mongodb://" + user + ":" + password + "@" + replica1
        		+ "," + replica2 + "," + replica3 + "/?authSource=" + database);
        String uriNeo4j = "bolt://localhost:7687";
        
        //Config section used to handle the retry Capability of the Transaction Functions.
        //By default the MaxTransactionRetryTime is set to 30s
        Config config = Config.builder()
                .withMaxTransactionRetryTime( retryTime, TimeUnit.SECONDS)
                .build();
        
        driver = GraphDatabase.driver(uriNeo4j, AuthTokens.basic(user, password), config);
        
    }

    public static DBConnection getInstance() {
        if(instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }
}
