package unitbv.devops.meniudigitalui.service;

public class PromotionService {
    private final PromotionStatus status = new PromotionStatus();

    public PromotionStatus getStatus() {
        return status;
    }

    public void setHappyHour(boolean active) {
        status.setHappyHourActive(active);
    }

    public void setMealDeal(boolean active) {
        status.setMealDealActive(active);
    }

    public void setPartyPack(boolean active) {
        status.setPartyPackActive(active);
    }
}

