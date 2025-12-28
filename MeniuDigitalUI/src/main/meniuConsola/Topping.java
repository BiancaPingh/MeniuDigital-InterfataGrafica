public enum Topping {
    MOZZARELLA_EXTRA("Extra Mozzarella", 8.0),
    CIUPERCI("Ciuperci", 6.0),
    SALAM("Salam", 7.0),
    ANANAS("Ananas", 5.0),
    SUNCA("Șuncă", 7.0),
    MASLINE("Măsline", 5.0),
    ARDEI("Ardei Gras", 5.0),
    CEAPA("Ceapă", 3.0),
    BACON("Bacon", 9.0),
    PORUMB("Porumb", 4.0),
    RUCOLA("Rucola", 6.0),
    ROSII_CHERRY("Roșii Cherry", 6.0),
    PARMEZAN("Parmezan", 8.0),
    GORGONZOLA("Gorgonzola", 10.0),
    PEPPERONI("Pepperoni", 8.0),
    TON("Ton", 9.0),
    CAPERE("Capere", 5.0),
    USTUROI("Usturoi", 3.0);

    private final String denumire;
    private final double pret;

    Topping(String denumire, double pret) {
        this.denumire = denumire;
        this.pret = pret;
    }

    public String getDenumire() {
        return denumire;
    }

    public double getPret() {
        return pret;
    }

    @Override
    public String toString() {
        return denumire + " (+" + pret + " RON)";
    }
}

