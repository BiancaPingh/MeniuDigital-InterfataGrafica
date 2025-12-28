public class TestConfig {
    public static void main(String[] args) {
        System.out.println("=== Test Încărcare Configurație ===");
        System.out.println("Restaurant: " + Config.getNumeRestaurant());
        System.out.println("TVA: " + (Config.getTVA() * 100) + "%");
        System.out.println("Config încărcată: " + Config.isConfigIncarcata());
    }
}

