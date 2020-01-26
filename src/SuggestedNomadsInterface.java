import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class SuggestedNomadsInterface {

    private NomadAdvisor nomadAdvisor;
    private Customer loggedCustomer;

    @FXML
    private AnchorPane titleBox;

    @FXML
    private Label title;

    @FXML
    private TableView<?> tableNomads;

    @FXML
    private TableColumn<?, ?> usernameColumn;

    @FXML
    private TableColumn<?, ?> emailColumn;

    public void setNomadAdvisor(NomadAdvisor nomadAdvisor) {
        this.nomadAdvisor = nomadAdvisor;
    }

    public void initInterface() {
        loggedCustomer = (Customer) nomadAdvisor.getUser();
        System.out.println("UTENTE LOGGATO: " + loggedCustomer.getName()); //DEBUG
        //TODO Popolazione della tabella
    }
}
