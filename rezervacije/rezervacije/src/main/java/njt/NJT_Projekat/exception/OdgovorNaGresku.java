package njt.NJT_Projekat.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

// oblik u kom se sve greske vracaju klijentu
public class OdgovorNaGresku {

    private int status;
    private String poruka;
    private String putanja;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime vreme;

    public OdgovorNaGresku() {
    }

    public OdgovorNaGresku(int status, String poruka, String putanja) {
        this.status = status;
        this.poruka = poruka;
        this.putanja = putanja;
        this.vreme = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    public String getPutanja() {
        return putanja;
    }

    public void setPutanja(String putanja) {
        this.putanja = putanja;
    }

    public LocalDateTime getVreme() {
        return vreme;
    }

    public void setVreme(LocalDateTime vreme) {
        this.vreme = vreme;
    }
}
