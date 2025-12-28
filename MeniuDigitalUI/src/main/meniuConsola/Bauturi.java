public final class Bauturi extends Produs{
    private final int volum; // in ml
    private final boolean alcoolic; // true if alcoholic

    public Bauturi(String nume, double pret, int volum) {
        this(nume, pret, volum, false);
    }

    public Bauturi(String nume, double pret, int volum, boolean alcoolic) {
        super(nume, pret);
        this.volum = volum;
        this.alcoolic = alcoolic;
    }

    public boolean isAlcoholic() {
        return alcoolic;
    }

    @Override
    public Categorie getCategorie() {
        return alcoolic ? Categorie.BAUTURI_ALCOOLICE : Categorie.BAUTURI_RACORITOARE;
    }

    @Override
    public String toString() {
        return super.toString() + " - Volum: " + volum + "ml" + (alcoolic ? " - Alcoolic" : "");
    }
}
