package controllers;

import entities.ForumPost;
import services.ServiceForumPost;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class PostListController {

    @FXML private VBox discussionsContainer;
    @FXML private Label lblTotalPosts;
    @FXML private Button btnRetour;
    @FXML private ComboBox<String> filterCategory;
    @FXML private ComboBox<String> filterRole;
    @FXML private TextField searchField;

    private ServiceForumPost service;
    private MainForumController mainController;
    private ObservableList<ForumPost> allPosts;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        service = new ServiceForumPost();
        loadPosts();
        setupFilters();
    }

    public void setMainController(MainForumController controller) {
        this.mainController = controller;
    }

    private void loadPosts() {
        allPosts = service.recuperer();
        afficherCartes(allPosts);
        lblTotalPosts.setText("📊 " + allPosts.size() + " discussion" + (allPosts.size() > 1 ? "s" : ""));
    }

    private void afficherCartes(ObservableList<ForumPost> posts) {
        discussionsContainer.getChildren().clear();

        for (ForumPost post : posts) {
            VBox carte = createCarte(post);
            discussionsContainer.getChildren().add(carte);
        }
    }

    private VBox createCarte(ForumPost post) {
        // Carte principale
        VBox carte = new VBox(15);
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 25; " +
                "-fx-padding: 20; -fx-border-color: #E2E8F0; -fx-border-width: 1; " +
                "-fx-border-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 3);");

        // Effet au survol
        carte.setOnMouseEntered(e ->
                carte.setStyle("-fx-background-color: white; -fx-background-radius: 25; " +
                        "-fx-padding: 20; -fx-border-color: #667eea; -fx-border-width: 2; " +
                        "-fx-border-radius: 25; -fx-effect: dropshadow(gaussian, rgba(102,126,234,0.2), 15, 0, 0, 5);")
        );
        carte.setOnMouseExited(e ->
                carte.setStyle("-fx-background-color: white; -fx-background-radius: 25; " +
                        "-fx-padding: 20; -fx-border-color: #E2E8F0; -fx-border-width: 1; " +
                        "-fx-border-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 3);")
        );

        // En-tête avec avatar et auteur
        HBox header = new HBox(15);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Avatar circulaire
        Circle avatar = new Circle(25);
        if (post.getRole().contains("Médecin")) {
            avatar.setFill(javafx.scene.paint.Color.web("#27AE60"));
        } else if (post.getRole().contains("Thérapeute")) {
            avatar.setFill(javafx.scene.paint.Color.web("#2980B9"));
        } else {
            avatar.setFill(javafx.scene.paint.Color.web("#E74C3C"));
        }
        avatar.setOpacity(0.2);

        StackPane avatarContainer = new StackPane(avatar);
        Label avatarIcon = new Label(getRoleIcon(post.getRole()));
        avatarIcon.setStyle("-fx-font-size: 24px;");
        avatarContainer.getChildren().add(avatarIcon);

        // Infos auteur
        VBox authorInfo = new VBox(3);
        Label nomLabel = new Label(post.getNom());
        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: #1A202C;");

        HBox roleBox = new HBox(10);
        roleBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label roleLabel = new Label(post.getRole());
        roleLabel.setStyle(getRoleStyle(post.getRole()));

        Label badgeLabel = new Label(post.getBadge());
        badgeLabel.setStyle("-fx-background-color: #EDF2F7; -fx-padding: 4 12; " +
                "-fx-background-radius: 20; -fx-text-fill: #4A5568; " +
                "-fx-font-size: 12px; -fx-font-weight: 700;");

        roleBox.getChildren().addAll(roleLabel, badgeLabel);
        authorInfo.getChildren().addAll(nomLabel, roleBox);

        header.getChildren().addAll(avatarContainer, authorInfo);

        // Date
        Label dateLabel = new Label(post.getDateCreation().format(formatter));
        dateLabel.setStyle("-fx-text-fill: #718096; -fx-font-size: 13px; -fx-font-style: italic;");

        HBox dateBox = new HBox();
        dateBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        dateBox.getChildren().add(dateLabel);

        // Contenu
        VBox contentBox = new VBox(10);
        contentBox.setStyle("-fx-padding: 10 0 10 50;");

        Label contenuLabel = new Label(post.getContenu());
        contenuLabel.setWrapText(true);
        contenuLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #2D3748;");

        // Catégorie
        Label categorieLabel = new Label(getCategorieIcon(post.getCategorie()) + " " +
                post.getCategorie().replaceAll("[😟🧠😢🧘💤💪👨‍👩‍👧💼🍎🏃]", "").trim());
        categorieLabel.setStyle("-fx-background-color: " + getCategorieCouleur(post.getCategorie()) + "; " +
                "-fx-padding: 6 18; -fx-background-radius: 25; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-font-weight: 700;");

        contentBox.getChildren().addAll(contenuLabel, categorieLabel);

        // Statistiques
        HBox statsBox = new HBox(25);
        statsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        statsBox.setStyle("-fx-padding: 10 0 0 50;");

        Label likesLabel = new Label("❤️ " + post.getLikes());
        likesLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: 700;");

        Label vuesLabel = new Label("👁️ " + post.getVues());
        vuesLabel.setStyle("-fx-text-fill: #718096;");

        Label reponsesLabel = new Label("💬 0"); // À connecter avec ServiceReponse
        reponsesLabel.setStyle("-fx-text-fill: #667eea; -fx-font-weight: 700;");

        statsBox.getChildren().addAll(likesLabel, vuesLabel, reponsesLabel);

        // Actions
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        actionsBox.setStyle("-fx-padding: 15 0 0 0;");

        Button btnView = new Button("👁️ Voir");
        btnView.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: 700; " +
                "-fx-background-radius: 30; -fx-padding: 8 25; -fx-cursor: hand;");
        btnView.setOnAction(e -> handleVoirDetails(post));

        Button btnEdit = new Button("✏️ Modifier");
        btnEdit.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-weight: 700; " +
                "-fx-background-radius: 30; -fx-padding: 8 25; -fx-cursor: hand;");
        btnEdit.setOnAction(e -> handleModifier(post));

        Button btnDelete = new Button("🗑️ Supprimer");
        btnDelete.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: 700; " +
                "-fx-background-radius: 30; -fx-padding: 8 25; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> handleSupprimer(post));

        Button btnLike = new Button("❤️ " + post.getLikes());
        btnLike.setStyle("-fx-background-color: #FFF0F0; -fx-text-fill: #E74C3C; -fx-font-weight: 700; " +
                "-fx-background-radius: 30; -fx-padding: 8 20; -fx-cursor: hand; " +
                "-fx-border-color: #FFB8B8; -fx-border-width: 1; -fx-border-radius: 30;");
        btnLike.setOnAction(e -> {
            service.incrementerLike(post.getIdPost());
            post.setLikes(post.getLikes() + 1);
            btnLike.setText("❤️ " + post.getLikes());
        });

        actionsBox.getChildren().addAll(btnView, btnEdit, btnDelete, btnLike);

        // Assemblage de la carte
        HBox headerRow = new HBox();
        headerRow.getChildren().addAll(header, dateBox);
        HBox.setHgrow(header, Priority.ALWAYS);

        carte.getChildren().addAll(headerRow, contentBox, statsBox, actionsBox);

        return carte;
    }

    private String getRoleIcon(String role) {
        if (role.contains("Médecin")) return "👨‍⚕️";
        if (role.contains("Thérapeute")) return "🧘";
        return "🧑";
    }

    private String getRoleStyle(String role) {
        if (role.contains("Médecin")) {
            return "-fx-background-color: #E8F5E9; -fx-text-fill: #27AE60; -fx-font-weight: 700; " +
                    "-fx-background-radius: 20; -fx-padding: 4 15; -fx-font-size: 13px;";
        } else if (role.contains("Thérapeute")) {
            return "-fx-background-color: #E5F0FF; -fx-text-fill: #2980B9; -fx-font-weight: 700; " +
                    "-fx-background-radius: 20; -fx-padding: 4 15; -fx-font-size: 13px;";
        } else {
            return "-fx-background-color: #FFE5E5; -fx-text-fill: #E74C3C; -fx-font-weight: 700; " +
                    "-fx-background-radius: 20; -fx-padding: 4 15; -fx-font-size: 13px;";
        }
    }

    private String getCategorieIcon(String categorie) {
        if (categorie.contains("Anxiété")) return "😟";
        if (categorie.contains("Stress")) return "🧠";
        if (categorie.contains("Dépression")) return "😢";
        if (categorie.contains("Méditation")) return "🧘";
        if (categorie.contains("Sommeil")) return "💤";
        if (categorie.contains("Estime")) return "💪";
        if (categorie.contains("Famille")) return "👨‍👩‍👧";
        if (categorie.contains("Travail")) return "💼";
        if (categorie.contains("Alimentation")) return "🍎";
        if (categorie.contains("Sport")) return "🏃";
        return "💬";
    }

    private String getCategorieCouleur(String categorie) {
        if (categorie.contains("Anxiété")) return "#DC2626";
        if (categorie.contains("Stress")) return "#D97706";
        if (categorie.contains("Dépression")) return "#2563EB";
        if (categorie.contains("Méditation")) return "#059669";
        if (categorie.contains("Sommeil")) return "#4F46E5";
        if (categorie.contains("Estime")) return "#B45309";
        if (categorie.contains("Famille")) return "#DB2777";
        if (categorie.contains("Travail")) return "#7C3AED";
        if (categorie.contains("Alimentation")) return "#16A34A";
        if (categorie.contains("Sport")) return "#EA580C";
        return "#6B7280";
    }

    private void setupFilters() {
        filterCategory.setItems(FXCollections.observableArrayList(
                "Toutes", "😟 Anxiété", "🧠 Stress", "😢 Dépression", "🧘 Méditation",
                "💤 Sommeil", "💪 Estime de soi", "👨‍👩‍👧 Famille", "💼 Travail",
                "🍎 Alimentation", "🏃 Sport", "💬 Général"
        ));
        filterCategory.setValue("Toutes");
        filterCategory.setOnAction(e -> applyFilters());

        filterRole.setItems(FXCollections.observableArrayList(
                "Tous", "🧑 Patient", "👨‍⚕️ Médecin", "🧘 Thérapeute"
        ));
        filterRole.setValue("Tous");
        filterRole.setOnAction(e -> applyFilters());

        searchField.textProperty().addListener((obs, old, nv) -> applyFilters());
    }

    private void applyFilters() {
        if (allPosts == null) return;

        ObservableList<ForumPost> filtered = FXCollections.observableArrayList();

        String cat = filterCategory.getValue();
        String role = filterRole.getValue();
        String search = searchField.getText().toLowerCase();

        for (ForumPost post : allPosts) {
            boolean matchCat = cat.equals("Toutes") || post.getCategorie().contains(cat.replaceAll("[😟🧠😢🧘💤💪👨‍👩‍👧💼🍎🏃]", "").trim());
            boolean matchRole = role.equals("Tous") || post.getRole().contains(role.replaceAll("[🧑👨‍⚕️🧘]", "").trim());
            boolean matchSearch = search.isEmpty() ||
                    post.getNom().toLowerCase().contains(search) ||
                    post.getContenu().toLowerCase().contains(search);

            if (matchCat && matchRole && matchSearch) {
                filtered.add(post);
            }
        }

        afficherCartes(filtered);
        lblTotalPosts.setText("📊 " + filtered.size() + " discussion" + (filtered.size() > 1 ? "s" : ""));
    }

    @FXML
    private void handleNouveauPost() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/post_form.fxml"));
            VBox page = loader.load();

            PostFormController controller = loader.getController();
            controller.setService(service);
            controller.setPostListController(this);
            controller.setMode(PostFormController.Mode.AJOUT);

            Stage stage = new Stage();
            stage.setTitle("Nouvelle discussion - GrowMind");
            stage.setScene(new Scene(page));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleVoirDetails(ForumPost post) {
        // À implémenter
    }

    private void handleModifier(ForumPost post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/post_form.fxml"));
            VBox page = loader.load();

            PostFormController controller = loader.getController();
            controller.setService(service);
            controller.setPostListController(this);
            controller.setMode(PostFormController.Mode.MODIFICATION);
            controller.setPost(post);

            Stage stage = new Stage();
            stage.setTitle("Modifier - GrowMind");
            stage.setScene(new Scene(page));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSupprimer(ForumPost post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer cette discussion ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.supprimer(post);
                loadPosts();
                if (mainController != null) mainController.refreshStatistics();
            }
        });
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_forum.fxml"));
            AnchorPane dashboard = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(dashboard, 1300, 750));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshTable() {
        loadPosts();
        applyFilters();
    }
}