import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class Utils {
	// City's Characteristics
	public static enum cityNames {
		TEMPERATURE,COST,AIR_QUALITY,SAFETY,QUALITY_LIFE,WALKABILITY,HEALTHCARE,NIGHTLIFE,
		WIFI,FOREIGNERS,ENGLISH
	}
	// HashMap where the key is the cityNames and the value is the key in the DB related to the characteristics
	public static HashMap<cityNames,String> cityAttributes = new HashMap();

	// Initialize cityAttributes
	static {
		cityAttributes.put(cityNames.TEMPERATURE,"temperature");
		cityAttributes.put(cityNames.COST,"cost");
		cityAttributes.put(cityNames.AIR_QUALITY,"airQuality");
		cityAttributes.put(cityNames.SAFETY,"safety");
		cityAttributes.put(cityNames.QUALITY_LIFE, "qualityLife");
		cityAttributes.put(cityNames.WALKABILITY, "walkability");
		cityAttributes.put(cityNames.HEALTHCARE, "healthcare");		
		cityAttributes.put(cityNames.NIGHTLIFE, "nightlife");
		cityAttributes.put(cityNames.WIFI, "wifi");
		cityAttributes.put(cityNames.FOREIGNERS, "foreignersFriendly");
		cityAttributes.put(cityNames.ENGLISH, "english");
	}
	
	// Adjust the strings in order to be visible in the column of the tables
	public static final Callback<TableColumn<City,String>, TableCell<City,String>> WRAPPING_CELL_FACTORY =
            new Callback<TableColumn<City,String>, TableCell<City,String>>() {

                @Override public TableCell<City,String> call(TableColumn<City,String> param) {
                    TableCell<City,String> tableCell = new TableCell<City,String>() {
                        @Override protected void updateItem(String item, boolean empty) {
                            if (item == getItem()) return;

                            super.updateItem(item, empty);
                            if (item == null) {
                                super.setText(null);
                                super.setGraphic(null);
                            } else {
                            	item = item.replace("}", "");
                                item = item.replace("{", "");
                                item = item.replace(", ", "\n");
                                super.setText(null);
                                Label l = new Label(item);
                                l.setWrapText(true);
                                VBox box = new VBox(l);
                                l.heightProperty().addListener((observable,oldValue,newValue)-> {
                                    box.setPrefHeight(newValue.doubleValue()+7);
                                    Platform.runLater(()->this.getTableRow().requestLayout());
                                });
                                super.setGraphic(box);
                            }
                        }
                    };
                    return tableCell;
                }
            };
	
	// DB's keys
	public final static String ID = "_id",
			// city's keys
			CITY = "city",
			COUNTRY = "country",
			PREFERENCES = "preferences";
	
	// encrypt the password
    public static String cryptPwd(String pwd) {
    	String ret = null;
    	// if the field is empty -> error
    	if(pwd.equals("") == true)
    		return ret;
    	try {
    		MessageDigest digest = MessageDigest.getInstance("SHA-1");
    		digest.reset();
    		digest.update(pwd.getBytes("utf-8"));
    		ret = String.format("%040x",new BigInteger(1,digest.digest()));
    	} catch(Exception e) {
    		System.out.println("Exception in method cryptoPwd"+e.getMessage());
    	}
    	return ret;
    }
    
    // Print the user's fields
    public static void printUser(User user) {
    	System.out.print(user.getRole()+":"+" name:"+user.getName()+" surname: "+user.getSurname()+"email:"+user.getEmail()+" pwd:"+user.getPassword());
    	if(user.getRole().contentEquals("customer")) {
    		Customer customer = (Customer) user;
    		System.out.println(" username:"+customer.getUsername()+" preferences: "+customer.getPreferences()); 
    	}
    }
}
