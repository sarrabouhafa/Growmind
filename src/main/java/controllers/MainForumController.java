package controllers;

import services.ServiceForumPost;
import services.ServiceReponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainForumController {

    @FXML private AnchorPane rootPane;
    @FXML private Label lblDate;
    @FXML private Label lblUserRole;
    @FXML private Label lblPatientsCount;
    @FXML private Label lblMedecinsCount;
    @FXML private Label lblPostsCount;
    @FXML private Label lblReponsesCount;
    @FXML private TextField searchField;

    private String currentRole = "👤 Patient";
    private boolean isMedecin = false;
    private ServiceForumPost servicePost;
    private ServiceReponse serviceReponse;

    @FXML
    public void initialize() {
        servicePost = new ServiceForumPost();
        serviceReponse = new ServiceReponse();

        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblUserRole.setText(currentRole);

        updateStatistics();
    }

    private void updateStatistics() {
        int totalPosts = servicePost.compter();
        lblPostsCount.setText(String.valueOf(totalPosts));
        lblPatientsCount.setText("1,254");
        lblMedecinsCount.setText("89");

        int totalReponses = 0;
        try {
            totalReponses = 1254;
        } catch (Exception e) {
            totalReponses = 0;
        }
        lblReponsesCount.setText(totalReponses + "");
    }

    @FXML
    public void handleForum() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/post_list.fxml"));
            VBox postView = loader.load();

            PostListController controller = loader.getController();
            controller.setMainController(this);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(postView, 1300, 750));
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger le forum");
        }
    }

    @FXML
    public void handleNouvelleDiscussion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/post_form.fxml"));
            VBox page = loader.load();

            PostFormController controller = loader.getController();
            controller.setService(servicePost);
            controller.setMainController(this);
            controller.setMode(PostFormController.Mode.AJOUT);

            Stage stage = new Stage();
            stage.setTitle("Nouvelle discussion");
            stage.setScene(new Scene(page));
            stage.showAndWait();

            updateStatistics();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire");
        }
    }

    @FXML
    public void handleEspacePatient() {
        isMedecin = false;
        currentRole = "👤 Patient";
        lblUserRole.setText(currentRole);

        showInfo("Espace Patient",
                "👥 Bienvenue dans votre espace personnel\n\n" +
                        "Fonctionnalités disponibles :\n" +
                        "• 📝 Créer des publications\n" +
                        "• 💬 Répondre aux discussions\n" +
                        "• ❤️ Liker les conseils utiles\n" +
                        "• ⚠️ Signaler les contenus inappropriés");
    }

    @FXML
    public void handleEspaceMedecin() {
        isMedecin = true;
        currentRole = "👨‍⚕️ Médecin";
        lblUserRole.setText(currentRole);

        showInfo("Espace Médecin",
                "👨‍⚕️ Bienvenue dans votre espace professionnel\n\n" +
                        "Fonctionnalités disponibles :\n" +
                        "• 👨‍⚕️ Publier des conseils certifiés\n" +
                        "• 💬 Répondre aux patients\n" +
                        "• 🛡️ Modérer les contenus signalés\n" +
                        "• 🗑️ Supprimer les publications inappropriées");
    }

    @FXML
    public void handleSwitchRole() {
        if (isMedecin) {
            handleEspacePatient();
        } else {
            handleEspaceMedecin();
        }
    }

    @FXML
    public void handleRessources() {
        showInfo("📚 Ressources recommandées",
                "Applications de bien-être :\n\n" +
                        "🧘 Petit BamBou - Méditation guidée\n" +
                        "🌿 Mindfulness - Pleine conscience\n" +
                        "😌 Calm - Relaxation et sommeil\n" +
                        "💤 Headspace - Méditation pour débutants\n\n" +
                        "📖 Livres recommandés :\n" +
                        "• 'Le piège du bonheur' - Russ Harris\n" +
                        "• 'Méditer pour ne plus déprimer' - Mark Williams\n" +
                        "• 'L'anti-régime' - Michel Freud");
    }

    @FXML
    public void handleUrgence() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("🆘 AIDE D'URGENCE 24h/24 - 7j/7");
        alert.setHeaderText("Numéro national de prévention du suicide");
        alert.setContentText(
                "📞 3114 - Appel gratuit et confidentiel\n\n" +
                        "Des professionnels de santé sont disponibles\n" +
                        "pour vous écouter et vous aider.\n\n" +
                        "👉 N'attendez pas, vous n'êtes pas seul(e).");
        alert.showAndWait();
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            showInfo("Recherche", "🔍 Recherche de : " + query + "\n\nFonctionnalité à venir !");
        }
    }

    public void refreshStatistics() {
        updateStatistics();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}