package controllers;

import entities.ForumPost;
import entities.Reponse;
import services.ServiceReponse;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class ReponseFormController {

    @FXML private TextArea txtContenu;
    @FXML private Button btnSauvegarder;
    @FXML private Button btnAnnuler;

    private ServiceReponse service;
    private ForumPost postCourant;
    private ReponseListController reponseListController;

    @FXML
    public void initialize() {
        txtContenu.setPromptText("Écrivez votre réponse ici...");
    }

    public void setService(ServiceReponse service) {
        this.service = service;
    }

    public void setPost(ForumPost post) {
        this.postCourant = post;
    }

    public void setReponseListController(ReponseListController controller) {
        this.reponseListController = controller;
    }

    @FXML
    private void handleSauvegarder() {
        String contenu = txtContenu.getText().trim();

        if (contenu.isEmpty()) {
            showError("La réponse ne peut pas être vide");
            txtContenu.requestFocus();
            return;
        }

        if (contenu.length() < 5) {
            showError("La réponse doit contenir au moins 5 caractères");
            txtContenu.requestFocus();
            return;
        }

        Reponse nouvelleReponse = new Reponse(
                postCourant.getIdPost(),
                "Utilisateur",
                contenu,
                LocalDateTime.now()
        );

        service.ajouter(nouvelleReponse);

        if (reponseListController != null) {
            reponseListController.refreshTable();
        }

        showSuccess("Réponse publiée avec succès !");
        fermerFenetre();
    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        ((Stage) btnSauvegarder.getScene().getWindow()).close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText("❌ " + message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("✅ " + message);
        alert.showAndWait();
    }
}