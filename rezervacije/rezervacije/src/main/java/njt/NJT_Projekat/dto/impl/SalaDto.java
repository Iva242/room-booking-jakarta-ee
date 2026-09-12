package njt.NJT_Projekat.dto.impl;

import java.util.List;
import njt.NJT_Projekat.dto.Dto;

public class SalaDto implements Dto{
    private Long idSale;
    private String naziv;
    private Integer kapacitet;
    private String lokacija;
    private String opis;
    private Double latitude;
    private Double longitude;
    private String slika;
    private List<KarakteristikaDto> karakteristike;
    private List<TipDogadjajaDto> tipoviDogadjaja;

    public SalaDto() {
    }

    public SalaDto(Long idSale, String naziv, Integer kapacitet, String lokacija, String opis, Double latitude, Double longitude, String slike, List<KarakteristikaDto> karakteristike, List<TipDogadjajaDto> tipoviDogadjaja) {
        this.idSale = idSale;
        this.naziv = naziv;
        this.kapacitet = kapacitet;
        this.lokacija = lokacija;
        this.opis = opis;
        this.latitude = latitude;
        this.longitude = longitude;
        this.slika = slike;
        this.karakteristike = karakteristike;
        this.tipoviDogadjaja = tipoviDogadjaja;
    }

    // Getteri i Setteri
    public Long getIdSale() { return idSale; }
    public void setIdSale(Long idSale) { this.idSale = idSale; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public Integer getKapacitet() { return kapacitet; }
    public void setKapacitet(Integer kapacitet) { this.kapacitet = kapacitet; }

    public String getLokacija() { return lokacija; }
    public void setLokacija(String lokacija) { this.lokacija = lokacija; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getSlika() { return slika; }
    public void setSlika(String slika) { this.slika = slika; }

    public List<KarakteristikaDto> getKarakteristike() { return karakteristike; }
    public void setKarakteristike(List<KarakteristikaDto> karakteristike) { this.karakteristike = karakteristike; }

    public List<TipDogadjajaDto> getTipoviDogadjaja() { return tipoviDogadjaja; }
    public void setTipoviDogadjaja(List<TipDogadjajaDto> tipoviDogadjaja) { this.tipoviDogadjaja = tipoviDogadjaja; }
}