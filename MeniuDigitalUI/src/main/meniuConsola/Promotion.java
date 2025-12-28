import java.time.LocalDateTime;

public interface Promotion {
    // returns a PromotionResult describing discount amount and any free items added
    PromotionResult apply(Comanda comanda, LocalDateTime now);

    default String getName() { return this.getClass().getSimpleName(); }
}
