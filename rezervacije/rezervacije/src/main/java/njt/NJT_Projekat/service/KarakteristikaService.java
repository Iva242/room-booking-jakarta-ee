package njt.NJT_Projekat.service;

import njt.NJT_Projekat.dto.impl.KarakteristikaDto;
import njt.NJT_Projekat.entity.impl.Karakteristika;
import njt.NJT_Projekat.repository.impl.KarakteristikaRepository;
import njt.NJT_Projekat.mapper.impl.KarakteristikaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KarakteristikaService {

    private final KarakteristikaRepository karakteristikaRepository;
    private final KarakteristikaMapper karakteristikaMapper;

    @Autowired
    public KarakteristikaService(KarakteristikaRepository karakteristikaRepository, KarakteristikaMapper karakteristikaMapper) {
        this.karakteristikaRepository = karakteristikaRepository;
        this.karakteristikaMapper = karakteristikaMapper;
    }

    // vracaa sve karakteristike prebacene u DTO listu
    public List<KarakteristikaDto> vratiSve() {
        return karakteristikaRepository.findAll().stream()
                .map(karakteristikaMapper::toDto)
                .collect(Collectors.toList());
    }

    
    public KarakteristikaDto kreirajKarakteristiku(KarakteristikaDto karakteristikaDto) {
        Karakteristika karakteristika = karakteristikaMapper.toEntity(karakteristikaDto);

        karakteristikaRepository.save(karakteristika);

        // Maperu se prosledi sam objekat 'karakteristika' koji je sada sacuvan
        return karakteristikaMapper.toDto(karakteristika);
    }
}
