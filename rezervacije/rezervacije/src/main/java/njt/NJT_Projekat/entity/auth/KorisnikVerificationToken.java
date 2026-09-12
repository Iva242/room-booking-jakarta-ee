package njt.NJT_Projekat.entity.auth;

import jakarta.persistence.*;
import njt.NJT_Projekat.entity.impl.Korisnik; 

@Entity
@Table(name="korisnik_verification_tokens")
public class KorisnikVerificationToken extends AbstractVerificationToken {

    // veza 1 na 1 sa korisnikom
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="korisnik_id", nullable=false, unique=true)
    private Korisnik korisnik;
    
    public KorisnikVerificationToken() {}
    
    
    public static KorisnikVerificationToken of(Korisnik k, long ttlSeconds){
        KorisnikVerificationToken t = new KorisnikVerificationToken();
        t.initialize(ttlSeconds); 
        t.korisnik = k;
        return t;
    }

    
    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }
}