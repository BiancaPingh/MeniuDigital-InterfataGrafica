package unitbv.devops.meniudigitalui.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BAUTURA")
public class Bauturi extends Produs {
    public Bauturi() {
    }

    public Bauturi(String nume, Double pret, String descriere, Boolean vegetarian) {
        super(nume, pret, descriere, vegetarian);
    }
}

