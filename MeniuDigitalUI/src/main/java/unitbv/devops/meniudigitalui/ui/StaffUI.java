package unitbv.devops.meniudigitalui.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import unitbv.devops.meniudigitalui.entity.*;
import unitbv.devops.meniudigitalui.service.MenuService;
import unitbv.devops.meniudigitalui.service.OrderService;
import unitbv.devops.meniudigitalui.service.DiscountService;
import unitbv.devops.meniudigitalui.service.TableService;
import unitbv.devops.meniudigitalui.service.ReceiptLine;
import unitbv.devops.meniudigitalui.service.AsyncTaskExecutor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StaffUI {
    private final SessionFactory sessionFactory;
    private final User currentUser;
    private final MenuService menuService = new MenuService();
    private final OrderService orderService = new OrderService();
    private final DiscountService discountService = new DiscountService();
    private final TableService tableService = new TableService();

    private Stage stage;
    private Comanda currentComanda;
    private Masa selectedMasa;

    private TableView<ReceiptLine> cartTable;
    private Label cartTotalLabel;

    private TableView<Comanda> historyTable;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TabPane tabPane;
    private Label selectedTableLabel;

    private TableView<Produs> menuTable;
    private Label menuCountLabel;
    private ObservableList<Produs> menuItems;

    public StaffUI(SessionFactory sessionFactory, User currentUser) {
        this.sessionFactory = sessionFactory;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        this.stage = primaryStage;

        // load products once from DB
        menuItems = FXCollections.observableArrayList(menuService.getAllProducts());
        System.out.println("[UI] StaffUI.show loaded products = " + menuItems.size());

        BorderPane root = new BorderPane();
        root.setTop(createTopBar());
        root.setLeft(createLeftPanel());
        this.tabPane = createMainTabs();
        root.setCenter(this.tabPane);

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Restaurant Staff - " + currentUser.getUsername());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TabPane createMainTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab menuTab = createMenuTab();
        Tab cartTab = createCartTab();
        Tab historyTab = createHistoryTab();

        tabPane.getTabs().addAll(menuTab, cartTab, historyTab);
        return tabPane;
    }

    private VBox createTopBar() {
        VBox topBar = new VBox(6);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        Label title = new Label("Order Management - " + currentUser.getUsername());
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        selectedTableLabel = new Label("Selected table: -");
        topBar.getChildren().addAll(title, selectedTableLabel);
        return topBar;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");
        leftPanel.setPrefWidth(200);

        Label infoLabel = new Label("Table & Order");
        infoLabel.setStyle("-fx-font-weight: bold;");

        Button selectTableButton = new Button("Select Table");
        selectTableButton.setPrefWidth(180);
        selectTableButton.setOnAction(e -> showTableSelection());

        Button startOrderButton = new Button("New Order");
        startOrderButton.setPrefWidth(180);
        startOrderButton.setOnAction(e -> startNewOrder());

        Button historyButton = new Button("My Orders");
        historyButton.setPrefWidth(180);
        historyButton.setOnAction(e -> {
            System.out.println("[UI] My Orders button clicked");
            refreshHistory();
            if (tabPane != null) {
                // Switch to history tab (index 2: Menu=0, Order=1, My History=2)
                tabPane.getSelectionModel().select(2);
            }
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(180);
        logoutButton.setOnAction(e -> backToLogin());

        leftPanel.getChildren().addAll(
                infoLabel,
                selectTableButton,
                startOrderButton,
                new Separator(),
                historyButton,
                new Separator(),
                logoutButton
        );

        VBox.setVgrow(logoutButton, javafx.scene.layout.Priority.ALWAYS);
        return leftPanel;
    }

    private Tab createMenuTab() {
        Tab tab = new Tab("Menu", new VBox());
        tab.setClosable(false);
        tab.setContent(createMenuContent());
        return tab;
    }

    private Tab createCartTab() {
        Tab tab = new Tab("Order", new VBox());
        tab.setClosable(false);
        tab.setContent(createCartContent());
        return tab;
    }

    private Tab createHistoryTab() {
        Tab tab = new Tab("My History", new VBox());
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        historyTable = new TableView<>();
        historyTable.setMinHeight(400);
        historyTable.setPrefHeight(600);
        historyTable.setPlaceholder(new Label("No orders yet. Complete an order to see it here."));

        TableColumn<Comanda, Integer> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getId()));
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

        TableColumn<Comanda, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cd -> {
            Double total = cd.getValue().getTotal();
            String formatted = String.format("%.2f RON", total != null ? total : 0.0);
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        totalCol.setPrefWidth(120);

        historyTable.getColumns().addAll(idCol, dateCol, tableCol, totalCol);

        Button refresh = new Button("Refresh History");
        refresh.setOnAction(e -> {
            System.out.println("[UI] Manual refresh history button clicked");
            refreshHistory();
        });

        Label titleLabel = new Label("Your Completed Orders:");
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        content.getChildren().addAll(titleLabel, historyTable, refresh);
        VBox.setVgrow(historyTable, javafx.scene.layout.Priority.ALWAYS);

        tab.setContent(content);

        // Load history when tab is first created
        System.out.println("[UI] Creating history tab, loading initial data");
        refreshHistory();

        return tab;
    }

    private VBox createCartContent() {
        VBox cartPanel = new VBox(10);
        cartPanel.setPadding(new Insets(10));

        cartTable = new TableView<>();

        TableColumn<ReceiptLine, String> descCol = new TableColumn<>("Item");
        descCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getDescriere()));
        descCol.setPrefWidth(260);

        TableColumn<ReceiptLine, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getCantitate()));
        qtyCol.setPrefWidth(60);

        TableColumn<ReceiptLine, Double> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getPretUnitar()));
        unitCol.setPrefWidth(90);

        TableColumn<ReceiptLine, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getTotal()));
        totalCol.setPrefWidth(90);

        TableColumn<ReceiptLine, Void> editCol = new TableColumn<>("Edit");
        editCol.setPrefWidth(220);
        editCol.setCellFactory(col -> new TableCell<>() {
            private final Spinner<Integer> qtySpinner = new Spinner<>(1, 100, 1);
            private final Button updateBtn = new Button("Update");
            private final Button removeBtn = new Button("Remove");
            private final HBox box = new HBox(5, qtySpinner, updateBtn, removeBtn);

            {
                qtySpinner.setPrefWidth(65);
                updateBtn.setOnAction(e -> {
                    ReceiptLine line = getTableView().getItems().get(getIndex());
                    if (line != null && !line.isDiscount()) {
                        updateProductQuantityById(line.getProdusId(), qtySpinner.getValue());
                    }
                });
                removeBtn.setOnAction(e -> {
                    ReceiptLine line = getTableView().getItems().get(getIndex());
                    if (line != null && !line.isDiscount()) {
                        removeProductLineById(line.getProdusId());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                ReceiptLine line = getTableView().getItems().get(getIndex());
                if (line == null || line.isDiscount()) {
                    setGraphic(null);
                } else {
                    qtySpinner.getValueFactory().setValue(Math.max(1, line.getCantitate()));
                    setGraphic(box);
                }
            }
        });

        cartTable.getColumns().addAll(descCol, qtyCol, unitCol, totalCol, editCol);

        cartTotalLabel = new Label("Total: 0.00 RON");
        cartTotalLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button finalizeButton = new Button("Finalize Order");
        finalizeButton.setPrefWidth(150);
        finalizeButton.setOnAction(e -> finalizeOrder(cartTotalLabel));

        Button clearButton = new Button("Clear Cart");
        clearButton.setPrefWidth(150);
        clearButton.setOnAction(e -> clearCart(cartTotalLabel));

        actionBox.getChildren().addAll(clearButton, finalizeButton);

        cartPanel.getChildren().addAll(
                new Label("Current Order:"),
                cartTable,
                cartTotalLabel,
                actionBox
        );

        VBox.setVgrow(cartTable, javafx.scene.layout.Priority.ALWAYS);
        return cartPanel;
    }

    private void showTableSelection() {
        List<Masa> tables = tableService.getAllTables();

        Stage tableStage = new Stage();
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TableView<Masa> tableView = new TableView<>();

        TableColumn<Masa, Integer> nrCol = new TableColumn<>("Table");
        nrCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getNumar()));
        nrCol.setPrefWidth(120);

        TableColumn<Masa, Boolean> occCol = new TableColumn<>("Occupied");
        occCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getOcupata()));
        occCol.setPrefWidth(120);

        tableView.getColumns().addAll(nrCol, occCol);
        tableView.setItems(javafx.collections.FXCollections.observableArrayList(tables));

        Button choose = new Button("Choose");
        choose.setOnAction(e -> {
            Masa m = tableView.getSelectionModel().getSelectedItem();
            if (m != null) {
                selectedMasa = m;
                if (selectedTableLabel != null) {
                    selectedTableLabel.setText("Selected table: " + m.getNumar() + (Boolean.TRUE.equals(m.getOcupata()) ? " (occupied)" : ""));
                }
                tableStage.close();
            }
        });

        root.setCenter(tableView);
        root.setBottom(new HBox(10, choose));

        Scene scene = new Scene(root, 300, 400);
        tableStage.setTitle("Select Table");
        tableStage.setScene(scene);
        tableStage.show();
    }

    private void startNewOrder() {
        if (selectedMasa == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Please select a table first");
            alert.showAndWait();
            return;
        }

        selectedMasa = tableService.ensureTableExists(selectedMasa.getNumar());
        tableService.setOccupied(selectedMasa, true);

        currentComanda = orderService.createNewOrder(currentUser, selectedMasa);
        currentComanda.setItems(new ArrayList<>());

        if (selectedTableLabel != null) {
            selectedTableLabel.setText("Selected table: " + selectedMasa.getNumar() + " (occupied)");
        }

        if (tabPane != null) {
            Tab menuTab = tabPane.getTabs().get(0);
            menuTab.setContent(createMenuContent());
            tabPane.getSelectionModel().select(menuTab);
        }

        updateCartDisplay();
    }

    private VBox createMenuContent() {
        VBox menuPanel = new VBox(10);
        menuPanel.setPadding(new Insets(10));

        // Try to load from DB first
        List<Produs> dbProducts = menuService.getAllProducts();

        if (dbProducts != null && !dbProducts.isEmpty()) {
            // Use DB products (they have IDs and can be persisted)
            menuItems = FXCollections.observableArrayList(dbProducts);
            System.out.println("[UI] Loaded " + menuItems.size() + " products from DB");
        } else {
            // Fallback: use hardcoded products BUT persist them to DB first so they get IDs
            menuItems = FXCollections.observableArrayList();

            Mancare p1 = new Mancare("Pizza Margherita", 45.0, "Tomato, mozzarella, basil", true);
            Mancare p2 = new Mancare("Pizza Quattro Formaggi", 48.0, "Four cheese pizza", true);
            Mancare p3 = new Mancare("Paste Carbonara", 52.5, "Pasta with bacon and cream", false);
            Mancare p4 = new Mancare("Paste Vegetariene", 48.0, "Vegetarian pasta", true);
            Mancare p5 = new Mancare("Salata de vinete", 18.0, "Eggplant salad", true);
            Mancare p6 = new Mancare("Hummus", 22.0, "Chickpea dip", true);
            Mancare p7 = new Mancare("Bruschette", 25.0, "Toasted bread with toppings", true);
            Mancare p8 = new Mancare("Tiramisu", 25.0, "Italian dessert", true);
            Mancare p9 = new Mancare("Cheesecake", 28.0, "Cream cheese cake", true);
            Bauturi p10 = new Bauturi("Limonada", 15.0, "Fresh lemonade", true);

            // Persist each product so it gets an ID
            menuService.saveProduct(p1);
            menuService.saveProduct(p2);
            menuService.saveProduct(p3);
            menuService.saveProduct(p4);
            menuService.saveProduct(p5);
            menuService.saveProduct(p6);
            menuService.saveProduct(p7);
            menuService.saveProduct(p8);
            menuService.saveProduct(p9);
            menuService.saveProduct(p10);

            // Reload from DB to get the persisted versions with IDs
            menuItems = FXCollections.observableArrayList(menuService.getAllProducts());
            System.out.println("[UI] Created and persisted " + menuItems.size() + " fallback products");
        }

        for (Produs p : menuItems) {
            System.out.println("[UI] Product: " + p.getId() + " - " + p.getNume() + " @ " + p.getPret());
        }

        menuCountLabel = new Label("Products available: " + menuItems.size());
        Label hint = new Label("Select products below. Start a New Order to add them to cart.");

        menuTable = new TableView<>();
        menuTable.setMinHeight(400);
        menuTable.setPrefHeight(600);
        menuTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Produs, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(cd -> {
            Produs prod = cd.getValue();
            String name = (prod != null && prod.getNume() != null) ? prod.getNume() : "UNKNOWN";
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        nameCol.setMinWidth(200);

        TableColumn<Produs, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cd -> {
            Produs prod = cd.getValue();
            Double price = (prod != null && prod.getPret() != null) ? prod.getPret() : 0.0;
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f RON", price));
        });
        priceCol.setMinWidth(100);

        TableColumn<Produs, Void> addCol = new TableColumn<>("Action");
        addCol.setMinWidth(150);
        addCol.setCellFactory(col -> new TableCell<>() {
            private final Button addButton = new Button("Add to Cart");

            {
                addButton.setOnAction(e -> {
                    Produs p = getTableView().getItems().get(getIndex());
                    if (currentComanda == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText(null);
                        alert.setContentText("Please select a table and start a New Order first!");
                        alert.showAndWait();
                        return;
                    }
                    addToCart(p, 1);
                    System.out.println("[UI] Added to cart: " + p.getNume() + " (ID: " + p.getId() + ")");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    addButton.setDisable(currentComanda == null);
                    setGraphic(addButton);
                }
            }
        });

        menuTable.getColumns().clear();
        menuTable.getColumns().addAll(nameCol, priceCol, addCol);
        menuTable.setItems(menuItems);
        menuTable.setPlaceholder(new Label("No products available"));

        Button debugButton = new Button("DEBUG: Reload Menu");
        debugButton.setOnAction(e -> {
            System.out.println("[DEBUG] Current menuItems size: " + menuItems.size());
            System.out.println("[DEBUG] TableView items size: " + menuTable.getItems().size());
            menuTable.refresh();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Debug Info");
            alert.setHeaderText("Menu Table Debug");
            alert.setContentText("MenuItems size: " + menuItems.size() + "\nTableView items: " + menuTable.getItems().size());
            alert.showAndWait();
        });

        menuPanel.getChildren().addAll(
                new Label("Available Products:"),
                menuCountLabel,
                hint,
                debugButton,
                menuTable
        );
        VBox.setVgrow(menuTable, javafx.scene.layout.Priority.ALWAYS);
        return menuPanel;
    }

    private void addToCart(Produs produs, Integer quantity) {
        if (currentComanda == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Please start a new order first");
            alert.showAndWait();
            return;
        }

        ComandaItem item = new ComandaItem(currentComanda, produs, quantity, produs.getPret());
        currentComanda.getItems().add(item);

        updateCartDisplay();
    }

    private void refreshHistory() {
        if (historyTable == null) {
            System.out.println("[UI] historyTable is null, cannot refresh");
            return;
        }

        LoadingDialog loading = new LoadingDialog(stage, "Loading order history...");
        loading.show();

        AsyncTaskExecutor.executeAsync(
                () -> {
                    System.out.println("[UI] Refreshing history for user ID: " + currentUser.getId());
                    return orderService.getUserOrdersWithUser(currentUser.getId());
                },
                orders -> {
                    System.out.println("[UI] Found " + orders.size() + " orders");
                    for (Comanda c : orders) {
                        System.out.println("[UI] Order: ID=" + c.getId() + ", Total=" + c.getTotal() +
                                ", Date=" + (c.getDataOra() != null ? c.getDataOra().format(dtf) : "null") +
                                ", Table=" + (c.getMasa() != null ? c.getMasa().getNumar() : "null"));
                    }
                    historyTable.setItems(FXCollections.observableArrayList(orders));
                    historyTable.refresh();
                },
                ex -> {
                    System.err.println("[UI] Error refreshing history: " + ex.getMessage());
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to load order history");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                },
                loading::close
        );
    }

    private void updateCartDisplay() {
        if (currentComanda != null) {
            cartTable.setItems(javafx.collections.FXCollections.observableArrayList(discountService.buildReceiptLines(currentComanda)));

            discountService.applyDiscounts(currentComanda);
            double total = currentComanda.getTotal();

            if (cartTotalLabel != null) {
                cartTotalLabel.setText("Total: " + String.format("%.2f", total) + " RON");
            }
        } else {
            cartTable.setItems(javafx.collections.FXCollections.observableArrayList());
            if (cartTotalLabel != null) {
                cartTotalLabel.setText("Total: 0.00 RON");
            }
        }
    }

    private void clearCart(Label totalLabel) {
        if (currentComanda != null && currentComanda.getItems() != null) {
            currentComanda.getItems().clear();
        }
        currentComanda = null;

        if (cartTable != null) {
            cartTable.setItems(javafx.collections.FXCollections.observableArrayList());
        }
        if (totalLabel != null) {
            totalLabel.setText("Total: 0.00 RON");
        }
        if (cartTotalLabel != null) {
            cartTotalLabel.setText("Total: 0.00 RON");
        }
    }

    private void updateProductQuantity(String productName, int newQty) {
        if (currentComanda == null || currentComanda.getItems() == null) {
            return;
        }
        for (ComandaItem item : currentComanda.getItems()) {
            if (item.getProdus() != null && item.getProdus().getNume() != null && item.getProdus().getNume().equals(productName)) {
                item.setCantitate(newQty);
                break;
            }
        }
        updateCartDisplay();
    }

    private void removeProductLine(String productName) {
        if (currentComanda == null || currentComanda.getItems() == null) {
            return;
        }
        currentComanda.getItems().removeIf(i -> i.getProdus() != null && i.getProdus().getNume() != null && i.getProdus().getNume().equals(productName));
        updateCartDisplay();
    }

    private void updateProductQuantityById(Integer produsId, int newQty) {
        if (produsId == null || currentComanda == null || currentComanda.getItems() == null) {
            return;
        }
        for (ComandaItem item : currentComanda.getItems()) {
            if (item.getProdus() != null && produsId.equals(item.getProdus().getId())) {
                item.setCantitate(newQty);
                break;
            }
        }
        updateCartDisplay();
    }

    private void removeProductLineById(Integer produsId) {
        if (produsId == null || currentComanda == null || currentComanda.getItems() == null) {
            return;
        }
        currentComanda.getItems().removeIf(i -> i.getProdus() != null && produsId.equals(i.getProdus().getId()));
        updateCartDisplay();
    }

    private void finalizeOrder(Label totalLabel) {
        if (currentComanda == null || currentComanda.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Order is empty");
            alert.showAndWait();
            return;
        }

        try {
            orderService.saveOrder(currentComanda);
            tableService.setOccupied(selectedMasa, false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Order finalized and saved");
            alert.showAndWait();

            currentComanda = null;
            selectedMasa = null;
            clearCart(totalLabel);
            refreshHistory();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to save order: " + e.getMessage());
            alert.showAndWait();
        }
    }


    private void backToLogin() {
        LoginUI loginUI = new LoginUI(sessionFactory);
        loginUI.show(stage);
    }
}
