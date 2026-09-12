package njt.NJT_Projekat.mapper.impl;

import njt.NJT_Projekat.dto.impl.KorisnikDto;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class KorisnikMapper implements DtoEntityMapper<KorisnikDto, Korisnik> {

    @Override
    public KorisnikDto toDto(Korisnik e) {
        if (e == null) return null;
        
        KorisnikDto d = new KorisnikDto();
        // Iz entiteta (idKorisnika) prepisujemo u DTO (id)
        d.setId(e.getIdKorisnika());                 
        d.setIme(e.getIme());                         
        d.setPrezime(e.getPrezime());                 
        d.setKorisnickoIme(e.getKorisnickoIme());
        d.setUloga(e.getUloga());
        return d;
    }

    @Override
    public Korisnik toEntity(KorisnikDto t) {
        if (t == null) return null;
        
        Korisnik e = new Korisnik();
        // Iz DTO-a (id) prepisujemo nazad u entitet (idKorisnika)
        e.setIdKorisnika(t.getId());                 
        e.setIme(t.getIme());                         
        e.setPrezime(t.getPrezime());                 
        e.setKorisnickoIme(t.getKorisnickoIme());
        if (t.getUloga() != null) {
            e.setUloga(t.getUloga());
        }
        return e;
    }
}