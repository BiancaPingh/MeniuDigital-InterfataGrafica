import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       PIZZA CUSTOMIZABILĂ - BUILDER PATTERN DEMO          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Demonstrație 1: Pizza simplă (doar elemente obligatorii)
        System.out.println("=== 1. Pizza Simplă (doar blat și sos - elemente obligatorii) ===");
        Pizza pizzaSimple = Pizza.builder()
                .cuBlat(TipBlat.TRADITIONAL)
                .cuSos(TipSos.ROSII)
                .build();
        System.out.println(pizzaSimple);
        System.out.println();

        // Demonstrație 2: Pizza Margherita (cu topping-uri)
        System.out.println("=== 2. Pizza Margherita ===");
        Pizza pizzaMargherita = Pizza.builder()
                .cuNume("Pizza Margherita")
                .cuBlat(TipBlat.SUBTIRE)
                .cuSos(TipSos.ROSII)
                .adaugaTopping(Topping.MOZZARELLA_EXTRA)
                .adaugaTopping(Topping.ROSII_CHERRY)
                .adaugaTopping(Topping.RUCOLA)
                .vegetariana()
                .build();
        System.out.println(pizzaMargherita);
        System.out.println();

        // Demonstrație 3: Pizza cu multe topping-uri (super-customizată)
        System.out.println("=== 3. Pizza Super-Customizată (multe topping-uri) ===");
        Pizza pizzaSupreme = Pizza.builder()
                .cuNume("Pizza Supreme Deluxe")
                .cuBlat(TipBlat.PUFOS)
                .cuSos(TipSos.BBQ)
                .cuPretBaza(30.0)
                .cuGramaj(500)
                .adaugaToppings(
                    Topping.MOZZARELLA_EXTRA,
                    Topping.PEPPERONI,
                    Topping.SALAM,
                    Topping.BACON,
                    Topping.CIUPERCI,
                    Topping.ARDEI,
                    Topping.CEAPA,
                    Topping.MASLINE
                )
                .build();
        System.out.println(pizzaSupreme);
        System.out.println();

        // Demonstrație 4: Pizza Vegetariană
        System.out.println("=== 4. Pizza Vegetariană Gourmet ===");
        Pizza pizzaVegetarian = Pizza.builder()
                .cuNume("Pizza Vegetarian Gourmet")
                .cuBlat(TipBlat.INTEGRAL)
                .cuSos(TipSos.PESTO)
                .adaugaToppings(
                    Topping.MOZZARELLA_EXTRA,
                    Topping.CIUPERCI,
                    Topping.ARDEI,
                    Topping.ROSII_CHERRY,
                    Topping.RUCOLA,
                    Topping.PARMEZAN,
                    Topping.USTUROI
                )
                .vegetariana()
                .build();
        System.out.println(pizzaVegetarian);
        System.out.println();

        // Demonstrație 5: Pizza Controversată (cu ananas!)
        System.out.println("=== 5. Pizza Hawaiană (controversată - cu ananas!) ===");
        Pizza pizzaHawaiian = Pizza.builder()
                .cuNume("Pizza Hawaiian")
                .cuBlat(TipBlat.TRADITIONAL)
                .cuSos(TipSos.ROSII)
                .adaugaToppings(
                    Topping.MOZZARELLA_EXTRA,
                    Topping.SUNCA,
                    Topping.ANANAS  // Controversat!
                )
                .build();
        System.out.println(pizzaHawaiian);
        System.out.println();

        // Demonstrație 6: Pizza fără gluten
        System.out.println("=== 6. Pizza Fără Gluten (pentru alergici) ===");
        Pizza pizzaFaraGluten = Pizza.builder()
                .cuNume("Pizza Fără Gluten")
                .cuBlat(TipBlat.FARA_GLUTEN)
                .cuSos(TipSos.SMANTANA)
                .adaugaToppings(
                    Topping.GORGONZOLA,
                    Topping.PARMEZAN,
                    Topping.RUCOLA,
                    Topping.ROSII_CHERRY
                )
                .vegetariana()
                .cuGramaj(350)
                .build();
        System.out.println(pizzaFaraGluten);
        System.out.println();

        // Demonstrație 7: Validare - încercare de construire fără elemente obligatorii
        System.out.println("=== 7. Validare Builder Pattern ===");
        try {
            System.out.println("Încercare de construire pizza fără blat...");
            Pizza pizzaInvalida = Pizza.builder()
                    .cuSos(TipSos.ROSII)
                    .build(); // Va arunca excepție
            System.out.println("✗ NU AR TREBUI SĂ AJUNGEM AICI!");
        } catch (IllegalStateException e) {
            System.out.println("✓ Excepție corectă: " + e.getMessage());
        }

        try {
            System.out.println("\nÎncercare de construire pizza fără sos...");
            Pizza pizzaInvalida2 = Pizza.builder()
                    .cuBlat(TipBlat.TRADITIONAL)
                    .build(); // Va arunca excepție
            System.out.println("✗ NU AR TREBUI SĂ AJUNGEM AICI!");
        } catch (IllegalStateException e) {
            System.out.println("✓ Excepție corectă: " + e.getMessage());
        }
        System.out.println();

        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              CONTINUARE CU MENIUL PRINCIPAL              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        Meniu menu = new Meniu();

        // Creăm câteva pizze customizate pentru meniu
        Pizza pizzaMargheritaMeniu = Pizza.builder()
                .cuNume("Pizza Margherita")
                .cuBlat(TipBlat.SUBTIRE)
                .cuSos(TipSos.ROSII)
                .adaugaToppings(Topping.MOZZARELLA_EXTRA, Topping.ROSII_CHERRY)
                .vegetariana()
                .build();

        Pizza pizzaQuattroFormaggi = Pizza.builder()
                .cuNume("Pizza Quattro Formaggi")
                .cuBlat(TipBlat.TRADITIONAL)
                .cuSos(TipSos.SMANTANA)
                .adaugaToppings(Topping.MOZZARELLA_EXTRA, Topping.GORGONZOLA,
                               Topping.PARMEZAN, Topping.RUCOLA)
                .vegetariana()
                .build();

        List<Produs> produse = Arrays.asList(
                pizzaMargheritaMeniu.caMancare(),  // Pizza customizată 1
                pizzaQuattroFormaggi.caMancare(),  // Pizza customizată 2
                new Mancare("Pizza", 45.0, 450),
                new Mancare("Paste Carbonara", 52.5, 400),
                new Mancare("Paste Vegetariene", 48.0, 380, true),
                new Mancare("Salată de vinete", 18.0, 150, Categorie.APERITIVE, true),
                new Mancare("Hummus", 22.0, 200, Categorie.APERITIVE, true),
                new Mancare("Bruschette", 25.0, 180, Categorie.APERITIVE, true),
                new Mancare("Tiramisu", 25.0, 200, Categorie.DESERT),
                new Mancare("Cheesecake", 28.0, 180, Categorie.DESERT),
                new Mancare("Salată de fructe", 20.0, 250, Categorie.DESERT, true),
                new Mancare("Risotto cu ciuperci", 55.0, 350, Categorie.FEL_PRINCIPAL, true),
                new Mancare("Friptură de vită", 120.0, 300, Categorie.FEL_PRINCIPAL),
                new Bauturi("Limonada", 15.0, 400),
                new Bauturi("Apa", 8.0, 500),
                new Bauturi("Vin Rosu", 25.0, 150, true),
                new Bauturi("Bere", 12.0, 330, true)
        );
        menu.setProduse(produse);
        System.out.println(menu);

        // Demonstrate accessing products by category
        System.out.println("\n\n=== Accesare produse pe categorii ===");
        System.out.println("\nAperitive:");
        for (Produs p : menu.getProdusePerCategorie(Categorie.APERITIVE)) {
            System.out.println("  - " + p);
        }

        System.out.println("\nFel Principal:");
        for (Produs p : menu.getProdusePerCategorie(Categorie.FEL_PRINCIPAL)) {
            System.out.println("  - " + p);
        }

        System.out.println("\nDesert:");
        for (Produs p : menu.getProdusePerCategorie(Categorie.DESERT)) {
            System.out.println("  - " + p);
        }

        System.out.println("\nBăuturi Răcoritoare:");
        for (Produs p : menu.getProdusePerCategorie(Categorie.BAUTURI_RACORITOARE)) {
            System.out.println("  - " + p);
        }

        System.out.println("\nBăuturi Alcoolice:");
        for (Produs p : menu.getProdusePerCategorie(Categorie.BAUTURI_ALCOOLICE)) {
            System.out.println("  - " + p);
        }

        // Demonstrate Complex Queries using Streams API
        System.out.println("\n\n=== Interogări Complexe pentru Management ===");

        // Query 1: All vegetarian dishes sorted alphabetically
        System.out.println("\n1. Preparate vegetariene (sortate alfabetic):");
        List<Mancare> vegetariene = menu.getPreparateVegetarianeSortate();
        if (vegetariene.isEmpty()) {
            System.out.println("  Nu există preparate vegetariene în meniu.");
        } else {
            for (Mancare m : vegetariene) {
                System.out.println("  - " + m);
            }
        }

        // Query 2: Average price of desserts
        System.out.println("\n2. Prețul mediu al deserturilor:");
        double pretMediuDeserturi = menu.getPretMediuPerCategorie(Categorie.DESERT);
        System.out.println("  Preț mediu: " + String.format("%.2f", pretMediuDeserturi) + " RON");

        // Query 3: Check if any dish costs more than 100 RON
        System.out.println("\n3. Există preparate cu prețul mai mare de 100 RON?");
        boolean existaPretMare = menu.existaPreparatCuPretMaiMareDe(100.0);
        System.out.println("  Răspuns: " + (existaPretMare ? "DA" : "NU"));
        if (existaPretMare) {
            System.out.println("  Preparate cu preț > 100 RON:");
            menu.getProduse().stream()
                    .filter(p -> p.getPret() > 100.0)
                    .forEach(p -> System.out.println("    - " + p));
        }

        // Demonstrate Safe Search functionality using Optional
        System.out.println("\n\n=== Căutare Sigură în Meniu ===");

        // Search 1: Product found (exact match)
        System.out.println("\n1. Căutare produs existent (exact): 'Pizza'");
        Optional<Produs> rezultat1 = menu.cautaProduseDupaNume("Pizza");
        if (rezultat1.isPresent()) {
            System.out.println("  ✓ Produs găsit: " + rezultat1.get());
        } else {
            System.out.println("  ✗ Produsul nu a fost găsit.");
        }

        // Search 2: Product not found (typo)
        System.out.println("\n2. Căutare cu greșeală de tastare: 'Piza' (lipsește z)");
        Optional<Produs> rezultat2 = menu.cautaProduseDupaNume("Piza");
        if (rezultat2.isPresent()) {
            System.out.println("  ✓ Produs găsit: " + rezultat2.get());
        } else {
            System.out.println("  ✗ Produsul nu a fost găsit în meniu.");
        }

        // Search 3: Case-insensitive search
        System.out.println("\n3. Căutare case-insensitive: 'pizza' (litere mici)");
        Optional<Produs> rezultat3 = menu.cautaProduseDupaNumeIgnoreCase("pizza");
        if (rezultat3.isPresent()) {
            System.out.println("  ✓ Produs găsit: " + rezultat3.get());
        } else {
            System.out.println("  ✗ Produsul nu a fost găsit.");
        }

        // Search 4: Using orElse for default value
        System.out.println("\n4. Căutare cu valoare default: 'Sushi' (nu există)");
        Produs rezultat4 = menu.cautaProduseDupaNume("Sushi")
                .orElse(null);
        if (rezultat4 != null) {
            System.out.println("  ✓ Produs găsit: " + rezultat4);
        } else {
            System.out.println("  ✗ Produsul 'Sushi' nu este disponibil în meniu.");
        }

        // Search 5: Partial search
        System.out.println("\n5. Căutare parțială: 'Paste' (conține substring)");
        List<Produs> rezultat5 = menu.cautaProdusePartial("Paste");
        if (rezultat5.isEmpty()) {
            System.out.println("  ✗ Nu s-au găsit produse care conțin 'Paste'.");
        } else {
            System.out.println("  ✓ Produse găsite (" + rezultat5.size() + "):");
            rezultat5.forEach(p -> System.out.println("    - " + p));
        }

        // Search 6: Using ifPresentOrElse (Java 9+)
        System.out.println("\n6. Verificare alergeni pentru 'Tiramisu':");
        menu.cautaProduseDupaNume("Tiramisu")
                .ifPresentOrElse(
                    p -> System.out.println("  ✓ " + p.getNume() + " găsit. Preț: " + p.getPret() + " RON"),
                    () -> System.out.println("  ✗ Produsul nu a fost găsit în meniu.")
                );

        // Search 7: Empty/null search
        System.out.println("\n7. Căutare cu string gol:");
        Optional<Produs> rezultat7 = menu.cautaProduseDupaNume("");
        System.out.println("  Rezultat: " + (rezultat7.isPresent() ? "Găsit" : "Nu s-a efectuat căutarea"));

        // Demonstrate Comanda (order) usage
        System.out.println("\n\n=== Demonstrare Comandă cu Pizza Customizată ===");
        Comanda c = new Comanda();
        c.adaugaItem(produse.get(0), 2); // 2 x Pizza Margherita (customizată)
        c.adaugaItem(produse.get(12), 2); // 2 x Limonada
        c.adaugaItem(produse.get(7), 1); // 1 x Risotto cu ciuperci

        // Add promotions
        c.addPromotion(new PercentOffTotalPromotion(0.10)); // 10% off total

        System.out.println();
        System.out.println("Now (system time):\n" + c);

        // Show another order with the controversial Hawaiian pizza
        System.out.println("\n=== Comandă cu Pizza Hawaiian (controversată!) ===");
        Comanda c2 = new Comanda();
        c2.adaugaItem(pizzaHawaiian.caMancare(), 1);
        c2.adaugaItem(produse.get(13), 1); // 1 x Apa
        System.out.println(c2);
    }
}
