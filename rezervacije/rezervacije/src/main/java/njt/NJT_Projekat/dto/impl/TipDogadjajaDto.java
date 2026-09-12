package njt.NJT_Projekat.dto.impl;

import njt.NJT_Projekat.dto.Dto;

public class TipDogadjajaDto implements Dto{
    private Long idTipDogadjaja;
    private String naziv;

    public TipDogadjajaDto() {
    }

    public TipDogadjajaDto(Long idTipDogadjaja, String naziv) {
        this.idTipDogadjaja = idTipDogadjaja;
        this.naziv = naziv;
    }

    public Long getIdTipDogadjaja() { return idTipDogadjaja; }
    public void setIdTipDogadjaja(Long idTipDogadjaja) { this.idTipDogadjaja = idTipDogadjaja; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }
}