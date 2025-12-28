package unitbv.devops.meniudigitalui.service;

import unitbv.devops.meniudigitalui.entity.Comanda;
import unitbv.devops.meniudigitalui.entity.ComandaItem;
import unitbv.devops.meniudigitalui.entity.Masa;
import unitbv.devops.meniudigitalui.entity.Produs;
import unitbv.devops.meniudigitalui.entity.User;
import unitbv.devops.meniudigitalui.repository.ComandaRepository;

import java.util.List;

public class OrderService {
    private final ComandaRepository comandaRepository = new ComandaRepository();
    private final DiscountService discountService = new DiscountService();

    public Comanda createNewOrder(User user, Masa masa) {
        return new Comanda(user, masa);
    }

    public void saveOrder(Comanda comanda) {
        discountService.applyDiscounts(comanda);
        comandaRepository.save(comanda);
    }

    public void finalizeOrder(Comanda comanda) {
        comanda.setFinalizata(true);
        comandaRepository.update(comanda);
    }

    public List<Comanda> getUserOrders(Integer userId) {
        return comandaRepository.findByUserId(userId);
    }

    public List<Comanda> getUserOrdersWithUser(Integer userId) {
        return comandaRepository.findByUserIdWithUser(userId);
    }

    public List<Comanda> getAllOrders() {
        return comandaRepository.findAll();
    }

    public List<Comanda> getAllOrdersWithUser() {
        return comandaRepository.findAllWithUser();
    }

    public void addItemToOrder(Comanda comanda, Produs produs, Integer cantitate) {
        ComandaItem item = new ComandaItem(comanda, produs, cantitate, produs.getPret());
        comanda.getItems().add(item);
    }

    public void removeItemFromOrder(Comanda comanda, ComandaItem item) {
        comanda.getItems().remove(item);
    }
}
