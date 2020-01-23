import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class SuggestedNomadsInterface {

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

}
