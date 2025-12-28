public enum TipBlat {
    SUBTIRE("Blat Subțire", 0.0),
    TRADITIONAL("Blat Tradițional", 0.0),
    PUFOS("Blat Pufos", 5.0),
    INTEGRAL("Blat Integral", 7.0),
    FARA_GLUTEN("Blat Fără Gluten", 10.0);

    private final String denumire;
    private final double pretSuplimentar;

    TipBlat(String denumire, double pretSuplimentar) {
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

