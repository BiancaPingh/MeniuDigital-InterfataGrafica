package unitbv.devops.meniudigitalui.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categorii")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nume;

    @Column
    private String descriere;

    public Categorie() {
    }

    public Categorie(String nume) {
        this.nume = nume;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }
}

