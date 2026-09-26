package njt.NJT_Projekat.entity.impl;

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
@Table(name = "konverzacija")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Konverzacija extends MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKonverzacije;

    // razgovor uvek pripada korisniku; administrator vidi sve razgovore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_korisnika", nullable = false)
    private Korisnik korisnik;

    // sala o kojoj se pita
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sale")
    private Sala sala;

    private LocalDateTime kreirana;

    private LocalDateTime poslednjaAktivnost;
}
