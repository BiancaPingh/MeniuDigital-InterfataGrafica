import java.time.LocalDateTime;

public class BuyXGetYFreePromotion implements Promotion {
    private String buyProductName;
    private int buyQty;
    private String freeProductName;
    private int freeQty;

    public BuyXGetYFreePromotion(String buyProductName, int buyQty, String freeProductName, int freeQty) {
        this.buyProductName = buyProductName;
        this.buyQty = buyQty;
        this.freeProductName = freeProductName;
        this.freeQty = freeQty;
    }

    @Override
    public PromotionResult apply(Comanda comanda, LocalDateTime now) {
        PromotionResult res = new PromotionResult();
        int bought = 0;
        Produs freeProd = null;
        for (ItemComanda it : comanda.getItems()) {
            if (it.getProdus().getNume().equalsIgnoreCase(buyProductName)) {
                bought += it.getCantitate();
            }
            if (it.getProdus().getNume().equalsIgnoreCase(freeProductName)) {
                freeProd = it.getProdus();
            }
        }
        if (bought >= buyQty && freeProd != null) {
            int sets = bought / buyQty;
            int totalFree = sets * freeQty;
            res.setDescription(String.format("Buy %d %s, get %d %s free (added %d)", buyQty, buyProductName, freeQty, freeProductName, totalFree));
            res.addFreeItem(new ItemComanda(freeProd, totalFree));
            // also treat the monetary value of free items as a discount
            res.addDiscountAmount(freeProd.getPret() * totalFree);
        }
        return res;
    }
}
