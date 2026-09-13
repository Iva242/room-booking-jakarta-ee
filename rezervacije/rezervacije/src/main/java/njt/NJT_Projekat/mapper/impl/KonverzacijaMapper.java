package njt.NJT_Projekat.mapper.impl;

import njt.NJT_Projekat.dto.impl.KonverzacijaDto;
import njt.NJT_Projekat.entity.impl.Konverzacija;
import njt.NJT_Projekat.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class KonverzacijaMapper implements DtoEntityMapper<KonverzacijaDto, Konverzacija> {

    private final KorisnikMapper korisnikMapper;

    public KonverzacijaMapper(KorisnikMapper korisnikMapper) {
        this.korisnikMapper = korisnikMapper;
    }

    @Override
    public KonverzacijaDto toDto(Konverzacija e) {
        if (e == null) return null;

        KonverzacijaDto d = new KonverzacijaDto();
        d.setIdKonverzacije(e.getIdKonverzacije());
        d.setPoslednjaAktivnost(e.getPoslednjaAktivnost());

        if (e.getKorisnik() != null) {
            d.setKorisnik(korisnikMapper.toDto(e.getKorisnik()));
        }
        if (e.getSala() != null) {
            d.setIdSale(e.getSala().getIdSale());
            d.setNazivSale(e.getSala().getNaziv());
        }
        return d;
    }

    @Override
    public Konverzacija toEntity(KonverzacijaDto t) {
        if (t == null) return null;

        Konverzacija e = new Konverzacija();
        e.setIdKonverzacije(t.getIdKonverzacije());
        e.setPoslednjaAktivnost(t.getPoslednjaAktivnost());
        return e;
    }
}
