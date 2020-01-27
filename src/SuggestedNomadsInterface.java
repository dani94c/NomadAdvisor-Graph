import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.List;

public class SuggestedNomadsInterface {

    private NomadAdvisor nomadAdvisor;
    private Customer loggedCustomer;
    private ObservableList<Customer> customerList;
    private ObservableList<String> compatibilityList;

    @FXML
    private AnchorPane titleBox;
    @FXML
    private Label title;
    @FXML
    private TableView<Customer> tableNomads;
    @FXML
    private TableColumn<Customer, String> usernameColumn;
    @FXML
    private TableColumn<Customer, String> emailColumn;
    @FXML
    private TableColumn<Customer, List<String>> preferencesColumn;

    // This method is used in order to improve the visibility of the table in the prefences column
    public static final Callback<TableColumn<Customer, List<String>>, TableCell<Customer, List<String>>> WRAPPING_CELL_FACTORY =
            new Callback<TableColumn<Customer, List<String>>, TableCell<Customer, List<String>>>() {
                @Override
                public TableCell<Customer, List<String>> call(TableColumn<Customer, List<String>> param) {
                    TableCell<Customer, List<String>> tableCell = new TableCell<Customer, List<String>>() {
                        @Override
                        protected void updateItem(List<String> item, boolean empty) {
                            if (item == getItem()) return;
                            super.updateItem(item, empty);
                            if (item == null) {
                                super.setText(null);
                                super.setGraphic(null);
                            } else {
                                super.setText(null);
                                Label l = new Label(item.toString());
                                l.setWrapText(true);
                                VBox box = new VBox(l);
                                l.heightProperty().addListener((observable, oldValue, newValue) -> {
                                    box.setPrefHeight(newValue.doubleValue() + 7);
                                    Platform.runLater(() -> this.getTableRow().requestLayout());
                                });
                                super.setGraphic(box);
                            }
                        }
                    };
                    return tableCell;
                }
            };

    // Method used to pass the NomadAdvisor object to the interface
    public void setNomadAdvisor(NomadAdvisor nomadAdvisor) {
        this.nomadAdvisor = nomadAdvisor;
    }

    // This method update the table adding the customers stored in the list
    public void customerListUpdate(List<Customer> customers) {
        customerList.clear();
        customerList.addAll(customers);
    }

    // This method initializes the table of the suggested customer
    public void initTable() {
        customerList = FXCollections.observableArrayList();
        usernameColumn.setCellValueFactory(new PropertyValueFactory("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory("email"));
        preferencesColumn.setCellValueFactory(new PropertyValueFactory("preferences"));
    }

    // Initializes the Interface
    public void initInterface() {
        loggedCustomer = (Customer) nomadAdvisor.getUser();
        initTable();
        customerListUpdate(NomadHandler.SuggestedCustomers(loggedCustomer));
        tableNomads.setItems(customerList);
        preferencesColumn.setCellFactory(WRAPPING_CELL_FACTORY);
    }
}
