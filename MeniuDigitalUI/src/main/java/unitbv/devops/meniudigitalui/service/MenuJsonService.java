package unitbv.devops.meniudigitalui.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import unitbv.devops.meniudigitalui.entity.Bauturi;
import unitbv.devops.meniudigitalui.entity.Mancare;
import unitbv.devops.meniudigitalui.entity.Produs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuJsonService {

    public record ProductJson(String type, String nume, double pret, String descriere, boolean vegetarian,
                              String ingrediente, Double gramajVolum) {
    }

    private final ObjectMapper mapper;

    public MenuJsonService() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void exportMenu(List<Produs> produse, File file) throws IOException {
        List<ProductJson> out = new ArrayList<>();
        for (Produs p : produse) {
            out.add(new ProductJson(
                    p.getClass().getSimpleName(),
                    p.getNume(),
                    p.getPret(),
                    p.getDescriere(),
                    Boolean.TRUE.equals(p.getVegetarian()),
                    p.getIngrediente(),
                    p.getGramajVolum()
            ));
        }
        mapper.writeValue(file, out);
    }

    public List<Produs> importMenu(File file) throws IOException {
        List<ProductJson> in = mapper.readValue(file, new TypeReference<>() {
        });

        List<Produs> produse = new ArrayList<>();
        for (ProductJson pj : in) {
            Produs p;
            if ("Bauturi".equalsIgnoreCase(pj.type()) || "Bautura".equalsIgnoreCase(pj.type()) || "BAUTURA".equalsIgnoreCase(pj.type())) {
                p = new Bauturi(pj.nume(), pj.pret(), pj.descriere(), pj.vegetarian());
            } else {
                p = new Mancare(pj.nume(), pj.pret(), pj.descriere(), pj.vegetarian());
            }
            p.setIngrediente(pj.ingrediente());
            p.setGramajVolum(pj.gramajVolum());
            produse.add(p);
        }
        return produse;
    }
}

