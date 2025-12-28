package unitbv.devops.meniudigitalui.repository;

import org.junit.jupiter.api.Test;
import unitbv.devops.meniudigitalui.entity.*;

import static org.junit.jupiter.api.Assertions.*;

public class CascadeDeleteTest {

    @Test
    void deletingStaffUser_cascadesOrdersAndItems() {
        UserRepository userRepo = new UserRepository();
        ComandaRepository comandaRepo = new ComandaRepository();
        MasaRepository masaRepo = new MasaRepository();
        ProdusRepository produsRepo = new ProdusRepository();

        User staff = new User("cascade_test_user" + System.nanoTime(), "pass", UserRole.STAFF);
        userRepo.save(staff);

        Masa masa = new Masa(999);
        masaRepo.save(masa);

        Produs p = new Mancare("Pizza Cascade" + System.nanoTime(), 10.0, "", false);
        produsRepo.save(p);

        Comanda comanda = new Comanda(staff, masa);
        comanda.setItems(new java.util.ArrayList<>());
        comanda.getItems().add(new ComandaItem(comanda, p, 2, 10.0));

        comandaRepo.save(comanda);

        Integer orderId = comanda.getId();
        assertNotNull(orderId);

        userRepo.delete(staff.getId());

        assertTrue(comandaRepo.findById(orderId).isEmpty(), "Expected order to be deleted when staff user is deleted");
    }
}

