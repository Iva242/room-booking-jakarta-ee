package njt.NJT_Projekat.dto.impl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import njt.NJT_Projekat.dto.Dto;

// telo zahteva kada korisnik zapocinje razgovor ili salje odgovor
public class NovaPorukaDto implements Dto {

    // prazno kod opsteg pitanja
    private Long idSale;

    @NotBlank(message = "Poruka ne sme biti prazna.")
    @Size(max = 2000, message = "Poruka može imati najviše 2000 karaktera.")
    private String tekst;

    public NovaPorukaDto() {
    }

    public Long getIdSale() { return idSale; }
    public void setIdSale(Long idSale) { this.idSale = idSale; }

    public String getTekst() { return tekst; }
    public void setTekst(String tekst) { this.tekst = tekst; }
}
