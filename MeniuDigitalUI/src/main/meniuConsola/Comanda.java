import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Comanda {
    private final List<ItemComanda> items = new ArrayList<>();
    private final List<Promotion> promotions = new ArrayList<>();

    public Comanda() {}

    public void setPromotions(List<Promotion> promotions) {
        this.promotions.clear();
        if (promotions != null) {
            this.promotions.addAll(promotions);
        }
    }

    public void addPromotion(Promotion p) {
        if (p != null) this.promotions.add(p);
    }

    public void adaugaItem(Produs produs, int cantitate) {
        if (produs == null) throw new IllegalArgumentException("Produs nu poate fi null");
        // runtime guard: only Mancare or Bauturi allowed
        if (!(produs instanceof Mancare) && !(produs instanceof Bauturi)) {
            throw new IllegalArgumentException("Se permit doar Mancare sau Bauturi in comanda. Produs invalid: " + produs);
        }
        if (cantitate <= 0) throw new IllegalArgumentException("Cantitatea trebuie > 0");
        // could merge same product into same item, but keep simple: add new item
        items.add(new ItemComanda(produs, cantitate));
    }

    public double calculSubtotal() {
        double sum = 0.0;
        for (ItemComanda it : items) sum += it.getSubtotal();
        return sum;
    }

    public PromotionResult applyPromotions(LocalDateTime now) {
        PromotionResult combined = new PromotionResult();
        for (Promotion p : promotions) {
            PromotionResult r = p.apply(this, now);
            if (r == null) continue;
            combined.addDiscountAmount(r.getDiscountAmount());
            for (ItemComanda free : r.getFreeItems()) combined.addFreeItem(free);
            if (combined.getDescription() == null || combined.getDescription().isEmpty()) {
                combined.setDescription(r.getDescription());
            } else if (r.getDescription() != null && !r.getDescription().isEmpty()) {
                combined.setDescription(combined.getDescription() + "; " + r.getDescription());
            }
        }
        return combined;
    }

    public double calculTaxa(LocalDateTime now) {
        PromotionResult r = applyPromotions(now);
        double taxable = Math.max(0.0, calculSubtotal() - r.getDiscountAmount());
        return taxable * Config.getTVA();
    }

    public double calculTotal(LocalDateTime now) {
        PromotionResult r = applyPromotions(now);
        double subtotal = calculSubtotal();
        double afterDiscount = Math.max(0.0, subtotal - r.getDiscountAmount());
        double tax = afterDiscount * Config.getTVA();
        return afterDiscount + tax;
    }

    public List<ItemComanda> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public String toString() {
        return toStringWithTime(LocalDateTime.now());
    }

    public String toStringWithTime(LocalDateTime now) {
        PromotionResult r = applyPromotions(now);
        StringBuilder sb = new StringBuilder("---- Comanda ----\n");
        if (items.isEmpty() && r.getFreeItems().isEmpty()) {
            sb.append("(gol)\n");
        } else {
            for (ItemComanda it : items) sb.append(it).append("\n");
            if (!r.getFreeItems().isEmpty()) {
                sb.append("-- Free items from promotions --\n");
                for (ItemComanda free : r.getFreeItems()) sb.append("(FREE) ").append(free).append("\n");
            }
            double subtotal = calculSubtotal();
            sb.append(String.format("Subtotal: %.2f RON\n", subtotal));
            if (r.getDiscountAmount() > 0) sb.append(String.format("Discounts: -%.2f RON (%s)\n", r.getDiscountAmount(), r.getDescription()));
            double taxable = Math.max(0.0, subtotal - r.getDiscountAmount());
            double tax = taxable * Config.getTVA();
            sb.append(String.format("TVA (%.0f%%): %.2f RON\n", Config.getTVA() * 100, tax));
            sb.append(String.format("Total: %.2f RON\n", taxable + tax));
        }
        sb.append("-----------------");
        return sb.toString();
    }
}
