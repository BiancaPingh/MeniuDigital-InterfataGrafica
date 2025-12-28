package unitbv.devops.meniudigitalui.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public final class AppContext {
    private static final PromotionService PROMOTION_SERVICE = new PromotionService();

    private static final BooleanProperty HAPPY_HOUR_ENABLED = new SimpleBooleanProperty(true);
    private static final BooleanProperty MEAL_DEAL_ENABLED = new SimpleBooleanProperty(true);
    private static final BooleanProperty PARTY_PACK_ENABLED = new SimpleBooleanProperty(true);

    private AppContext() {
    }

    public static PromotionService promotions() {
        return PROMOTION_SERVICE;
    }

    public static boolean isHappyHourEnabled() {
        return HAPPY_HOUR_ENABLED.get();
    }

    public static void setHappyHourEnabled(boolean enabled) {
        HAPPY_HOUR_ENABLED.set(enabled);
    }

    public static BooleanProperty happyHourEnabledProperty() {
        return HAPPY_HOUR_ENABLED;
    }

    public static boolean isMealDealEnabled() {
        return MEAL_DEAL_ENABLED.get();
    }

    public static void setMealDealEnabled(boolean enabled) {
        MEAL_DEAL_ENABLED.set(enabled);
    }

    public static BooleanProperty mealDealEnabledProperty() {
        return MEAL_DEAL_ENABLED;
    }

    public static boolean isPartyPackEnabled() {
        return PARTY_PACK_ENABLED.get();
    }

    public static void setPartyPackEnabled(boolean enabled) {
        PARTY_PACK_ENABLED.set(enabled);
    }

    public static BooleanProperty partyPackEnabledProperty() {
        return PARTY_PACK_ENABLED;
    }
}
