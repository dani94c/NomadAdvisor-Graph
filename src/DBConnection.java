import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

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
        mongoClient = MongoClients.create("mongodb://" + user + ":" + password + "@" + replica1
        		+ "," + replica2 + "," + replica3 + "/?authSource=" + database);
        String uriNeo4j = "bolt://localhost:7687";
        driver = GraphDatabase.driver(uriNeo4j, AuthTokens.basic(user, password));
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
