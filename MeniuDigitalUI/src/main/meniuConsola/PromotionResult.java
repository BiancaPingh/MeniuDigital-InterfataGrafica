import java.util.ArrayList;
import java.util.List;

public class PromotionResult {
    private double discountAmount; // positive number, how much to subtract from subtotal
    private List<ItemComanda> freeItems = new ArrayList<>();
    private String description;

    public PromotionResult(double discountAmount, String description) {
        this.discountAmount = discountAmount;
        this.description = description;
    }

    public PromotionResult() { this(0.0, ""); }

    public double getDiscountAmount() { return discountAmount; }
    public void addDiscountAmount(double d) { this.discountAmount += d; }

    public List<ItemComanda> getFreeItems() { return new ArrayList<>(freeItems); }
    public void addFreeItem(ItemComanda item) { this.freeItems.add(item); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

