package unitbv.devops.meniudigitalui.service;

import unitbv.devops.meniudigitalui.entity.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DiscountService {

    private PromotionStatus effectiveStatus() {
        return AppContext.promotions().getStatus();
    }

    public List<ReceiptLine> buildReceiptLines(Comanda comanda) {
        List<ReceiptLine> lines = new ArrayList<>();
        if (comanda == null || comanda.getItems() == null) {
            return lines;
        }

        for (ComandaItem item : comanda.getItems()) {
            if (item == null || item.getProdus() == null) {
                continue;
            }
            lines.add(ReceiptLine.product(
                    item.getProdus().getId(),
                    item.getProdus().getNume(),
                    item.getCantitate(),
                    item.getPret()
            ));
        }

        if (!Boolean.TRUE.equals(comanda.getFinalizata())) {
            PromotionStatus status = effectiveStatus();
            if (status.isHappyHourActive()) {
                lines.addAll(happyHourDiscountLines(comanda.getItems()));
            }
            if (status.isMealDealActive()) {
                lines.addAll(mealDealDiscountLines(comanda.getItems()));
            }
            if (status.isPartyPackActive()) {
                lines.addAll(partyPackDiscountLines(comanda.getItems()));
            }
        }

        return lines;
    }

    public void applyDiscounts(Comanda comanda) {
        if (comanda == null) {
            return;
        }
        resetItemDiscounts(comanda);

        List<ReceiptLine> lines = buildReceiptLines(comanda);
        double total = lines.stream().mapToDouble(ReceiptLine::getTotal).sum();
        comanda.setTotal(total);
    }

    private void resetItemDiscounts(Comanda comanda) {
        if (comanda.getItems() == null) {
            return;
        }
        for (ComandaItem item : comanda.getItems()) {
            if (item != null) {
                item.setReducere(0.0);
            }
        }
    }

    private List<ReceiptLine> happyHourDiscountLines(List<ComandaItem> items) {
        List<Double> drinkUnitPrices = new ArrayList<>();
        for (ComandaItem item : items) {
            if (item.getProdus() instanceof Bauturi) {
                for (int i = 0; i < item.getCantitate(); i++) {
                    drinkUnitPrices.add(item.getPret());
                }
            }
        }

        int discounted = drinkUnitPrices.size() / 2;
        if (discounted <= 0) {
            return List.of();
        }

        drinkUnitPrices.sort(Double::compareTo);

        double sumDiscountedPrices = 0.0;
        for (int i = 0; i < discounted; i++) {
            sumDiscountedPrices += drinkUnitPrices.get(i);
        }

        double amount = -(sumDiscountedPrices * 0.5);
        if (amount == 0.0) {
            return List.of();
        }
        return List.of(ReceiptLine.discount("Happy Hour (every 2nd drink -50%)", amount));
    }

    private List<ReceiptLine> mealDealDiscountLines(List<ComandaItem> items) {
        int pizzaQty = items.stream()
                .filter(i -> isPizza(i.getProdus()))
                .mapToInt(ComandaItem::getCantitate)
                .sum();

        if (pizzaQty <= 0) {
            return List.of();
        }

        ComandaItem cheapestDessert = items.stream()
                .filter(i -> isDessert(i.getProdus()))
                .min(Comparator.comparingDouble(ComandaItem::getPret))
                .orElse(null);

        if (cheapestDessert == null) {
            return List.of();
        }

        double amount = -(cheapestDessert.getPret() * 0.25);
        return List.of(ReceiptLine.discount("Meal Deal (cheapest dessert -25%)", amount));
    }

    private List<ReceiptLine> partyPackDiscountLines(List<ComandaItem> items) {
        int pizzaQty = items.stream()
                .filter(i -> isPizza(i.getProdus()))
                .mapToInt(ComandaItem::getCantitate)
                .sum();

        int freebies = pizzaQty / 4;
        if (freebies <= 0) {
            return List.of();
        }

        ComandaItem cheapestPizza = items.stream()
                .filter(i -> isPizza(i.getProdus()))
                .min(Comparator.comparingDouble(ComandaItem::getPret))
                .orElse(null);

        if (cheapestPizza == null) {
            return List.of();
        }

        double amount = -(freebies * cheapestPizza.getPret());
        return List.of(ReceiptLine.discount("Party Pack (4 pizzas -> 1 free)", amount));
    }

    private boolean isPizza(Produs produs) {
        if (produs == null) {
            return false;
        }
        String name = produs.getNume() == null ? "" : produs.getNume().toLowerCase();
        return name.contains("pizza");
    }

    private boolean isDessert(Produs produs) {
        if (!(produs instanceof Mancare)) {
            return false;
        }
        String name = produs.getNume() == null ? "" : produs.getNume().toLowerCase();
        if (name.contains("pizza")) {
            return false;
        }
        return name.contains("dessert") || name.contains("tiramisu") || name.contains("panna") || name.contains("lava") || name.contains("clat") || name.contains("cheesecake") || name.contains("inghet");
    }
}
