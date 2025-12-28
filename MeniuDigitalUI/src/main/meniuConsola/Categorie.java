public enum Categorie {
    APERITIVE("Aperitive"),
    FEL_PRINCIPAL("Fel Principal"),
    DESERT("Desert"),
    BAUTURI_RACORITOARE("Băuturi Răcoritoare"),
    BAUTURI_ALCOOLICE("Băuturi Alcoolice");

    private final String denumire;

    Categorie(String denumire) {
        this.denumire = denumire;
    }

    public String getDenumire() {
        return denumire;
    }

    @Override
    public String toString() {
        return denumire;
    }
}

