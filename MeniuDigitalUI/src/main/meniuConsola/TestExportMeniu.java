import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestExportMeniu {
    public static void main(String[] args) throws Exception {
        Meniu meniu = new Meniu();
        Mancare p1 = new Mancare("Salata Caesar", 35.0, 250, Categorie.APERITIVE, false);
        Mancare p2 = new Mancare("Tarta de legume", 28.0, 200, Categorie.DESERT, true);
        Bauturi b1 = new Bauturi("Cola", 8.0, 330, false);
        Bauturi b2 = new Bauturi("Vin rosu", 45.0, 750, true);

        meniu.setProduse(Arrays.asList(p1, p2, b1, b2));

        String cale = "meniu_export_test.json";
        boolean ok = meniu.exportToJson(cale);
        System.out.println("Export ok? " + ok);

        if (ok) {
            String content = Files.readString(Paths.get(cale));
            System.out.println("-- Preview exported JSON --");
            System.out.println(content);
        }
    }
}

