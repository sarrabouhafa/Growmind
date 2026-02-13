package controllers;

import entities.ForumPost;
import services.ServiceForumPost;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;

public class PostDetailsController {

    @FXML private Label lblNom;
    @FXML private Label lblRole;
    @FXML private Label lblCategorie;
    @FXML private Label lblDateCreation;
    @FXML private Label lblStatut;
    @FXML private Label lblLikes;
    @FXML private Label lblVues;
    @FXML private Label lblBadge;
    @FXML private TextArea txtContenu;
    @FXML private Button btnFermer;

    private ForumPost post;
    private ServiceForumPost service;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        txtContenu.setEditable(false);
    }

    public void setPost(ForumPost post) {
        this.post = post;
        afficherDetails();
    }

    public void setService(ServiceForumPost service) {
        this.service = service;
    }

    private void afficherDetails() {
        lblNom.setText(post.getNom());
        lblRole.setText(post.getRole());
        lblCategorie.setText(post.getCategorie() + " " + getIconeCategorie(post.getCategorie()));
        lblDateCreation.setText(post.getDateCreation().format(formatter));
        txtContenu.setText(post.getContenu());
        lblLikes.setText("❤️ " + post.getLikes());
        lblVues.setText("👁️ " + post.getVues());
        lblBadge.setText(post.getBadge());

        if (post.isArchive()) {
            lblStatut.setText("Archivé");
            lblStatut.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-padding: 3 12; -fx-background-radius: 20;");
        } else {
            lblStatut.setText("Actif");
            lblStatut.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-padding: 3 12; -fx-background-radius: 20;");
        }
    }

    private String getIconeCategorie(String categorie) {
        switch (categorie) {
            case "😟 Anxiété": return "😟";
            case "🧠 Stress": return "🧠";
            case "😢 Dépression": return "😢";
            case "🧘 Méditation": return "🧘";
            case "💤 Sommeil": return "💤";
            default: return "💬";
        }
    }

    @FXML
    private void handleFermer() {
        ((Stage) btnFermer.getScene().getWindow()).close();
    }
}