package njt.NJT_Projekat.dto.impl;

import njt.NJT_Projekat.dto.Dto;

public class KarakteristikaDto implements Dto{
    private Long idKarakteristike;
    private String naziv;

    public KarakteristikaDto() {
    }

    public KarakteristikaDto(Long idKarakteristike, String naziv) {
        this.idKarakteristike = idKarakteristike;
        this.naziv = naziv;
    }

    public Long getIdKarakteristike() { return idKarakteristike; }
    public void setIdKarakteristike(Long idKarakteristike) { this.idKarakteristike = idKarakteristike; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }
}