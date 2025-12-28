import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Test automat pentru gestiunea robustă a erorilor din Config.java
 * Acest test verifică trei situații:
 * 1) Config valid (Config.isConfigIncarcata() == true)
 * 2) Fișier lipsă (Config.isConfigIncarcata() == false)
 * 3) JSON corupt (Config.isConfigIncarcata() == false)
 *
 * Observație: folosim assert-uri simple (aruncă AssertionError) pentru a nu necesita
 * un framework extern (JUnit). Rularea: java -cp "out;lib\\gson-2.10.1.jar" TestGestiuneErori
 */
public class TestGestiuneErori {
    private static final Path CONFIG = Paths.get("config.json");
    private static final Path ORIGINAL_BACKUP = Paths.get("config_original_backup_for_test.json");
    private static final Path MISSING_BACKUP = Paths.get("config_missing_backup_for_test.json");

    public static void main(String[] args) {
        System.out.println("=== Încep teste TestGestiuneErori ===");
        boolean overallSuccess = true;

        try {
            // Backup original config if exists
            boolean hadOriginal = Files.exists(CONFIG);
            if (hadOriginal) {
                // If a previous backup file exists, remove it first to avoid FileAlreadyExistsException
                try {
                    if (Files.exists(ORIGINAL_BACKUP)) {
                        Files.delete(ORIGINAL_BACKUP);
                    }
                } catch (IOException ignore) {
                    // best-effort cleanup; continue
                }
                // Copy with REPLACE_EXISTING to be robust if file remains
                Files.copy(CONFIG, ORIGINAL_BACKUP, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[INFO] Backup creat: " + ORIGINAL_BACKUP.toAbsolutePath());
            } else {
                System.out.println("[INFO] Nu exista config.json original; vom crea unul temporar pentru test.");
            }

            // Ensure a valid config exists for Test 1
            String validJson = "{\n" +
                    "  \"numeRestaurant\": \"La Andrei\",\n" +
                    "  \"cotaTVA\": 0.09,\n" +
                    "  \"versiune\": \"1.0\",\n" +
                    "  \"configuratii\": { \"moneda\": \"RON\" }\n" +
                    "}";

            Files.write(CONFIG, validJson.getBytes(StandardCharsets.UTF_8));

            // Test 1: Config valid
            System.out.println("--- Test 1: Config VALID ---");
            Config.reincarcaConfiguratie();
            assertTrue(Config.isConfigIncarcata(), "Config trebuia să fie încărcată pentru un JSON valid");
            System.out.println("[PASS] Test 1: Config valid încărcat cu succes.");

            // Test 2: Fișier lipsă
            System.out.println("--- Test 2: Fișier LIPSEște ---");
            if (Files.exists(CONFIG)) {
                // ensure previous missing backup is removed before move
                try {
                    if (Files.exists(MISSING_BACKUP)) Files.delete(MISSING_BACKUP);
                } catch (IOException ignore) {}
                Files.move(CONFIG, MISSING_BACKUP, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[INFO] config.json mutat temporar: " + MISSING_BACKUP.toAbsolutePath());
            }

            Config.reincarcaConfiguratie();
            assertFalse(Config.isConfigIncarcata(), "ConfigIncarcata trebuie false când fisierul lipsește");
            System.out.println("[PASS] Test 2: Lipsă fișier - aplicația folosește valorile implicite fără a crăpa.");

            // Restore a valid config for Test 3
            if (Files.exists(MISSING_BACKUP)) {
                Files.move(MISSING_BACKUP, CONFIG, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[INFO] Restaurat config.json pentru Test 3.");
            } else {
                Files.write(CONFIG, validJson.getBytes(StandardCharsets.UTF_8));
            }

            // Test 3: JSON corupt
            System.out.println("--- Test 3: JSON CORRUPT ---");
            String corrupt = "{ \"numeRestaurant\": \"La Andrei\", \"cotaTVA\": 0.09,  "; // truncated
            Files.write(CONFIG, corrupt.getBytes(StandardCharsets.UTF_8));

            Config.reincarcaConfiguratie();
            assertFalse(Config.isConfigIncarcata(), "ConfigIncarcata trebuie false pentru JSON corupt");
            System.out.println("[PASS] Test 3: JSON corupt - aplicația gestionează eroarea și continuă.");

        } catch (AssertionError ae) {
            overallSuccess = false;
            System.err.println("[FAIL] Asserție eșuată: " + ae.getMessage());
            ae.printStackTrace();
        } catch (Exception e) {
            overallSuccess = false;
            System.err.println("[ERROR] Excepție neașteptată în timpul testului: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Restaurează backup-ul original dacă exista
            try {
                if (Files.exists(ORIGINAL_BACKUP)) {
                    // if CONFIG exists, remove it before restore to avoid conflicts
                    try { if (Files.exists(CONFIG)) Files.delete(CONFIG); } catch (IOException ignore) {}
                    Files.move(ORIGINAL_BACKUP, CONFIG, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("[INFO] Restaurat fișierul original config.json din backup.");
                    Files.deleteIfExists(ORIGINAL_BACKUP);
                } else {
                    // Dacă nu existase original, asigură-te că ștergem fișierele temporare
                    Files.deleteIfExists(CONFIG);
                }
                // Curățenie
                Files.deleteIfExists(MISSING_BACKUP);
            } catch (IOException e) {
                System.err.println("[WARN] Nu s-a putut restaura/curăța fișierele temporare: " + e.getMessage());
            }

            System.out.println("=== Teste finalizate. Rezultat general: " + (overallSuccess ? "SUCCES" : "EȘEC") + " ===");
            // Exit code non-zero în caz de eșec
            if (!overallSuccess) {
                System.exit(1);
            }
        }
    }

    // Helper assertion functions
    private static void assertTrue(boolean cond, String message) {
        if (!cond) throw new AssertionError(message);
    }

    private static void assertFalse(boolean cond, String message) {
        if (cond) throw new AssertionError(message);
    }
}
