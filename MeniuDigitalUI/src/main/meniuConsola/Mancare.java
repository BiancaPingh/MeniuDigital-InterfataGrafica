public final class Mancare extends Produs {
    private final int gramaj;
    private final Categorie categorie;
    private final boolean vegetarian;

    public Mancare(String nume, double pret, int gramaj) {
        this(nume, pret, gramaj, Categorie.FEL_PRINCIPAL, false);
    }

    public Mancare(String nume, double pret, int gramaj, Categorie categorie) {
        this(nume, pret, gramaj, categorie, false);
    }

    public Mancare(String nume, double pret, int gramaj, boolean vegetarian) {
        this(nume, pret, gramaj, Categorie.FEL_PRINCIPAL, vegetarian);
    }

    public Mancare(String nume, double pret, int gramaj, Categorie categorie, boolean vegetarian) {
        super(nume, pret);
        this.gramaj = gramaj;
        this.categorie = categorie;
        this.vegetarian = vegetarian;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    @Override
    public Categorie getCategorie() {
        return categorie;
    }

    @Override
    public String toString() {
        return super.toString() + " - Gramaj: " + gramaj + "g" + (vegetarian ? " [Vegetarian]" : "");
    }
}
