package njt.NJT_Projekat.service;

import njt.NJT_Projekat.dto.impl.TipDogadjajaDto;
import njt.NJT_Projekat.entity.impl.TipDogadjaja;
import njt.NJT_Projekat.repository.impl.TipDogadjajaRepository;
import njt.NJT_Projekat.mapper.impl.TipDogadjajaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipDogadjajaService {

    private final TipDogadjajaRepository tipDogadjajaRepository;
    private final TipDogadjajaMapper tipDogadjajaMapper;

    @Autowired
    public TipDogadjajaService(TipDogadjajaRepository tipDogadjajaRepository, TipDogadjajaMapper tipDogadjajaMapper) {
        this.tipDogadjajaRepository = tipDogadjajaRepository;
        this.tipDogadjajaMapper = tipDogadjajaMapper;
    }

    // vraca sve tipove događaja kao DTO listu
    public List<TipDogadjajaDto> vratiSve() {
        return tipDogadjajaRepository.findAll().stream()
                .map(tipDogadjajaMapper::toDto)
                .collect(Collectors.toList());
    }

    // kreiranje novog tipa događaja
    public TipDogadjajaDto kreiraj(TipDogadjajaDto tipDogadjajaDto) {
        TipDogadjaja tipDogadjaja = tipDogadjajaMapper.toEntity(tipDogadjajaDto);

       
        tipDogadjajaRepository.save(tipDogadjaja);

      
        return tipDogadjajaMapper.toDto(tipDogadjaja);
    }
}
