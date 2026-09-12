package njt.NJT_Projekat.mapper.impl;

import njt.NJT_Projekat.dto.impl.RezervacijaDto;
import njt.NJT_Projekat.entity.impl.Rezervacija;
import njt.NJT_Projekat.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class RezervacijaMapper implements DtoEntityMapper<RezervacijaDto, Rezervacija> {

    private final TipDogadjajaMapper tipDogadjajaMapper;
    private final KorisnikMapper korisnikMapper;

    public RezervacijaMapper(TipDogadjajaMapper tipDogadjajaMapper, KorisnikMapper korisnikMapper) {
        this.tipDogadjajaMapper = tipDogadjajaMapper;
        this.korisnikMapper = korisnikMapper;
    }

    @Override
    public RezervacijaDto toDto(Rezervacija e) {
        if (e == null) return null;

        RezervacijaDto d = new RezervacijaDto();
        d.setIdRezervacije(e.getIdRezervacije());
        d.setPocetak(e.getPocetak());
        d.setKraj(e.getKraj());
        d.setStatus(e.getStatus());

        // MAPIRANJE ID-a SALE
        if (e.getSala() != null) {
            d.setIdSale(e.getSala().getIdSale());
        }

        if (e.getTipDogadjaja() != null) {
            d.setTipDogadjaja(tipDogadjajaMapper.toDto(e.getTipDogadjaja()));
        }
        if (e.getKorisnik() != null) {
            d.setKorisnik(korisnikMapper.toDto(e.getKorisnik()));
        }

        return d;
    }

    @Override
    public Rezervacija toEntity(RezervacijaDto t) {
        if (t == null) return null;

        Rezervacija e = new Rezervacija();
        e.setIdRezervacije(t.getIdRezervacije());
        e.setPocetak(t.getPocetak());
        e.setKraj(t.getKraj());
        e.setStatus(t.getStatus());

        
        if (t.getTipDogadjaja() != null) {
            e.setTipDogadjaja(tipDogadjajaMapper.toEntity(t.getTipDogadjaja()));
        }
        if (t.getKorisnik() != null) {
            e.setKorisnik(korisnikMapper.toEntity(t.getKorisnik()));
        }

        return e;
    }
}