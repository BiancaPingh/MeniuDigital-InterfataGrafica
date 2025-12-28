public class ItemComanda {
    private Produs produs;
    private int cantitate;

    public ItemComanda(Produs produs, int cantitate) {
        if (produs == null) {
            throw new IllegalArgumentException("Produs nu poate fi null");
        }
        // allow only Mancare or Bauturi
        if (!(produs instanceof Mancare) && !(produs instanceof Bauturi)) {
            throw new IllegalArgumentException("Se permit doar Mancare sau Bauturi in comanda. Produs invalid: " + produs);
        }
        if (cantitate <= 0) {
            throw new IllegalArgumentException("Cantitatea trebuie sa fie pozitiva");
        }
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public Produs getProdus() {
        return produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public double getSubtotal() {
        return produs.getPret() * cantitate;
    }

    @Override
    public String toString() {
        return cantitate + " x " + produs.getNume() + " -> " + String.format("%.2f RON", getSubtotal());
    }
}
