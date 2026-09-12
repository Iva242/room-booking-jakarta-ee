package njt.NJT_Projekat.mapper.impl;

import njt.NJT_Projekat.dto.impl.KarakteristikaDto;
import njt.NJT_Projekat.entity.impl.Karakteristika;
import njt.NJT_Projekat.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class KarakteristikaMapper implements DtoEntityMapper<KarakteristikaDto, Karakteristika> {

    @Override
    public KarakteristikaDto toDto(Karakteristika e) {
        if (e == null) return null;
        
        KarakteristikaDto d = new KarakteristikaDto();
        d.setIdKarakteristike(e.getIdKarakteristike());
        d.setNaziv(e.getNaziv());
        return d;
    }

    @Override
    public Karakteristika toEntity(KarakteristikaDto t) {
        if (t == null) return null;
        
        Karakteristika e = new Karakteristika();
        e.setIdKarakteristike(t.getIdKarakteristike());
        e.setNaziv(t.getNaziv());
        return e;
    }
}