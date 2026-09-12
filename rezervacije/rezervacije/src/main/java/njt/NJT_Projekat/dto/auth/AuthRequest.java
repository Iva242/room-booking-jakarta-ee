package njt.NJT_Projekat.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {
    @NotBlank(message = "Korisničko ime je obavezno.")
    private String korisnickoIme;
    
    @NotBlank(message = "Lozinka je obavezna.")
    private String lozinka;

    public AuthRequest() {
    }

    public AuthRequest(String korisnickoIme, String lozinka) {
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
    }

    public String getKorisnickoIme() { return korisnickoIme; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }
    public String getLozinka() { return lozinka; }
    public void setLozinka(String lozinka) { this.lozinka = lozinka; }
}