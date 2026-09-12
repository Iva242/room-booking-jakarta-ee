package njt.NJT_Projekat.mapper.impl;

import njt.NJT_Projekat.dto.impl.TipDogadjajaDto;
import njt.NJT_Projekat.entity.impl.TipDogadjaja;
import njt.NJT_Projekat.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class TipDogadjajaMapper implements DtoEntityMapper<TipDogadjajaDto, TipDogadjaja> {

    @Override
    public TipDogadjajaDto toDto(TipDogadjaja e) {
        if (e == null) return null;
        
        TipDogadjajaDto d = new TipDogadjajaDto();
        d.setIdTipDogadjaja(e.getIdTipDogadjaja());
        d.setNaziv(e.getNaziv());
        return d;
    }

    @Override
    public TipDogadjaja toEntity(TipDogadjajaDto t) {
        if (t == null) return null;
        
        TipDogadjaja e = new TipDogadjaja();
        e.setIdTipDogadjaja(t.getIdTipDogadjaja());
        e.setNaziv(t.getNaziv());
        return e;
    }
}