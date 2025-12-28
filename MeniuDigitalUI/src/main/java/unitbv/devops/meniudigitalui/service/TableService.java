package unitbv.devops.meniudigitalui.service;

import unitbv.devops.meniudigitalui.entity.Masa;
import unitbv.devops.meniudigitalui.repository.MasaRepository;

import java.util.List;

public class TableService {
    private final MasaRepository masaRepository = new MasaRepository();

    public List<Masa> getAllTables() {
        return masaRepository.findAll();
    }

    public Masa ensureTableExists(int numar) {
        return masaRepository.findByNumar(numar).orElseGet(() -> masaRepository.save(new Masa(numar)));
    }

    public void setOccupied(Masa masa, boolean ocupata) {
        masa.setOcupata(ocupata);
        masaRepository.update(masa);
    }
}

