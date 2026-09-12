package njt.NJT_Projekat.entity.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njt.NJT_Projekat.entity.MyEntity;


@Entity
@Table(name = "korisnik")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Korisnik extends MyEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKorisnika;

    private String ime;
    private String prezime;
    private String korisnickoIme;
    private String lozinka;
    private String email;
    private String uloga;
    private boolean aktivan;

    // jedan korisnik moze imati vise rezervacija
    @OneToMany(mappedBy = "korisnik", fetch = FetchType.EAGER) 
    @JsonIgnoreProperties("korisnik")
    private List<Rezervacija> rezervacije;
}
