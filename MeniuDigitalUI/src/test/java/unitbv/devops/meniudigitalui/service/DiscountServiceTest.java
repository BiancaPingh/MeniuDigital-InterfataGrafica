package unitbv.devops.meniudigitalui.service;

import org.junit.jupiter.api.Test;
import unitbv.devops.meniudigitalui.entity.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountServiceTest {

    @Test
    void happyHour_everySecondDrink_halfPrice_asNegativeLine() {
        AppContext.promotions().setHappyHour(true);
        AppContext.promotions().setMealDeal(false);
        AppContext.promotions().setPartyPack(false);

        DiscountService svc = new DiscountService();

        Comanda comanda = new Comanda();
        comanda.setFinalizata(false);
        comanda.setItems(new ArrayList<>());

        Bauturi cola = new Bauturi("Cola", 10.0, "", true);
        comanda.getItems().add(new ComandaItem(comanda, cola, 3, 10.0));

        List<ReceiptLine> lines = svc.buildReceiptLines(comanda);
        assertTrue(lines.stream().anyMatch(l -> l.isDiscount() && l.getTotal() < 0), "Expected a negative discount line");

        svc.applyDiscounts(comanda);
        assertEquals(25.0, comanda.getTotal(), 0.0001);
    }

    @Test
    void mealDeal_withPizza_cheapestDessert_25percent_asNegativeLine() {
        AppContext.promotions().setHappyHour(false);
        AppContext.promotions().setMealDeal(true);
        AppContext.promotions().setPartyPack(false);

        DiscountService svc = new DiscountService();

        Comanda comanda = new Comanda();
        comanda.setFinalizata(false);
        comanda.setItems(new ArrayList<>());

        Mancare pizza = new Mancare("Pizza Margherita", 30.0, "", false);
        Mancare tiramisu = new Mancare("Tiramisu", 12.0, "", true);
        Mancare cheesecake = new Mancare("Cheesecake", 20.0, "", true);

        comanda.getItems().add(new ComandaItem(comanda, pizza, 1, 30.0));
        comanda.getItems().add(new ComandaItem(comanda, tiramisu, 1, 12.0));
        comanda.getItems().add(new ComandaItem(comanda, cheesecake, 1, 20.0));

        List<ReceiptLine> lines = svc.buildReceiptLines(comanda);
        assertTrue(lines.stream().anyMatch(l -> l.isDiscount() && l.getDescriere().toLowerCase().contains("meal deal")));

        svc.applyDiscounts(comanda);
        assertEquals(30.0 + 12.0 + 20.0 - 3.0, comanda.getTotal(), 0.0001);
    }

    @Test
    void partyPack_every4Pizzas_oneFree_cheapestPizza_asNegativeLine() {
        AppContext.promotions().setHappyHour(false);
        AppContext.promotions().setMealDeal(false);
        AppContext.promotions().setPartyPack(true);

        DiscountService svc = new DiscountService();

        Comanda comanda = new Comanda();
        comanda.setFinalizata(false);
        comanda.setItems(new ArrayList<>());

        Mancare p1 = new Mancare("Pizza A", 10.0, "", false);
        Mancare p2 = new Mancare("Pizza B", 15.0, "", false);

        comanda.getItems().add(new ComandaItem(comanda, p1, 2, 10.0));
        comanda.getItems().add(new ComandaItem(comanda, p2, 2, 15.0));

        List<ReceiptLine> lines = svc.buildReceiptLines(comanda);
        assertTrue(lines.stream().anyMatch(l -> l.isDiscount() && l.getDescriere().toLowerCase().contains("party pack")));

        svc.applyDiscounts(comanda);
        assertEquals((2 * 10.0) + (2 * 15.0) - 10.0, comanda.getTotal(), 0.0001);
    }

    @Test
    void happyHour_mixedDrinkPrices_discountsCheapestHalfForEverySecondUnit() {
        AppContext.promotions().setHappyHour(true);
        AppContext.promotions().setMealDeal(false);
        AppContext.promotions().setPartyPack(false);

        DiscountService svc = new DiscountService();

        Comanda comanda = new Comanda();
        comanda.setFinalizata(false);
        comanda.setItems(new ArrayList<>());

        Bauturi cheap = new Bauturi("Water", 8.0, "", true);
        Bauturi expensive = new Bauturi("Beer", 12.0, "", true);

        comanda.getItems().add(new ComandaItem(comanda, cheap, 1, 8.0));
        comanda.getItems().add(new ComandaItem(comanda, expensive, 2, 12.0));

        // total drinks units = 3 => discounted units = 1
        // cheapest unit is 8 => discount = -4
        svc.applyDiscounts(comanda);
        assertEquals(8.0 + (2 * 12.0) - 4.0, comanda.getTotal(), 0.0001);

        List<ReceiptLine> lines = svc.buildReceiptLines(comanda);
        assertTrue(lines.stream().anyMatch(l -> l.isDiscount() && l.getTotal() == -4.0));
    }
}
