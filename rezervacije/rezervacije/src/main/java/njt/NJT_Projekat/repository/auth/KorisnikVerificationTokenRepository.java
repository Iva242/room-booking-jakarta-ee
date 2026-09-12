package njt.NJT_Projekat.repository.auth;

import njt.NJT_Projekat.entity.auth.KorisnikVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KorisnikVerificationTokenRepository extends JpaRepository<KorisnikVerificationToken, String> {

    // ovo nam sluzi da u servisu nadjemo token kada korisnik klikne na link iz mejla
    KorisnikVerificationToken findByToken(String token);
    
}