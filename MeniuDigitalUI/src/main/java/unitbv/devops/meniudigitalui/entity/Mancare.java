package unitbv.devops.meniudigitalui.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANCARE")
public class Mancare extends Produs {
    public Mancare() {
    }

    public Mancare(String nume, Double pret, String descriere, Boolean vegetarian) {
        super(nume, pret, descriere, vegetarian);
    }
}

