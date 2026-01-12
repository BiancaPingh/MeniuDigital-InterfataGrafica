package unitbv.devops.meniudigitalui.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import unitbv.devops.meniudigitalui.entity.Produs;
import unitbv.devops.meniudigitalui.service.MenuService;

import java.util.List;
import java.util.Optional;

public class GuestUI {
    private final SessionFactory sessionFactory;
    private final MenuService menuService = new MenuService();
    private Stage stage;

    private List<Produs> allProducts;
    private List<Produs> filteredProducts;

    private TableView<Produs> menuTable;

    private Label detailName;
    private Label detailPrice;
    private Label detailType;
    private Label detailVeg;
    private Label detailGramaj;
    private TextArea detailDescriere;
    private TextArea detailIngrediente;

    private TextField searchField;
    private CheckBox vegetarianCheck;
    private ComboBox<String> typeCombo;
    private Spinner<Integer> minPrice;
    private Spinner<Integer> maxPrice;

    public GuestUI(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void show(Stage primaryStage) {
        this.stage = primaryStage;

        allProducts = menuService.getAllProducts();
        filteredProducts = allProducts;

        BorderPane root = new BorderPane();
        root.setTop(createTopBar());
        root.setLeft(createLeftPanel());
        root.setCenter(createCenterPanel());
        root.setRight(createDetailsPanel());

        Scene scene = new Scene(root, 1200, 650);
        primaryStage.setTitle("Restaurant - Guest Menu");
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshTable();
    }

    private VBox createTopBar() {
        VBox topBar = new VBox();
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        Label title = new Label("Browse Menu");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        topBar.getChildren().add(title);

        return topBar;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");
        leftPanel.setPrefWidth(200); // Reduced from 260

        Label filterLabel = new Label("Filters");
        filterLabel.setStyle("-fx-font-weight: bold;");

        // ... (rest of the method content remains the same until return)

        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.textProperty().addListener((obs, old, n) -> applyFiltersAndOptionalSearch());

        vegetarianCheck = new CheckBox("Vegetarian only");
        vegetarianCheck.selectedProperty().addListener((obs, old, n) -> applyFiltersAndOptionalSearch());

        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("All", "Mancare", "Bautura");
        typeCombo.setValue("All");
        typeCombo.setOnAction(e -> applyFiltersAndOptionalSearch());

        HBox priceBox = new HBox(5);
        minPrice = new Spinner<>(0, 1000, 0);
        maxPrice = new Spinner<>(0, 1000, 1000);
        minPrice.setPrefWidth(70);
        maxPrice.setPrefWidth(70);
        minPrice.valueProperty().addListener((o, a, b) -> applyFiltersAndOptionalSearch());
        maxPrice.valueProperty().addListener((o, a, b) -> applyFiltersAndOptionalSearch());
        priceBox.getChildren().addAll(new Label("Min:"), minPrice, new Label("Max:"), maxPrice);

        Button clearFilters = new Button("Reset");
        clearFilters.setPrefWidth(180);
        clearFilters.setOnAction(e -> {
            searchField.clear();
            vegetarianCheck.setSelected(false);
            typeCombo.setValue("All");
            minPrice.getValueFactory().setValue(0);
            maxPrice.getValueFactory().setValue(1000);
            applyFiltersAndOptionalSearch();
        });

        Button backButton = new Button("Back to Login");
        backButton.setPrefWidth(180);
        backButton.setOnAction(e -> backToLogin());

        leftPanel.getChildren().addAll(
                filterLabel,
                new Label("Search:"),
                searchField,
                new Separator(),
                vegetarianCheck,
                new Label("Type:"),
                typeCombo,
                new Label("Price Range:"),
                priceBox,
                new Separator(),
                clearFilters,
                backButton);

        VBox.setVgrow(backButton, javafx.scene.layout.Priority.ALWAYS);
        return leftPanel;
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(10));

        menuTable = new TableView<>();
        menuTable.setMinHeight(500); // Force minimum height
        menuTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        menuTable.setPrefHeight(Double.MAX_VALUE);

        TableColumn<Produs, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNume()));
        nameCol.setMinWidth(200);

        TableColumn<Produs, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getClass().getSimpleName()));
        typeCol.setMinWidth(100);

        TableColumn<Produs, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPret()));
        priceCol.setMinWidth(80);

        TableColumn<Produs, Boolean> vegCol = new TableColumn<>("Veg");
        vegCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getVegetarian()));
        vegCol.setMinWidth(60);

        menuTable.getColumns().addAll(nameCol, typeCol, priceCol, vegCol);

        menuTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                showProductDetails(selected);
            }
        });

        centerPanel.getChildren().addAll(new Label("Menu Items:"), menuTable);
        VBox.setVgrow(menuTable, javafx.scene.layout.Priority.ALWAYS);

        return centerPanel;
    }

    private VBox createDetailsPanel() {
        VBox details = new VBox(8);
        details.setPadding(new Insets(10));
        details.setPrefWidth(250); // Reduced from 350
        details.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 0 1;");

        Label title = new Label("Details");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        detailName = new Label("-");
        detailName.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        detailPrice = new Label("Price: -");
        detailType = new Label("Type: -");
        detailVeg = new Label("Vegetarian: -");
        detailGramaj = new Label("Weight/Volume: -");

        detailDescriere = new TextArea();
        detailDescriere.setEditable(false);
        detailDescriere.setWrapText(true);
        detailDescriere.setPrefRowCount(3);

        detailIngrediente = new TextArea();
        detailIngrediente.setEditable(false);
        detailIngrediente.setWrapText(true);
        detailIngrediente.setPrefRowCount(5);

        details.getChildren().addAll(
                title,
                detailName,
                detailPrice,
                detailType,
                detailVeg,
                detailGramaj,
                new Label("Description:"),
                detailDescriere,
                new Label("Ingredients:"),
                detailIngrediente);

        return details;
    }

    private void applyFiltersAndOptionalSearch() {
        Optional<String> nameQuery = Optional.ofNullable(searchField.getText()).filter(s -> !s.isBlank());
        Optional<Boolean> vegOnly = Optional.of(vegetarianCheck.isSelected());

        Optional<String> type = Optional.ofNullable(typeCombo.getValue())
                .filter(v -> v != null && !"All".equalsIgnoreCase(v));

        Optional<Double> min = Optional.of(minPrice.getValue().doubleValue());
        Optional<Double> max = Optional.of(maxPrice.getValue().doubleValue());

        filteredProducts = menuService.filterInMemory(allProducts, vegOnly, type, min, max, nameQuery);
        refreshTable();

        nameQuery.flatMap(q -> menuService.searchBestMatchOptional(filteredProducts, q))
                .ifPresent(p -> menuTable.getSelectionModel().select(p));
    }

    private void refreshTable() {
        menuTable.setItems(javafx.collections.FXCollections.observableArrayList(filteredProducts));
        if (!filteredProducts.isEmpty() && menuTable.getSelectionModel().getSelectedItem() == null) {
            menuTable.getSelectionModel().select(0);
        }
    }

    private void showProductDetails(Produs produs) {
        detailName.setText(produs.getNume() == null ? "-" : produs.getNume());
        detailPrice.setText("Price: " + produs.getPret() + " RON");
        detailType.setText("Type: " + produs.getClass().getSimpleName());
        detailVeg.setText("Vegetarian: " + (Boolean.TRUE.equals(produs.getVegetarian()) ? "Yes" : "No"));
        detailGramaj.setText("Weight/Volume: " + (produs.getGramajVolum() == null ? "-" : produs.getGramajVolum()));
        detailDescriere.setText(produs.getDescriere() == null ? "" : produs.getDescriere());
        detailIngrediente.setText(produs.getIngrediente() == null ? "" : produs.getIngrediente());
    }

    private void backToLogin() {
        LoginUI loginUI = new LoginUI(sessionFactory);
        loginUI.show(stage);
    }
}
