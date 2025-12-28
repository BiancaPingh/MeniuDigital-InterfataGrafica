package unitbv.devops.meniudigitalui.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mese")
public class Masa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer numar;

    @Column(nullable = false)
    private Boolean ocupata = false;

    public Masa() {
    }

    public Masa(Integer numar) {
        this.numar = numar;
        this.ocupata = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumar() {
        return numar;
    }

    public void setNumar(Integer numar) {
        this.numar = numar;
    }

    public Boolean getOcupata() {
        return ocupata;
    }

    public void setOcupata(Boolean ocupata) {
        this.ocupata = ocupata;
    }
}

