package unitbv.devops.meniudigitalui.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "produse")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs")
public abstract class Produs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nume;

    @Column(nullable = false)
    private Double pret;

    @Column
    private String descriere;

    @Column
    private String ingrediente;

    @Column
    private Double gramajVolum;

    @Column(nullable = false)
    private Boolean vegetarian = false;

    public Produs() {
    }

    public Produs(String nume, Double pret, String descriere, Boolean vegetarian) {
        this.nume = nume;
        this.pret = pret;
        this.descriere = descriere;
        this.vegetarian = vegetarian;
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

    public Double getPret() {
        return pret;
    }

    public void setPret(Double pret) {
        this.pret = pret;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(String ingrediente) {
        this.ingrediente = ingrediente;
    }

    public Double getGramajVolum() {
        return gramajVolum;
    }

    public void setGramajVolum(Double gramajVolum) {
        this.gramajVolum = gramajVolum;
    }

    public Boolean getVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(Boolean vegetarian) {
        this.vegetarian = vegetarian;
    }
}

