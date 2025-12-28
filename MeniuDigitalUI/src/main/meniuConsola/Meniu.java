import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.OptionalDouble;
import java.util.Optional;
import java.io.Writer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Meniu {
    private Map<Categorie, List<Produs>> produsePerCategorie;

    public Meniu() {
        this.produsePerCategorie = new HashMap<>();
        // Initialize all categories with empty lists
        for (Categorie cat : Categorie.values()) {
            produsePerCategorie.put(cat, new ArrayList<>());
        }
    }

    public void setProduse(List<Produs> produse) {
        if (produse == null) {
            // Clear all categories
            for (Categorie cat : Categorie.values()) {
                produsePerCategorie.put(cat, new ArrayList<>());
            }
            return;
        }
        // Validate only Mancare or Bauturi
        for (Produs p : produse) {
            if (!(p instanceof Mancare) && !(p instanceof Bauturi)) {
                throw new IllegalArgumentException("Meniu poate conține doar Mancare sau Bauturi. Produs invalid: " + p);
            }
        }

        // Clear existing categories
        for (Categorie cat : Categorie.values()) {
            produsePerCategorie.put(cat, new ArrayList<>());
        }

        // Organize products by category
        for (Produs p : produse) {
            Categorie categorie = p.getCategorie();
            produsePerCategorie.get(categorie).add(p);
        }
    }

    public List<Produs> getProduse() {
        List<Produs> toateProdusele = new ArrayList<>();
        for (List<Produs> listaProduse : produsePerCategorie.values()) {
            toateProdusele.addAll(listaProduse);
        }
        return toateProdusele;
    }

    /**
     * Returnează toate produsele dintr-o anumită categorie
     * @param categorie categoria dorită
     * @return lista de produse din categoria specificată (copie defensivă)
     */
    public List<Produs> getProdusePerCategorie(Categorie categorie) {
        if (categorie == null) {
            return new ArrayList<>();
        }
        List<Produs> produse = produsePerCategorie.get(categorie);
        return produse != null ? new ArrayList<>(produse) : new ArrayList<>();
    }

    /**
     * Returnează toate preparatele vegetariene, sortate în ordine alfabetică
     * @return lista de preparate vegetariene sortate alfabetic după nume
     */
    public List<Mancare> getPreparateVegetarianeSortate() {
        return getProduse().stream()
                .filter(p -> p instanceof Mancare)
                .map(p -> (Mancare) p)
                .filter(Mancare::isVegetarian)
                .sorted(Comparator.comparing(Produs::getNume))
                .collect(Collectors.toList());
    }

    /**
     * Calculează prețul mediu al produselor dintr-o categorie specifică
     * @param categorie categoria pentru care se calculează prețul mediu
     * @return prețul mediu sau 0.0 dacă nu există produse în categoria respectivă
     */
    public double getPretMediuPerCategorie(Categorie categorie) {
        if (categorie == null) {
            return 0.0;
        }

        OptionalDouble average = getProdusePerCategorie(categorie).stream()
                .mapToDouble(Produs::getPret)
                .average();

        return average.orElse(0.0);
    }

    /**
     * Verifică dacă există vreun preparat cu prețul mai mare decât suma specificată
     * @param pret prețul de referință
     * @return true dacă există cel puțin un preparat cu prețul mai mare, false altfel
     */
    public boolean existaPreparatCuPretMaiMareDe(double pret) {
        return getProduse().stream()
                .anyMatch(p -> p.getPret() > pret);
    }

    /**
     * Caută un produs după nume (căutare exactă, case-sensitive)
     * @param nume numele produsului căutat
     * @return Optional conținând produsul găsit, sau Optional.empty() dacă nu există
     */
    public Optional<Produs> cautaProduseDupaNume(String nume) {
        if (nume == null || nume.trim().isEmpty()) {
            return Optional.empty();
        }

        return getProduse().stream()
                .filter(p -> p.getNume().equals(nume))
                .findFirst();
    }

    /**
     * Caută un produs după nume (căutare case-insensitive)
     * @param nume numele produsului căutat
     * @return Optional conținând produsul găsit, sau Optional.empty() dacă nu există
     */
    public Optional<Produs> cautaProduseDupaNumeIgnoreCase(String nume) {
        if (nume == null || nume.trim().isEmpty()) {
            return Optional.empty();
        }

        return getProduse().stream()
                .filter(p -> p.getNume().equalsIgnoreCase(nume))
                .findFirst();
    }

    /**
     * Caută produse după nume parțial (conține substring, case-insensitive)
     * @param numePartial substring-ul căutat în numele produselor
     * @return lista de produse care conțin substring-ul în nume
     */
    public List<Produs> cautaProdusePartial(String numePartial) {
        if (numePartial == null || numePartial.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchTerm = numePartial.toLowerCase();
        return getProduse().stream()
                .filter(p -> p.getNume().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Export the menu (organized by categories) to a JSON file using Gson.
     * Returns true on success, false on failure (and prints a user-friendly message).
     */
    public boolean exportToJson(String caleFisier) {
        if (caleFisier == null || caleFisier.trim().isEmpty()) {
            System.err.println("[ERROR] Cale fișier invalidă pentru export.");
            return false;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = Files.newBufferedWriter(Paths.get(caleFisier))) {
            // Serialize the internal map (produse per categorie) so the exported file
            // reflects categories and product details.
            gson.toJson(this.produsePerCategorie, writer);
            System.out.println("✓ Meniul a fost exportat cu succes în: " + Paths.get(caleFisier).toAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("╔════════════════════════════════════════════════════════════╗");
            System.err.println("║  ⚠️  EROARE: Nu am putut scrie fișierul de export          ║");
            System.err.println("╚════════════════════════════════════════════════════════════╝");
            System.err.println();
            System.err.println("Nu s-a putut scrie fișierul de export: " + caleFisier);
            System.err.println("Motiv: " + e.getMessage());
            System.err.println();
            return false;
        }
    }

    @Override
    public String toString() {
        if (produsePerCategorie == null || getProduse().isEmpty()) {
            return "Menu: (no products)";
        }
        StringBuilder sb = new StringBuilder("--- Meniul Restaurantului '" + Config.getNumeRestaurant() + "' ---\n");

        // Display products organized by category
        for (Categorie categorie : Categorie.values()) {
            List<Produs> produse = produsePerCategorie.get(categorie);
            if (produse != null && !produse.isEmpty()) {
                sb.append("\n").append(categorie.getDenumire()).append(":\n");
                for (Produs p : produse) {
                    sb.append("  > ").append(p).append("\n");
                }
            }
        }
        sb.append("−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−−");
        return sb.toString();
    }
}