package njt.NJT_Projekat.entity.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njt.NJT_Projekat.entity.MyEntity;

@Entity
@Table(name = "rezervacija")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rezervacija extends MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRezervacije;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pocetak;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime kraj;
    private String status;

    // veze sa ostalim tabelama (FK)
    // rezervacija se odnosi na jednu i samo jednu salu
    @ManyToOne
    @JoinColumn(name = "id_sale")
    private Sala sala;

    // rezervacija je vezana za jedan tip dogadjaja
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tip_dogadjaja")
    private TipDogadjaja tipDogadjaja;

    // rezervaciju kreira jedan i samo jedan korisnik
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_korisnika")
    @JsonIgnoreProperties("rezervacije")
    private Korisnik korisnik;
}
