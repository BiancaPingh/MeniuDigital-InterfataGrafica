package unitbv.devops.meniudigitalui.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import unitbv.devops.meniudigitalui.entity.*;
import unitbv.devops.meniudigitalui.repository.ProdusRepository;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuService {
    private final ProdusRepository produsRepository = new ProdusRepository();
    private final MenuJsonService menuJsonService = new MenuJsonService();

    private final ObservableList<Produs> products = FXCollections.observableArrayList();

    public MenuService() {
        refreshProducts();
    }

    public void refreshProducts() {
        products.setAll(produsRepository.findAll());
    }

    public ObservableList<Produs> getProductsObservable() {
        return products;
    }

    public List<Produs> getAllProducts() {
        return produsRepository.findAll();
    }

    public List<Produs> filterInMemory(List<Produs> produse,
                                      Optional<Boolean> vegetarianOnly,
                                      Optional<String> type,
                                      Optional<Double> minPrice,
                                      Optional<Double> maxPrice,
                                      Optional<String> nameQuery) {

        return produse.stream()
                .filter(p -> vegetarianOnly.map(v -> !v || Boolean.TRUE.equals(p.getVegetarian())).orElse(true))
                .filter(p -> type.map(t -> {
                    if ("MANCARE".equalsIgnoreCase(t)) {
                        return p instanceof Mancare;
                    }
                    if ("BAUTURA".equalsIgnoreCase(t)) {
                        return p instanceof Bauturi;
                    }
                    return true;
                }).orElse(true))
                .filter(p -> minPrice.map(min -> p.getPret() >= min).orElse(true))
                .filter(p -> maxPrice.map(max -> p.getPret() <= max).orElse(true))
                .filter(p -> nameQuery.map(q -> {
                    String n = p.getNume() == null ? "" : p.getNume().toLowerCase();
                    return n.contains(q.toLowerCase());
                }).orElse(true))
                .collect(Collectors.toList());
    }

    public Optional<Produs> searchBestMatchOptional(List<Produs> produse, String query) {
        if (query == null || query.isBlank()) {
            return Optional.empty();
        }
        String q = query.trim().toLowerCase();
        return produse.stream()
                .filter(p -> p.getNume() != null)
                .filter(p -> p.getNume().toLowerCase().contains(q))
                .min(Comparator.comparingInt(p -> p.getNume().length()));
    }

    public void saveProduct(Produs produs) {
        produsRepository.save(produs);
        refreshProducts();
    }

    public void updateProduct(Produs produs) {
        produsRepository.update(produs);
        refreshProducts();
    }

    public void deleteProduct(Integer id) {
        produsRepository.delete(id);
        refreshProducts();
    }

    public void exportMenuToJson(File file) throws IOException {
        menuJsonService.exportMenu(getAllProducts(), file);
    }

    public void importMenuFromJson(File file) throws IOException {
        List<Produs> produse = menuJsonService.importMenu(file);
        for (Produs p : produse) {
            produsRepository.save(p);
        }
        refreshProducts();
    }
}
