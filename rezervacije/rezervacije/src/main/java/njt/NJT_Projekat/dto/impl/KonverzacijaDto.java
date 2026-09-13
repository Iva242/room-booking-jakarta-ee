package njt.NJT_Projekat.dto.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import njt.NJT_Projekat.dto.Dto;

public class KonverzacijaDto implements Dto {

    private Long idKonverzacije;
    private KorisnikDto korisnik;
    private Long idSale;
    private String nazivSale;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime poslednjaAktivnost;

    private String pregledPoslednjePoruke;
    private long brojNeprocitanih;

    public KonverzacijaDto() {
    }

    public Long getIdKonverzacije() { return idKonverzacije; }
    public void setIdKonverzacije(Long idKonverzacije) { this.idKonverzacije = idKonverzacije; }

    public KorisnikDto getKorisnik() { return korisnik; }
    public void setKorisnik(KorisnikDto korisnik) { this.korisnik = korisnik; }

    public Long getIdSale() { return idSale; }
    public void setIdSale(Long idSale) { this.idSale = idSale; }

    public String getNazivSale() { return nazivSale; }
    public void setNazivSale(String nazivSale) { this.nazivSale = nazivSale; }

    public LocalDateTime getPoslednjaAktivnost() { return poslednjaAktivnost; }
    public void setPoslednjaAktivnost(LocalDateTime poslednjaAktivnost) { this.poslednjaAktivnost = poslednjaAktivnost; }

    public String getPregledPoslednjePoruke() { return pregledPoslednjePoruke; }
    public void setPregledPoslednjePoruke(String pregledPoslednjePoruke) { this.pregledPoslednjePoruke = pregledPoslednjePoruke; }

    public long getBrojNeprocitanih() { return brojNeprocitanih; }
    public void setBrojNeprocitanih(long brojNeprocitanih) { this.brojNeprocitanih = brojNeprocitanih; }
}
