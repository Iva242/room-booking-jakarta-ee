package njt.NJT_Projekat.dto.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import njt.NJT_Projekat.dto.Dto;

public class RezervacijaDto implements Dto{

    private Long idRezervacije;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pocetak;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime kraj;
    private String status;
    private Long idSale;
    private TipDogadjajaDto tipDogadjaja;
    private KorisnikDto korisnik;

    public RezervacijaDto() {
    }

    public RezervacijaDto(Long idRezervacije, LocalDateTime pocetak, LocalDateTime kraj, String status, Long idSale, TipDogadjajaDto tipDogadjaja, KorisnikDto korisnik) {
        this.idRezervacije = idRezervacije;
        this.pocetak = pocetak;
        this.kraj = kraj;
        this.status = status;
        this.idSale = idSale;
        this.tipDogadjaja = tipDogadjaja;
        this.korisnik = korisnik;
    }

    // Getteri i Setteri
    public Long getIdRezervacije() {
        return idRezervacije;
    }

    public void setIdRezervacije(Long idRezervacije) {
        this.idRezervacije = idRezervacije;
    }

    public LocalDateTime getPocetak() {
        return pocetak;
    }

    public void setPocetak(LocalDateTime pocetak) {
        this.pocetak = pocetak;
    }

    public LocalDateTime getKraj() {
        return kraj;
    }

    public void setKraj(LocalDateTime kraj) {
        this.kraj = kraj;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getIdSale() {
        return idSale;
    }

    public void setIdSale(Long idSale) {
        this.idSale = idSale;
    }

    public TipDogadjajaDto getTipDogadjaja() {
        return tipDogadjaja;
    }

    public void setTipDogadjaja(TipDogadjajaDto tipDogadjaja) {
        this.tipDogadjaja = tipDogadjaja;
    }

    public KorisnikDto getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(KorisnikDto korisnik) {
        this.korisnik = korisnik;
    }
}
