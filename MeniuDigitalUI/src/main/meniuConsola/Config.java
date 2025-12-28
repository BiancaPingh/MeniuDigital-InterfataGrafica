import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * Clasa de configurație care citește setările dintr-un fișier JSON extern.
 * Permite modificarea TVA-ului, numelui restaurantului și altor parametri fără recompilare.
 */
public final class Config {
    // Valori implicite (fallback dacă fișierul nu există)
    private static final double TVA_DEFAULT = 0.09; // 9%
    private static final String NUME_RESTAURANT_DEFAULT = "La Andrei";
    private static final String MONEDA_DEFAULT = "RON";

    // Valori citite din fișier
    private static double tva;
    private static String numeRestaurant;
    private static String moneda;
    private static String versiune;

    private static boolean configIncarcata = false;

    static {
        // Încarcă configurația la inițializarea clasei
        incarcaConfiguratie();
    }

    private Config() {
        // Constructor privat - clasa nu poate fi instanțiată
    }

    /**
     * Încarcă configurația din fișierul config.json cu gestiune robustă a erorilor
     */
    private static void incarcaConfiguratie() {
        String caleConfig = "config.json";

        try {
            // Verifică dacă fișierul există
            if (!Files.exists(Paths.get(caleConfig))) {
                afiseazaMesajEroareFisierLipsa(caleConfig);
                folosesteValoriImplicite();
                return;
            }

            // Folosim Gson pentru parsing robust
            try (FileReader reader = new FileReader(caleConfig)) {
                Gson gson = new Gson();
                JsonObject jsonConfig = gson.fromJson(reader, JsonObject.class);

                if (jsonConfig == null) {
                    afiseazaMesajEroareFisierCorupt(caleConfig, "Conținut JSON invalid sau gol");
                    folosesteValoriImplicite();
                    return;
                }

                tva = jsonConfig.has("cotaTVA") ? jsonConfig.get("cotaTVA").getAsDouble() : TVA_DEFAULT;
                numeRestaurant = jsonConfig.has("numeRestaurant") ? jsonConfig.get("numeRestaurant").getAsString() : NUME_RESTAURANT_DEFAULT;
                versiune = jsonConfig.has("versiune") ? jsonConfig.get("versiune").getAsString() : "1.0";

                if (jsonConfig.has("configuratii") && jsonConfig.get("configuratii").isJsonObject()) {
                    JsonObject configuratii = jsonConfig.getAsJsonObject("configuratii");
                    moneda = configuratii.has("moneda") ? configuratii.get("moneda").getAsString() : MONEDA_DEFAULT;
                } else {
                    moneda = MONEDA_DEFAULT;
                }

                configIncarcata = true;

                System.out.println("✓ Configurație încărcată cu succes din config.json:");
                System.out.println("  - Restaurant: " + numeRestaurant);
                System.out.println("  - TVA: " + (tva * 100) + "%");
                System.out.println("  - Moneda: " + moneda);
                System.out.println("  - Versiune: " + versiune);
                System.out.println();

            } catch (JsonSyntaxException e) {
                // JSON invalid
                afiseazaMesajEroareFisierCorupt(caleConfig, e.getMessage());
                folosesteValoriImplicite();
            }

        } catch (FileNotFoundException e) {
            afiseazaMesajEroareFisierLipsa(caleConfig);
            folosesteValoriImplicite();

        } catch (IOException e) {
            System.err.println("╔════════════════════════════════════════════════════════════╗");
            System.err.println("║  ⚠️  EROARE: Problemă la citirea fișierului               ║");
            System.err.println("╚════════════════════════════════════════════════════════════╝");
            System.err.println();
            System.err.println("Nu s-a putut citi fișierul de configurare '" + caleConfig + "'.");
            System.err.println("Motiv: " + e.getMessage());
            System.err.println();
            System.err.println("Aplicația va continua cu valorile implicite:");
            System.err.println("  - Restaurant: " + NUME_RESTAURANT_DEFAULT);
            System.err.println("  - TVA: " + (TVA_DEFAULT * 100) + "%");
            System.err.println();
            folosesteValoriImplicite();

        } catch (Exception e) {
            System.err.println("╔════════════════════════════════════════════════════════════╗");
            System.err.println("║  ⚠️  EROARE NEPREVĂZUTĂ                                   ║");
            System.err.println("╚════════════════════════════════════════════════════════════╝");
            System.err.println();
            System.err.println("A apărut o eroare neprevăzută la încărcarea configurației.");
            System.err.println("Tip eroare: " + e.getClass().getSimpleName());
            System.err.println("Mesaj: " + e.getMessage());
            System.err.println();
            System.err.println("Aplicația va continua cu valorile implicite.");
            System.err.println();
            folosesteValoriImplicite();
        }
    }

    /**
     * Afișează un mesaj user-friendly când fișierul de configurare lipsește
     */
    private static void afiseazaMesajEroareFisierLipsa(String numeFisier) {
        System.err.println("╔════════════════════════════════════════════════════════════╗");
        System.err.println("║  ⚠️  EROARE: Fișierul de configurare lipsește             ║");
        System.err.println("╚════════════════════════════════════════════════════════════╝");
        System.err.println();
        System.err.println("Fișierul '" + numeFisier + "' nu a fost găsit.");
        System.err.println();
        System.err.println("Acesta ar trebui să fie localizat în:");
        System.err.println("  " + Paths.get(numeFisier).toAbsolutePath());
        System.err.println();
        System.err.println("Soluții:");
        System.err.println("  • Creați fișierul cu următorul conținut minimal:");
        System.err.println("    {");
        System.err.println("      \"numeRestaurant\": \"La Andrei\",");
        System.err.println("      \"cotaTVA\": 0.09");
        System.err.println("    }");
        System.err.println();
        System.err.println("  • SAU contactați suportul tehnic pentru asistență");
        System.err.println();
        System.err.println("Aplicația va continua cu valorile implicite:");
        System.err.println("  - Restaurant: " + NUME_RESTAURANT_DEFAULT);
        System.err.println("  - TVA: " + (TVA_DEFAULT * 100) + "%");
        System.err.println();
    }

    /**
     * Afișează un mesaj user-friendly când fișierul de configurare este corupt
     */
    private static void afiseazaMesajEroareFisierCorupt(String numeFisier, String detaliiTehnice) {
        System.err.println("╔════════════════════════════════════════════════════════════╗");
        System.err.println("║  ⚠️  EROARE: Fișierul de configurare este corupt          ║");
        System.err.println("╚════════════════════════════════════════════════════════════╝");
        System.err.println();
        System.err.println("Fișierul '" + numeFisier + "' conține erori de formatare.");
        System.err.println();
        System.err.println("Probleme posibile:");
        System.err.println("  • Lipsește o virgulă între elemente");
        System.err.println("  • Lipsesc ghilimele la un șir de text");
        System.err.println("  • Paranteze acolade { } neîmperecheate");
        System.err.println("  • Format JSON invalid");
        System.err.println();

        // Afișează detalii tehnice doar dacă sunt relevante
        if (detaliiTehnice != null && !detaliiTehnice.isEmpty() && detaliiTehnice.length() < 200) {
            System.err.println("Detalii tehnice: " + detaliiTehnice);
            System.err.println();
        }

        System.err.println("Soluții:");
        System.err.println("  • Verificați sintaxa JSON cu un editor de text");
        System.err.println("  • Restaurați fișierul din backup");
        System.err.println("  • Contactați suportul tehnic pentru asistență");
        System.err.println();
        System.err.println("Exemplu de format corect:");
        System.err.println("  {");
        System.err.println("    \"numeRestaurant\": \"La Andrei\",");
        System.err.println("    \"cotaTVA\": 0.09");
        System.err.println("  }");
        System.err.println();
        System.err.println("Aplicația va continua cu valorile implicite:");
        System.err.println("  - Restaurant: " + NUME_RESTAURANT_DEFAULT);
        System.err.println("  - TVA: " + (TVA_DEFAULT * 100) + "%");
        System.err.println();
    }

    /**
     * Folosește valorile implicite (hardcoded) dacă fișierul JSON nu poate fi citit
     */
    private static void folosesteValoriImplicite() {
        tva = TVA_DEFAULT;
        numeRestaurant = NUME_RESTAURANT_DEFAULT;
        moneda = MONEDA_DEFAULT;
        versiune = "1.0";
        configIncarcata = false;
    }

    /**
     * Reîncarcă configurația din fișier (util pentru refresh fără restart)
     */
    public static void reincarcaConfiguratie() {
        incarcaConfiguratie();
    }

    // Getters pentru accesarea valorilor de configurare

    public static double getTVA() {
        return tva;
    }

    public static String getNumeRestaurant() {
        return numeRestaurant;
    }

    public static String getMoneda() {
        return moneda;
    }

    public static String getVersiune() {
        return versiune;
    }

    public static boolean isConfigIncarcata() {
        return configIncarcata;
    }
}
