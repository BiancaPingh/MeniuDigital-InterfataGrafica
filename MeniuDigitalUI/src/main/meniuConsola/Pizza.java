import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clasa Pizza customizabilă implementată cu Builder Pattern.
 * Pizza are elemente obligatorii (blat, sos) și topping-uri opționale.
 * Folosește compoziție cu Mancare pentru a se integra în sistemul de meniu.
 */
public final class Pizza {
    // Mancare de bază care reprezintă produsul în sistem
    private final Mancare mancare;

    // Elemente obligatorii specifice pizzei
    private final TipBlat blat;
    private final TipSos sos;

    // Elemente opționale
    private final List<Topping> toppings;

    // Constructor privat - doar Builder-ul poate crea Pizza
    private Pizza(Builder builder) {
        // Calculează prețul total
        double pretTotal = builder.pretBaza;
        pretTotal += builder.blat.getPretSuplimentar();
        pretTotal += builder.sos.getPretSuplimentar();
        for (Topping topping : builder.toppings) {
            pretTotal += topping.getPret();
        }

        // Creează obiectul Mancare cu prețul final
        this.mancare = new Mancare(builder.nume, pretTotal, builder.gramaj,
                                    Categorie.FEL_PRINCIPAL, builder.vegetarian);
        this.blat = builder.blat;
        this.sos = builder.sos;
        this.toppings = new ArrayList<>(builder.toppings);
    }

    public TipBlat getBlat() {
        return blat;
    }

    public TipSos getSos() {
        return sos;
    }

    public List<Topping> getToppings() {
        return Collections.unmodifiableList(toppings);
    }

    /**
     * Returnează obiectul Mancare pentru a putea fi adăugat în meniu
     */
    public Mancare caMancare() {
        return mancare;
    }

    /**
     * Calculează prețul total al pizzei (bază + blat + sos + toate topping-urile)
     */
    public double getPretTotal() {
        return mancare.getPret();
    }

    public String getNume() {
        return mancare.getNume();
    }

    public boolean isVegetarian() {
        return mancare.isVegetarian();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mancare.getNume()).append(" - ").append(String.format("%.2f", getPretTotal())).append(" RON");
        sb.append("\n  Blat: ").append(blat.getDenumire());
        sb.append("\n  Sos: ").append(sos.getDenumire());

        if (!toppings.isEmpty()) {
            sb.append("\n  Topping-uri:");
            for (Topping topping : toppings) {
                sb.append("\n    + ").append(topping.getDenumire());
            }
        }

        if (mancare.isVegetarian()) {
            sb.append("\n  [Vegetarian]");
        }

        return sb.toString();
    }

    /**
     * Builder Pattern pentru construirea unei Pizza customizabile.
     * Impune setarea elementelor obligatorii (blat, sos) înainte de build().
     */
    public static class Builder {
        // Elemente obligatorii - vor fi setate prin metode dedicate
        private TipBlat blat;
        private TipSos sos;

        // Elemente opționale - au valori default
        private String nume = "Pizza Personalizată";
        private double pretBaza = 25.0; // prețul de bază
        private int gramaj = 400;
        private boolean vegetarian = false;
        private List<Topping> toppings = new ArrayList<>();

        public Builder() {
            // Constructor gol - forțăm setarea explicită a elementelor obligatorii
        }

        /**
         * Setează tipul de blat (OBLIGATORIU)
         */
        public Builder cuBlat(TipBlat blat) {
            if (blat == null) {
                throw new IllegalArgumentException("Blatul nu poate fi null!");
            }
            this.blat = blat;
            return this;
        }

        /**
         * Setează tipul de sos (OBLIGATORIU)
         */
        public Builder cuSos(TipSos sos) {
            if (sos == null) {
                throw new IllegalArgumentException("Sosul nu poate fi null!");
            }
            this.sos = sos;
            return this;
        }

        /**
         * Adaugă un topping (OPȚIONAL)
         */
        public Builder adaugaTopping(Topping topping) {
            if (topping != null) {
                this.toppings.add(topping);
                // Verifică dacă pizza mai este vegetariană
                checkVegetarian();
            }
            return this;
        }

        /**
         * Adaugă multiple topping-uri (OPȚIONAL)
         */
        public Builder adaugaToppings(Topping... toppings) {
            for (Topping topping : toppings) {
                if (topping != null) {
                    this.toppings.add(topping);
                }
            }
            checkVegetarian();
            return this;
        }

        /**
         * Setează numele custom al pizzei (OPȚIONAL)
         */
        public Builder cuNume(String nume) {
            if (nume != null && !nume.trim().isEmpty()) {
                this.nume = nume;
            }
            return this;
        }

        /**
         * Setează prețul de bază (OPȚIONAL)
         */
        public Builder cuPretBaza(double pretBaza) {
            if (pretBaza >= 0) {
                this.pretBaza = pretBaza;
            }
            return this;
        }

        /**
         * Setează gramajul (OPȚIONAL)
         */
        public Builder cuGramaj(int gramaj) {
            if (gramaj > 0) {
                this.gramaj = gramaj;
            }
            return this;
        }

        /**
         * Marchează pizza ca vegetariană (OPȚIONAL)
         */
        public Builder vegetariana() {
            this.vegetarian = true;
            return this;
        }

        /**
         * Verifică dacă topping-urile sunt toate vegetariene
         * (în acest exemplu, simplificat - toate sunt considerate OK)
         */
        private void checkVegetarian() {
            // Aici ai putea adăuga logică să verifici dacă anumite topping-uri
            // (precum SALAM, SUNCA, BACON, PEPPERONI, TON) fac pizza non-vegetariană
            List<Topping> nonVegToppings = List.of(
                Topping.SALAM, Topping.SUNCA, Topping.BACON,
                Topping.PEPPERONI, Topping.TON
            );

            for (Topping t : this.toppings) {
                if (nonVegToppings.contains(t)) {
                    this.vegetarian = false;
                    return;
                }
            }
        }

        /**
         * Construiește obiectul Pizza.
         * VALIDEAZĂ că elementele obligatorii (blat, sos) sunt setate.
         */
        public Pizza build() {
            // Validare: elementele obligatorii trebuie setate
            if (blat == null) {
                throw new IllegalStateException("Blatul este obligatoriu! Folosește cuBlat() înainte de build().");
            }
            if (sos == null) {
                throw new IllegalStateException("Sosul este obligatoriu! Folosește cuSos() înainte de build().");
            }

            return new Pizza(this);
        }
    }

    /**
     * Metodă statică pentru a începe construirea unei pizza
     */
    public static Builder builder() {
        return new Builder();
    }
}

