package unitbv.devops.meniudigitalui.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comanda_items")
public class ComandaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produs_id", nullable = false)
    private Produs produs;

    @Column(nullable = false)
    private Integer cantitate;

    @Column(nullable = false)
    private Double pret;

    @Column
    private Double reducere = 0.0;

    public ComandaItem() {
    }

    public ComandaItem(Comanda comanda, Produs produs, Integer cantitate, Double pret) {
        this.comanda = comanda;
        this.produs = produs;
        this.cantitate = cantitate;
        this.pret = pret;
        this.reducere = 0.0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }

    public Integer getCantitate() {
        return cantitate;
    }

    public void setCantitate(Integer cantitate) {
        this.cantitate = cantitate;
    }

    public Double getPret() {
        return pret;
    }

    public void setPret(Double pret) {
        this.pret = pret;
    }

    public Double getReducere() {
        return reducere;
    }

    public void setReducere(Double reducere) {
        this.reducere = reducere;
    }
}

