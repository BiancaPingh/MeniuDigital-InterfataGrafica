package unitbv.devops.meniudigitalui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Sample data (in-memory) to demonstrate the UI
        ObservableList<ProductItem> products = FXCollections.observableArrayList(
                new ProductItem("Pizza Margherita", 45.0, "Mancare", "450 g"),
                new ProductItem("Paste Carbonara", 52.5, "Mancare", "400 g"),
                new ProductItem("Tiramisu", 25.0, "Desert", "200 g"),
                new ProductItem("Limonada", 15.0, "Bauturi", "400 ml"),
                new ProductItem("Vin Rosu", 25.0, "Bauturi", "150 ml")
        );

        // Left: ListView with product names
        ListView<ProductItem> listView = new ListView<>(products);
        listView.setPrefWidth(240);
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ProductItem item, boolean empty) {
                super.updateItem(item, empty);
                textProperty().unbind();
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Bind the text to the product's name and formatted price so it updates reactively
                    textProperty().bind(item.nameProperty().concat(" - ").concat(item.priceStringProperty()).concat(" RON"));
                }
            }
        });

        root.setLeft(listView);

        // Center: Detail form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        Label nameLabel = new Label("Nume:");
        TextField nameField = new TextField();
        nameField.setEditable(false);

        Label priceLabel = new Label("Preț (RON):");
        TextField priceField = new TextField();

        Label catLabel = new Label("Categorie:");
        TextField catField = new TextField();
        catField.setEditable(false);

        Label sizeLabel = new Label("Gramaj/Volum:");
        TextField sizeField = new TextField();
        sizeField.setEditable(false);

        form.addRow(0, nameLabel, nameField);
        form.addRow(1, priceLabel, priceField);
        form.addRow(2, catLabel, catField);
        form.addRow(3, sizeLabel, sizeField);

        // Numeric filter for price: allow digits and decimal separator
        Pattern validNumeric = Pattern.compile("-?\\d*(\\.\\d*)?");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (validNumeric.matcher(newText).matches()) {
                return change;
            }
            return null;
        };
        DoubleStringConverter doubleConverter = new DoubleStringConverter();
        TextFormatter<Double> priceFormatter = new TextFormatter<>(doubleConverter, 0.0, filter);
        priceField.setTextFormatter(priceFormatter);

        // Holder for the current listener so we can remove it when selection changes
        final AtomicReference<ChangeListener<Double>> currentFormatterListener = new AtomicReference<>();

        // Selection listener: when a product is selected, update form fields and wire price edits
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            // remove previous listener if present
            if (currentFormatterListener.get() != null) {
                priceFormatter.valueProperty().removeListener(currentFormatterListener.get());
                currentFormatterListener.set(null);
            }

            if (newItem == null) {
                nameField.setText("");
                priceFormatter.setValue(0.0);
                catField.setText("");
                sizeField.setText("");
                priceField.setDisable(true);
            } else {
                nameField.setText(newItem.getName());
                priceFormatter.setValue(newItem.getPret());
                catField.setText(newItem.getCategorie());
                sizeField.setText(newItem.getGramaj());
                priceField.setDisable(false);

                // When price value changes (committed), update the model's price
                ChangeListener<Double> listener = (o, oldVal, newVal) -> {
                    if (newVal == null) return;
                    newItem.setPret(newVal);
                    listView.refresh();
                };
                priceFormatter.valueProperty().addListener(listener);
                currentFormatterListener.set(listener);
            }
        });

        VBox centerBox = new VBox(form);
        centerBox.setPadding(new Insets(10));
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 800, 400);
        stage.setTitle("Meniu - Editor Simplu");
        stage.setScene(scene);
        stage.show();
    }

    // Simple in-memory product item using JavaFX properties
    public static class ProductItem {
        private final StringProperty name = new SimpleStringProperty();
        private final DoubleProperty pret = new SimpleDoubleProperty();
        private final StringProperty categorie = new SimpleStringProperty();
        private final StringProperty gramaj = new SimpleStringProperty();
        private final StringProperty priceString = new SimpleStringProperty();

        public ProductItem(String name, double pret, String categorie, String gramaj) {
            this.name.set(name);
            this.pret.set(pret);
            this.categorie.set(categorie);
            this.gramaj.set(gramaj);
            this.priceString.set(String.format("%.2f", pret));
            // keep the formatted price string in sync with pret
            this.pret.addListener((o, oldV, newV) -> this.priceString.set(String.format("%.2f", newV.doubleValue())));
        }

        public String getName() { return name.get(); }
        public void setName(String v) { name.set(v); }
        public StringProperty nameProperty() { return name; }

        public double getPret() { return pret.get(); }
        public void setPret(double v) { pret.set(v); }
        public DoubleProperty pretProperty() { return pret; }

        public String getCategorie() { return categorie.get(); }
        public StringProperty categorieProperty() { return categorie; }

        public String getGramaj() { return gramaj.get(); }
        public StringProperty gramajProperty() { return gramaj; }

        // Stable property for formatted price string
        public StringProperty priceStringProperty() { return priceString; }
    }
}
