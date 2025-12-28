import java.time.LocalDateTime;

public class PercentOffTotalPromotion implements Promotion {
    private double percent; // 0.10 for 10%
    public PercentOffTotalPromotion(double percent) { this.percent = percent; }

    @Override
    public PromotionResult apply(Comanda comanda, LocalDateTime now) {
        PromotionResult res = new PromotionResult();
        double subtotal = comanda.calculSubtotal();
        if (subtotal <= 0) return res;
        double discount = subtotal * percent;
        res.addDiscountAmount(discount);
        res.setDescription(String.format("%d%% off total", (int)(percent*100)));
        return res;
    }
}

