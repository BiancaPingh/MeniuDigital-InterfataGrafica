import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeWindowDrinkDiscountPromotion implements Promotion {
    private LocalTime from;
    private LocalTime to;
    private double discountRate; // e.g., 0.20 for 20%

    public TimeWindowDrinkDiscountPromotion(LocalTime from, LocalTime to, double discountRate) {
        this.from = from;
        this.to = to;
        this.discountRate = discountRate;
    }

    @Override
    public PromotionResult apply(Comanda comanda, LocalDateTime now) {
        PromotionResult res = new PromotionResult();
        LocalTime t = now.toLocalTime();
        boolean inWindow;
        if (from.isBefore(to) || from.equals(to)) {
            inWindow = !t.isBefore(from) && !t.isAfter(to);
        } else {
            // window wraps midnight
            inWindow = !t.isBefore(from) || !t.isAfter(to);
        }
        if (!inWindow) return res;

        double discount = 0.0;
        for (ItemComanda it : comanda.getItems()) {
            if (it.getProdus() instanceof Bauturi) {
                Bauturi b = (Bauturi) it.getProdus();
                if (b.isAlcoholic()) {
                    discount += it.getSubtotal() * discountRate;
                }
            }
        }
        if (discount > 0) {
            res.addDiscountAmount(discount);
            res.setDescription("Happy Hour: " + (int)(discountRate*100) + "% off alcoholic drinks");
        }
        return res;
    }
}

