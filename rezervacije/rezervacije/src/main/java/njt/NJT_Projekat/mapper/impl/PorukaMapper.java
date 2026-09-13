package njt.NJT_Projekat.mapper.impl;

import njt.NJT_Projekat.dto.impl.PorukaDto;
import njt.NJT_Projekat.entity.impl.Poruka;
import njt.NJT_Projekat.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class PorukaMapper implements DtoEntityMapper<PorukaDto, Poruka> {

    private final KorisnikMapper korisnikMapper;

    public PorukaMapper(KorisnikMapper korisnikMapper) {
        this.korisnikMapper = korisnikMapper;
    }

    @Override
    public PorukaDto toDto(Poruka e) {
        if (e == null) return null;

        PorukaDto d = new PorukaDto();
        d.setIdPoruke(e.getIdPoruke());
        d.setTekst(e.getTekst());
        d.setVreme(e.getVreme());
        d.setProcitana(e.isProcitana());

        if (e.getKonverzacija() != null) {
            d.setIdKonverzacije(e.getKonverzacija().getIdKonverzacije());
        }
        if (e.getAutor() != null) {
            d.setAutor(korisnikMapper.toDto(e.getAutor()));
        }
        return d;
    }

    @Override
    public Poruka toEntity(PorukaDto t) {
        if (t == null) return null;

        Poruka e = new Poruka();
        e.setIdPoruke(t.getIdPoruke());
        e.setTekst(t.getTekst());
        e.setVreme(t.getVreme());
        e.setProcitana(t.isProcitana());
        return e;
    }
}
