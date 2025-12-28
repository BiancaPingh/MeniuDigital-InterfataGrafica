package unitbv.devops.meniudigitalui.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import unitbv.devops.meniudigitalui.entity.*;
import unitbv.devops.meniudigitalui.service.AdminService;
import unitbv.devops.meniudigitalui.service.MenuService;
import unitbv.devops.meniudigitalui.service.DiscountService;
import unitbv.devops.meniudigitalui.service.OrderService;
import unitbv.devops.meniudigitalui.service.AppContext;
import unitbv.devops.meniudigitalui.service.AsyncTaskExecutor;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.stage.FileChooser;

public class AdminUI {
    private final SessionFactory sessionFactory;
    private final User currentUser;
    private final AdminService adminService = new AdminService();
    private final MenuService menuService = new MenuService();
    private final DiscountService discountService = new DiscountService();
    private final OrderService orderService = new OrderService();
    private Stage stage;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AdminUI(SessionFactory sessionFactory, User currentUser) {
        this.sessionFactory = sessionFactory;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        this.stage = primaryStage;

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                createStaffTab(),
                createMenuTab(),
                createPromotionsTab(),
                createOrdersTab());

        BorderPane root = new BorderPane();
        VBox topBar = createTopBar();
        root.setTop(topBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Restaurant Admin - " + currentUser.getUsername());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createTopBar() {
        VBox topBar = new VBox();
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Admin Dashboard - " + currentUser.getUsername());
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> backToLogin());

        hbox.getChildren().addAll(title, spacer, logoutButton);
        topBar.getChildren().add(hbox);

        return topBar;
    }

    private Tab createStaffTab() {
        Tab tab = new Tab("Staff Management", new VBox());
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        List<User> staff = adminService.getAllStaff();

        TableView<User> staffTable = new TableView<>();

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));

        TableColumn<User, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    editStaff(user, staffTable);
                });
                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteStaff(user, staffTable);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        staffTable.getColumns().addAll(idCol, usernameCol, actionCol);
        staffTable.setItems(javafx.collections.FXCollections.observableArrayList(staff));

        HBox addBox = new HBox(10);
        addBox.setAlignment(Pos.CENTER_LEFT);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(150);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(150);

        Button addButton = new Button("Add Staff");
        addButton.setOnAction(e -> {
            if (!usernameField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
                adminService.addStaff(usernameField.getText(), passwordField.getText());
                usernameField.clear();
                passwordField.clear();
                refreshStaffTable(staffTable);
            }
        });

        addBox.getChildren().addAll(usernameField, passwordField, addButton);

        content.getChildren().addAll(
                new Label("Staff Members:"),
                staffTable,
                new Separator(),
                new Label("Add New Staff:"),
                addBox);

        tab.setContent(content);
        return tab;
    }

    private void editStaff(User user, TableView<User> table) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Staff");

        ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        TextField username = new TextField(user.getUsername());
        PasswordField password = new PasswordField();
        password.setPromptText("New password (optional)");

        VBox box = new VBox(10,
                new Label("Username"), username,
                new Label("Password"), password);
        box.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(box);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == save) {
            user.setUsername(username.getText().trim());
            if (!password.getText().isBlank()) {
                user.setPassword(password.getText());
            }
            adminService.updateStaff(user);
            refreshStaffTable(table);
        }
    }

    private Tab createMenuTab() {
        Tab tab = new Tab("Menu Management", new VBox());
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        TableView<Produs> menuTable = new TableView<>();

        TableColumn<Produs, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNume()));

        TableColumn<Produs, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPret()));

        TableColumn<Produs, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getClass().getSimpleName()));

        TableColumn<Produs, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setOnAction(e -> {
                    Produs product = getTableView().getItems().get(getIndex());
                    editProduct(product, menuTable);
                });
                deleteBtn.setOnAction(e -> {
                    Produs product = getTableView().getItems().get(getIndex());
                    menuService.deleteProduct(product.getId());
                    refreshMenuTable(menuTable);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        menuTable.getColumns().addAll(nameCol, priceCol, typeCol, actionCol);
        refreshMenuTable(menuTable);

        HBox addBox = new HBox(10);
        addBox.setAlignment(Pos.CENTER_LEFT);

        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");
        nameField.setPrefWidth(150);

        Spinner<Double> priceSpinner = new Spinner<>(0.0, 1000.0, 0.0, 1.0);
        priceSpinner.setPrefWidth(100);

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Mancare", "Bautura");
        typeCombo.setValue("Mancare");
        typeCombo.setPrefWidth(100);

        CheckBox vegCheckbox = new CheckBox("Vegetarian");

        Button addButton = new Button("Add Product");
        addButton.setOnAction(e -> {
            if (!nameField.getText().isEmpty()) {
                Produs product = "Mancare".equals(typeCombo.getValue())
                        ? new Mancare(nameField.getText(), priceSpinner.getValue(), "", vegCheckbox.isSelected())
                        : new Bauturi(nameField.getText(), priceSpinner.getValue(), "", vegCheckbox.isSelected());
                menuService.saveProduct(product);
                nameField.clear();
                priceSpinner.getValueFactory().setValue(0.0);
                vegCheckbox.setSelected(false);
                refreshMenuTable(menuTable);
            }
        });

        Button exportBtn = new Button("Export JSON");
        exportBtn.setOnAction(e -> exportMenuJson());

        Button importBtn = new Button("Import JSON");
        importBtn.setOnAction(e -> importMenuJson(menuTable));

        addBox.getChildren().addAll(nameField, priceSpinner, typeCombo, vegCheckbox, addButton, exportBtn, importBtn);

        content.getChildren().addAll(
                new Label("Products:"),
                menuTable,
                new Separator(),
                new Label("Add / Import / Export:"),
                addBox);

        tab.setContent(content);
        return tab;
    }

    private void editProduct(Produs product, TableView<Produs> menuTable) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");

        ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        TextField name = new TextField(product.getNume());
        Spinner<Double> price = new Spinner<>(0.0, 1000.0, product.getPret(), 1.0);
        CheckBox veg = new CheckBox("Vegetarian");
        veg.setSelected(Boolean.TRUE.equals(product.getVegetarian()));

        VBox box = new VBox(10,
                new Label("Name"), name,
                new Label("Price"), price,
                veg);
        box.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(box);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == save) {
            product.setNume(name.getText().trim());
            product.setPret(price.getValue());
            product.setVegetarian(veg.isSelected());
            menuService.updateProduct(product);
            refreshMenuTable(menuTable);
        }
    }

    private void exportMenuJson() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Menu JSON");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            LoadingDialog loading = new LoadingDialog(stage, "Exporting menu to JSON...");
            loading.show();

            AsyncTaskExecutor.executeAsync(
                    () -> {
                        try {
                            menuService.exportMenuToJson(file);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return (Void) null;
                    },
                    (result) -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Menu exported successfully!");
                        alert.showAndWait();
                    },
                    ex -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Export failed");
                        alert.setContentText(ex.getMessage());
                        alert.showAndWait();
                    },
                    loading::close);
        }
    }

    private void importMenuJson(TableView<Produs> menuTable) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import Menu JSON");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            LoadingDialog loading = new LoadingDialog(stage, "Importing menu from JSON...");
            loading.show();

            AsyncTaskExecutor.executeAsync(
                    () -> {
                        try {
                            menuService.importMenuFromJson(file);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return (Void) null;
                    },
                    (result) -> {
                        refreshMenuTable(menuTable);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Menu imported successfully!");
                        alert.showAndWait();
                    },
                    ex -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Import failed");
                        alert.setContentText(ex.getMessage());
                        alert.showAndWait();
                    },
                    loading::close);
        }
    }

    private Tab createOrdersTab() {
        Tab tab = new Tab("Order History", new VBox());
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        TableView<Comanda> orderTable = new TableView<>();
        orderTable.setMinHeight(400);
        orderTable.setPrefHeight(600);
        orderTable.setPlaceholder(new Label("No orders in the system yet."));

        TableColumn<Comanda, Integer> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
        idCol.setPrefWidth(80);

        TableColumn<Comanda, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getDataOra() == null ? "" : cd.getValue().getDataOra().format(dtf)));
        dateCol.setPrefWidth(150);

        TableColumn<Comanda, String> tableCol = new TableColumn<>("Table");
        tableCol.setCellValueFactory(cd -> {
            Comanda comanda = cd.getValue();
            String tableNum = (comanda.getMasa() != null) ? String.valueOf(comanda.getMasa().getNumar()) : "-";
            return new javafx.beans.property.SimpleStringProperty(tableNum);
        });
        tableCol.setPrefWidth(80);

        TableColumn<Comanda, String> staffCol = new TableColumn<>("Staff");
        staffCol.setCellValueFactory(cd -> {
            String username = (cd.getValue().getUser() != null) ? cd.getValue().getUser().getUsername() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(username);
        });
        staffCol.setPrefWidth(120);

        TableColumn<Comanda, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cd -> {
            Double total = cd.getValue().getTotal();
            String formatted = String.format("%.2f RON", total != null ? total : 0.0);
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        totalCol.setPrefWidth(120);

        orderTable.getColumns().addAll(idCol, dateCol, tableCol, staffCol, totalCol);

        Button refreshBtn = new Button("Refresh History");
        refreshBtn.setOnAction(e -> {
            System.out.println("[ADMIN] Refreshing global order history");
            refreshOrderTable(orderTable);
        });

        Label titleLabel = new Label("All Orders (from all staff):");
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        // Initial load
        System.out.println("[ADMIN] Loading initial order history");
        refreshOrderTable(orderTable);

        content.getChildren().addAll(titleLabel, orderTable, refreshBtn);
        VBox.setVgrow(orderTable, javafx.scene.layout.Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    private void refreshOrderTable(TableView<Comanda> orderTable) {
        LoadingDialog loading = new LoadingDialog(stage, "Loading all orders...");
        loading.show();

        AsyncTaskExecutor.executeAsync(
                () -> {
                    System.out.println("[ADMIN] Fetching all orders from DB...");
                    return orderService.getAllOrdersWithUser();
                },
                orders -> {
                    System.out.println("[ADMIN] Found " + orders.size() + " total orders");
                    for (Comanda c : orders) {
                        System.out.println("[ADMIN] Order: ID=" + c.getId() + ", Total=" + c.getTotal() +
                                ", Date=" + (c.getDataOra() != null ? c.getDataOra().format(dtf) : "null") +
                                ", Table=" + (c.getMasa() != null ? c.getMasa().getNumar() : "null") +
                                ", Staff=" + (c.getUser() != null ? c.getUser().getUsername() : "null"));
                    }
                    orderTable.setItems(javafx.collections.FXCollections.observableArrayList(orders));
                    orderTable.refresh();
                },
                ex -> {
                    System.err.println("[ADMIN] Error loading orders: " + ex.getMessage());
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to load order history");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                },
                loading::close);
    }

    private void deleteStaff(User user, TableView<User> table) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Staff Member");
        confirm.setContentText("Are you sure you want to delete " + user.getUsername()
                + "? This action will also delete their order history.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Alert confirmAgain = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAgain.setTitle("Confirm Again");
            confirmAgain.setContentText("Click OK to permanently delete this staff member and their records.");

            if (confirmAgain.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                adminService.deleteStaff(user.getId());
                refreshStaffTable(table);
            }
        }
    }

    private void refreshStaffTable(TableView<User> table) {
        LoadingDialog loading = new LoadingDialog(stage, "Loading staff...");
        loading.show();

        AsyncTaskExecutor.executeAsync(
                () -> adminService.getAllStaff(),
                staff -> {
                    table.setItems(javafx.collections.FXCollections.observableArrayList(staff));
                    table.refresh();
                },
                ex -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Failed to load staff: " + ex.getMessage());
                    alert.showAndWait();
                },
                loading::close);
    }

    private void refreshMenuTable(TableView<Produs> table) {
        LoadingDialog loading = new LoadingDialog(stage, "Loading menu...");
        loading.show();

        AsyncTaskExecutor.executeAsync(
                () -> menuService.getAllProducts(),
                products -> {
                    table.setItems(javafx.collections.FXCollections.observableArrayList(products));
                    table.refresh();
                },
                ex -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Failed to load menu: " + ex.getMessage());
                    alert.showAndWait();
                },
                loading::close);
    }

    private Tab createPromotionsTab() {
        Tab tab = new Tab("Promotions", new VBox());
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(10));

        Label title = new Label("Active discounts (toggle on/off):");

        CheckBox happyHour = new CheckBox("Happy Hour Drinks (every 2nd drink is 50% off)");
        happyHour.setSelected(AppContext.isHappyHourEnabled());
        happyHour.selectedProperty().addListener((obs, oldV, newV) -> AppContext.setHappyHourEnabled(newV));

        CheckBox mealDeal = new CheckBox("Meal Deal (Pizza -> cheapest dessert 25% off)");
        mealDeal.setSelected(AppContext.isMealDealEnabled());
        mealDeal.selectedProperty().addListener((obs, oldV, newV) -> AppContext.setMealDealEnabled(newV));

        CheckBox partyPack = new CheckBox("Party Pack (4 Pizzas -> cheapest pizza free)");
        partyPack.setSelected(AppContext.isPartyPackEnabled());
        partyPack.selectedProperty().addListener((obs, oldV, newV) -> AppContext.setPartyPackEnabled(newV));

        Button preview = new Button("Preview Engine State");
        preview.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Promotions");
            a.setHeaderText(null);
            a.setContentText(
                    "HappyHour=" + AppContext.isHappyHourEnabled() + "\n" +
                            "MealDeal=" + AppContext.isMealDealEnabled() + "\n" +
                            "PartyPack=" + AppContext.isPartyPackEnabled());
            a.showAndWait();
        });

        content.getChildren().addAll(title, happyHour, mealDeal, partyPack, new Separator(), preview);
        tab.setContent(content);
        return tab;
    }

    private void backToLogin() {
        LoginUI loginUI = new LoginUI(sessionFactory);
        loginUI.show(stage);
    }
}
