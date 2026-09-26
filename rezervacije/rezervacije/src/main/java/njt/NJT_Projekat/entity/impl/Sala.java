package njt.NJT_Projekat.entity.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njt.NJT_Projekat.entity.MyEntity;

@Entity
@Table(name = "sala")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sala extends MyEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSale;

    private String naziv;
    private Integer kapacitet;
    private String lokacija;
    private String opis;
    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String slike; 

    // SalaKarakteristika
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "sala_karakteristika",
        joinColumns = @JoinColumn(name = "id_sale"),
        inverseJoinColumns = @JoinColumn(name = "id_karakteristike")
    )
    private List<Karakteristika> karakteristike;

    // SalaTipDogadjaja
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE}) 
    @JoinTable(
        name = "sala_tip_dogadjaja",
        joinColumns = @JoinColumn(name = "id_sale"),
        inverseJoinColumns = @JoinColumn(name = "id_tip_dogadjaja")
    )
    private List<TipDogadjaja> tipoviDogadjaja;
}
