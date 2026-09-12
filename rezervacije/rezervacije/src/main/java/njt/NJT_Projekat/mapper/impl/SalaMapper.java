package njt.NJT_Projekat.mapper.impl;

import java.util.stream.Collectors;
import njt.NJT_Projekat.dto.impl.SalaDto;
import njt.NJT_Projekat.entity.impl.Sala;
import njt.NJT_Projekat.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class SalaMapper implements DtoEntityMapper<SalaDto, Sala> {

    private final KarakteristikaMapper karakteristikaMapper;
    private final TipDogadjajaMapper tipDogadjajaMapper;


    public SalaMapper(KarakteristikaMapper karakteristikaMapper, TipDogadjajaMapper tipDogadjajaMapper) {
        this.karakteristikaMapper = karakteristikaMapper;
        this.tipDogadjajaMapper = tipDogadjajaMapper;
    }

    @Override
    public SalaDto toDto(Sala e) {
        if (e == null) return null;

        SalaDto d = new SalaDto();
        d.setIdSale(e.getIdSale());
        d.setNaziv(e.getNaziv());
        d.setKapacitet(e.getKapacitet());
        d.setLokacija(e.getLokacija());
        d.setOpis(e.getOpis());
        d.setLatitude(e.getLatitude());
        d.setLongitude(e.getLongitude());
        d.setSlika(e.getSlike());

        // Mapiranje liste karakteristika iz Entity u DTO
        if (e.getKarakteristike() != null) {
            d.setKarakteristike(e.getKarakteristike().stream()
                    .map(karakteristikaMapper::toDto)
                    .collect(Collectors.toList()));
        }

        // Mapiranje liste tipova dogadjaja iz Entity u DTO
        if (e.getTipoviDogadjaja() != null) {
            d.setTipoviDogadjaja(e.getTipoviDogadjaja().stream()
                    .map(tipDogadjajaMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return d;
    }

    @Override
    public Sala toEntity(SalaDto t) {
        if (t == null) return null;

        Sala e = new Sala();
        e.setIdSale(t.getIdSale());
        e.setNaziv(t.getNaziv());
        e.setKapacitet(t.getKapacitet());
        e.setLokacija(t.getLokacija());
        e.setOpis(t.getOpis());
        e.setLatitude(t.getLatitude());
        e.setLongitude(t.getLongitude());
        e.setSlike(t.getSlika());

        // Mapiranje liste karakteristika iz DTO u Entity
        if (t.getKarakteristike() != null) {
            e.setKarakteristike(t.getKarakteristike().stream()
                    .map(karakteristikaMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        // Mapiranje liste tipova dogadjaja iz DTO u Entity
        if (t.getTipoviDogadjaja() != null) {
            e.setTipoviDogadjaja(t.getTipoviDogadjaja().stream()
                    .map(tipDogadjajaMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        return e;
    }
}