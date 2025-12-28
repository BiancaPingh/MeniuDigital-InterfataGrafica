package unitbv.devops.meniudigitalui.service;

public class ReceiptLine {
    private final Integer produsId;
    private final String descriere;
    private final int cantitate;
    private final double pretUnitar;
    private final double total;
    private final boolean discount;

    public ReceiptLine(Integer produsId, String descriere, int cantitate, double pretUnitar, double total, boolean discount) {
        this.produsId = produsId;
        this.descriere = descriere;
        this.cantitate = cantitate;
        this.pretUnitar = pretUnitar;
        this.total = total;
        this.discount = discount;
    }

    public static ReceiptLine product(Integer produsId, String descriere, int cantitate, double pretUnitar) {
        return new ReceiptLine(produsId, descriere, cantitate, pretUnitar, pretUnitar * cantitate, false);
    }

    public static ReceiptLine discount(String descriere, double amount) {
        return new ReceiptLine(null, descriere, 1, amount, amount, true);
    }

    public Integer getProdusId() {
        return produsId;
    }

    public String getDescriere() {
        return descriere;
    }

    public int getCantitate() {
        return cantitate;
    }

    public double getPretUnitar() {
        return pretUnitar;
    }

    public double getTotal() {
        return total;
    }

    public boolean isDiscount() {
        return discount;
    }
}
