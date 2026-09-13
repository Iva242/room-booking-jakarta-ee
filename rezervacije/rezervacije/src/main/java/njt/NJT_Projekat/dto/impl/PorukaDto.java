package njt.NJT_Projekat.dto.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import njt.NJT_Projekat.dto.Dto;

public class PorukaDto implements Dto {

    private Long idPoruke;
    private Long idKonverzacije;
    private String tekst;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime vreme;

    private boolean procitana;
    private KorisnikDto autor;

    public PorukaDto() {
    }

    public Long getIdPoruke() { return idPoruke; }
    public void setIdPoruke(Long idPoruke) { this.idPoruke = idPoruke; }

    public Long getIdKonverzacije() { return idKonverzacije; }
    public void setIdKonverzacije(Long idKonverzacije) { this.idKonverzacije = idKonverzacije; }

    public String getTekst() { return tekst; }
    public void setTekst(String tekst) { this.tekst = tekst; }

    public LocalDateTime getVreme() { return vreme; }
    public void setVreme(LocalDateTime vreme) { this.vreme = vreme; }

    public boolean isProcitana() { return procitana; }
    public void setProcitana(boolean procitana) { this.procitana = procitana; }

    public KorisnikDto getAutor() { return autor; }
    public void setAutor(KorisnikDto autor) { this.autor = autor; }
}
