package unitbv.devops.meniudigitalui.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comenzi")
public class Comanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "masa_id", nullable = false)
    private Masa masa;

    @Column(nullable = false)
    private LocalDateTime dataOra;

    @Column(nullable = false)
    private Double total = 0.0;

    @Column(nullable = false)
    private Boolean finalizata = false;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComandaItem> items;

    public Comanda() {
    }

    public Comanda(User user, Masa masa) {
        this.user = user;
        this.masa = masa;
        this.dataOra = LocalDateTime.now();
        this.total = 0.0;
        this.finalizata = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Masa getMasa() {
        return masa;
    }

    public void setMasa(Masa masa) {
        this.masa = masa;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Boolean getFinalizata() {
        return finalizata;
    }

    public void setFinalizata(Boolean finalizata) {
        this.finalizata = finalizata;
    }

    public List<ComandaItem> getItems() {
        return items;
    }

    public void setItems(List<ComandaItem> items) {
        this.items = items;
    }
}
