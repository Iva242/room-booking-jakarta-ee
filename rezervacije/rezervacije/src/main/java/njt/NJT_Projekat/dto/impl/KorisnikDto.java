package njt.NJT_Projekat.dto.impl;

import njt.NJT_Projekat.dto.Dto;

public class KorisnikDto implements Dto{

    private Long id;
    private String ime;
    private String prezime;
    private String korisnickoIme;  
    private String uloga;

    public KorisnikDto() {
    }

    
    public KorisnikDto(Long id, String korisnickoIme, String uloga, String ime, String prezime) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.korisnickoIme = korisnickoIme;
        this.uloga = uloga;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIme() { return ime; }
    public void setIme(String ime) { this.ime = ime; }

    public String getPrezime() { return prezime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }

    public String getKorisnickoIme() { return korisnickoIme; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }

    public String getUloga() { return uloga; }
    public void setUloga(String uloga) { this.uloga = uloga; }


}