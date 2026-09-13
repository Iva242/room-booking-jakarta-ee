package njt.NJT_Projekat.entity.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
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
@Table(name = "poruka")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Poruka extends MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPoruke;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_konverzacije", nullable = false)
    private Konverzacija konverzacija;

    // ko je napisao poruku — korisnik ili administrator
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_autora", nullable = false)
    private Korisnik autor;

    @Column(columnDefinition = "TEXT")
    private String tekst;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime vreme;

    // procitao je onaj kome poruka nije upucena kao autoru
    private boolean procitana;
}
