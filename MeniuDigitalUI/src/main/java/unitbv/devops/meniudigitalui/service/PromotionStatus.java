package unitbv.devops.meniudigitalui.service;

public class PromotionStatus {
    private boolean happyHourActive = false;
    private boolean mealDealActive = false;
    private boolean partyPackActive = false;

    public boolean isHappyHourActive() {
        return happyHourActive;
    }

    public void setHappyHourActive(boolean active) {
        this.happyHourActive = active;
    }

    public boolean isMealDealActive() {
        return mealDealActive;
    }

    public void setMealDealActive(boolean active) {
        this.mealDealActive = active;
    }

    public boolean isPartyPackActive() {
        return partyPackActive;
    }

    public void setPartyPackActive(boolean active) {
        this.partyPackActive = active;
    }
}

