public sealed abstract class Produs permits Mancare, Bauturi {
    protected String nume;
    protected double pret;

    public Produs(String nume, double pret) {
        this.nume = nume;
        this.pret = pret;
    }

    public String getNume() {
        return nume;
    }

    public double getPret() {
        return pret;
    }

    public abstract Categorie getCategorie();

    @Override
    public String toString() {
        return nume + " - " + pret + " RON";
    }
}
