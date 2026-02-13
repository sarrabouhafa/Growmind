package controllers;

import entities.ForumPost;
import entities.Reponse;
import services.ServiceReponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ReponseListController {

    @FXML private TableView<Reponse> tableReponses;
    @FXML private TableColumn<Reponse, String> colAuteur;
    @FXML private TableColumn<Reponse, String> colContenu;
    @FXML private TableColumn<Reponse, String> colDate;
    @FXML private TableColumn<Reponse, Void> colActions;
    @FXML private Label lblTitrePost;
    @FXML private Label lblTotalReponses;
    @FXML private Button btnNouvelleReponse;

    private ServiceReponse service;
    private ForumPost postCourant;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        service = new ServiceReponse();
        setupTableColumns();
        setupActionsColumn();
    }

    public void setPost(ForumPost post) {
        this.postCourant = post;
        String titre = post.getContenu();
        lblTitrePost.setText("Publication: " + (titre.length() > 50 ? titre.substring(0, 47) + "..." : titre));
        loadReponses();
    }

    private void setupTableColumns() {
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colContenu.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        colDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDateReponse().format(formatter)
                )
        );
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑️");

            {
                btnDelete.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-cursor: hand;");
                btnDelete.setOnAction(e -> {
                    Reponse reponse = getTableView().getItems().get(getIndex());
                    handleSupprimer(reponse);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    private void loadReponses() {
        if (postCourant != null) {
            ObservableList<Reponse> reponses = FXCollections.observableArrayList(
                    service.getAllByPost(postCourant.getIdPost())
            );
            tableReponses.setItems(reponses);
            int size = reponses.size();
            lblTotalReponses.setText("Total: " + size + " réponse" + (size > 1 ? "s" : ""));
        }
    }

    @FXML
    private void handleNouvelleReponse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reponse_form.fxml"));
            VBox page = loader.load();

            ReponseFormController controller = loader.getController();
            controller.setService(service);
            controller.setPost(postCourant);
            controller.setReponseListController(this);

            Stage stage = new Stage();
            stage.setTitle("Nouvelle réponse");
            stage.setScene(new Scene(page));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSupprimer(Reponse reponse) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la réponse ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réponse ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.supprimer(reponse);
                loadReponses();
            }
        });
    }

    public void refreshTable() {
        loadReponses();
    }
}