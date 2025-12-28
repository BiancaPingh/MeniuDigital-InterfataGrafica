public enum TipSos {
    ROSII("Sos de Roșii", 0.0),
    SMANTANA("Sos de Smântână", 0.0),
    BBQ("Sos BBQ", 3.0),
    PESTO("Sos Pesto", 5.0),
    PICANT("Sos Picant", 2.0),
    USTUROI("Sos Usturoi", 3.0);

    private final String denumire;
    private final double pretSuplimentar;

    TipSos(String denumire, double pretSuplimentar) {
        this.denumire = denumire;
        this.pretSuplimentar = pretSuplimentar;
    }

    public String getDenumire() {
        return denumire;
    }

    public double getPretSuplimentar() {
        return pretSuplimentar;
    }

    @Override
    public String toString() {
        return denumire + (pretSuplimentar > 0 ? " (+" + pretSuplimentar + " RON)" : "");
    }
}

