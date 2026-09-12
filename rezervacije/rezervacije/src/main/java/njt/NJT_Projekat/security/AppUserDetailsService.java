package njt.NJT_Projekat.security;

import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final KorisnikRepository korisnikRepository;


    public AppUserDetailsService(KorisnikRepository korisnikRepository) {
        this.korisnikRepository = korisnikRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Korisnik korisnik = korisnikRepository.findByKorisnickoIme(username)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik sa korisničkim imenom " + username + " nije pronađen."));

   
        String ulogaSaPrefiksom = korisnik.getUloga().toUpperCase();
        SimpleGrantedAuthority autoritet = new SimpleGrantedAuthority(ulogaSaPrefiksom);

        return new User(
                korisnik.getKorisnickoIme(),
                korisnik.getLozinka(),
                Collections.singletonList(autoritet)
        );
    }
}
