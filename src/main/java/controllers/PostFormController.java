package controllers;

import entities.ForumPost;
import services.ServiceForumPost;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class PostFormController {

    public enum Mode { AJOUT, MODIFICATION }

    @FXML private Label lblTitre;
    @FXML private TextField txtNom;
    @FXML private ComboBox<String> cmbRole;
    @FXML private ComboBox<String> cmbCategorie;
    @FXML private TextArea txtContenu;
    @FXML private CheckBox chkArchive;
    @FXML private Button btnSauvegarder;
    @FXML private Button btnAnnuler;

    private ServiceForumPost service;
    private PostListController postListController;
    private MainForumController mainController;
    private ForumPost postCourant;
    private Mode modeCourant;

    @FXML
    public void initialize() {
        cmbRole.setItems(FXCollections.observableArrayList(
                "🧑 Patient", "👨‍⚕️ Médecin", "🧘 Thérapeute"
        ));
        cmbRole.setValue("🧑 Patient");

        cmbCategorie.setItems(FXCollections.observableArrayList(
                "😟 Anxiété", "🧠 Stress", "😢 Dépression", "🧘 Méditation", "💬 Général"
        ));
        cmbCategorie.setValue("💬 Général");

        txtNom.setPromptText("Votre nom ou pseudo");
        txtContenu.setPromptText("Écrivez votre message ici...");
    }

    public void setService(ServiceForumPost service) {
        this.service = service;
    }

    public void setPostListController(PostListController controller) {
        this.postListController = controller;
    }

    public void setMainController(MainForumController controller) {
        this.mainController = controller;
    }

    public void setMode(Mode mode) {
        this.modeCourant = mode;
        if (mode == Mode.AJOUT) {
            lblTitre.setText("➕ Nouvelle publication");
            btnSauvegarder.setText("Publier");
            chkArchive.setVisible(false);
            txtNom.clear();
            txtContenu.clear();
        } else {
            lblTitre.setText("✏️ Modifier la publication");
            btnSauvegarder.setText("Mettre à jour");
            chkArchive.setVisible(true);
        }
    }

    public void setPost(ForumPost post) {
        this.postCourant = post;
        txtNom.setText(post.getNom());
        cmbRole.setValue(post.getRole());
        cmbCategorie.setValue(post.getCategorie());
        txtContenu.setText(post.getContenu());
        chkArchive.setSelected(post.isArchive());
    }

    @FXML
    private void handleSauvegarder() {
        String nom = txtNom.getText().trim();
        String contenu = txtContenu.getText().trim();

        if (nom.isEmpty()) {
            showError("Le nom est requis");
            txtNom.requestFocus();
            return;
        }

        if (nom.length() < 3) {
            showError("Le nom doit contenir au moins 3 caractères");
            txtNom.requestFocus();
            return;
        }

        if (contenu.isEmpty()) {
            showError("Le contenu est requis");
            txtContenu.requestFocus();
            return;
        }

        if (contenu.length() < 10) {
            showError("Le contenu doit contenir au moins 10 caractères");
            txtContenu.requestFocus();
            return;
        }

        if (modeCourant == Mode.AJOUT) {
            ForumPost nouveauPost = new ForumPost(
                    nom,
                    cmbRole.getValue(),
                    cmbCategorie.getValue(),
                    contenu,
                    LocalDateTime.now(),
                    false
            );
            service.ajouter(nouveauPost);
            showSuccess("Publication publiée avec succès !");
        } else {
            if (postCourant == null) {
                showError("Publication non trouvée");
                return;
            }
            postCourant.setNom(nom);
            postCourant.setRole(cmbRole.getValue());
            postCourant.setCategorie(cmbCategorie.getValue());
            postCourant.setContenu(contenu);
            postCourant.setArchive(chkArchive.isSelected());
            service.modifier(postCourant);
            showSuccess("Publication modifiée avec succès !");
        }

        if (postListController != null) {
            postListController.refreshTable();
        }

        if (mainController != null) {
            mainController.refreshStatistics();
        }

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