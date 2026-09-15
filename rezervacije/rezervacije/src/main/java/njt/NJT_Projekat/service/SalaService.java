package njt.NJT_Projekat.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import njt.NJT_Projekat.dto.impl.SalaDto;
import njt.NJT_Projekat.entity.impl.Sala;
import njt.NJT_Projekat.repository.impl.SalaRepository;
import njt.NJT_Projekat.mapper.impl.SalaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import njt.NJT_Projekat.entity.impl.Karakteristika;
import njt.NJT_Projekat.entity.impl.TipDogadjaja;
import java.util.List;
import java.util.stream.Collectors;
import njt.NJT_Projekat.entity.impl.Rezervacija;
import njt.NJT_Projekat.exception.EntitetNijePronadjenException;
import njt.NJT_Projekat.exception.KonfliktException;
import njt.NJT_Projekat.entity.enums.StatusRezervacije;
import njt.NJT_Projekat.mapper.impl.KarakteristikaMapper;
import njt.NJT_Projekat.mapper.impl.TipDogadjajaMapper;
import njt.NJT_Projekat.repository.impl.KarakteristikaRepository;
import njt.NJT_Projekat.repository.impl.TipDogadjajaRepository;
import njt.NJT_Projekat.repository.impl.RezervacijaRepository;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;
    private final KarakteristikaMapper karakteristikaMapper;
    private final TipDogadjajaMapper tipDogadjajaMapper;
    private final TipDogadjajaRepository tipRepository;
    private final KarakteristikaRepository karakteristikaRepository;
    private final RezervacijaRepository rezervacijaRepository; 

    @Autowired
    public SalaService(SalaRepository salaRepository, SalaMapper salaMapper,
            KarakteristikaMapper karakteristikaMapper,
            TipDogadjajaMapper tipDogadjajaMapper,
            TipDogadjajaRepository tipRepository,
            KarakteristikaRepository karakteristikaRepository,
            RezervacijaRepository rezervacijaRepository) { 
        this.salaRepository = salaRepository;
        this.salaMapper = salaMapper;
        this.karakteristikaMapper = karakteristikaMapper;
        this.tipDogadjajaMapper = tipDogadjajaMapper;
        this.tipRepository = tipRepository;
        this.karakteristikaRepository = karakteristikaRepository;
        this.rezervacijaRepository = rezervacijaRepository; 
    }

    public List<SalaDto> vratiSve() {
        return salaRepository.findAll().stream()
                .map(salaMapper::toDto)
                .collect(Collectors.toList());
    }

    public SalaDto vratiPoId(Long id) {

        Sala sala = salaRepository.findById(id);
        return salaMapper.toDto(sala);
    }


    @Transactional
    public SalaDto kreiraj(SalaDto salaDto) {
        // mapiramo osnovne podatke
        Sala sala = salaMapper.toEntity(salaDto);

        // ocistiti liste jer mapper mozda nije kreirao "managed" entitete
        sala.setKarakteristike(new ArrayList<>());
        sala.setTipoviDogadjaja(new ArrayList<>());

        // uciitaj i dodaj karakteristike
        if (salaDto.getKarakteristike() != null) {
            for (var kDto : salaDto.getKarakteristike()) {
                Karakteristika k = karakteristikaRepository.findById(kDto.getIdKarakteristike());
                if (k == null) {
                    throw new EntitetNijePronadjenException("Karakteristika", kDto.getIdKarakteristike());
                }
                sala.getKarakteristike().add(k);
            }
        }

        // ucitaj i dodaj tipove dogadjaja
        if (salaDto.getTipoviDogadjaja() != null) {
            for (var tDto : salaDto.getTipoviDogadjaja()) {
                TipDogadjaja t = tipRepository.findById(tDto.getIdTipDogadjaja());
                if (t == null) {
                    throw new EntitetNijePronadjenException("Tip događaja", tDto.getIdTipDogadjaja());
                }
                sala.getTipoviDogadjaja().add(t);
            }
        }


        salaRepository.save(sala);


        return salaMapper.toDto(sala);
    }

    public List<SalaDto> pretraziSale(String tekst) {
        return salaRepository.findByNazivContainingIgnoreCaseOrLokacijaContainingIgnoreCase(tekst, tekst).stream()
                .map(salaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SalaDto izmeni(Long id, SalaDto salaDto) {
        Sala postojecaSala = salaRepository.findById(id);
        if (postojecaSala == null) {
            throw new EntitetNijePronadjenException("Sala", id);
        }

        // azuriraj osnovna polja
        postojecaSala.setNaziv(salaDto.getNaziv());
        postojecaSala.setKapacitet(salaDto.getKapacitet());
        postojecaSala.setLokacija(salaDto.getLokacija());
        postojecaSala.setOpis(salaDto.getOpis());
        postojecaSala.setLatitude(salaDto.getLatitude());
        postojecaSala.setLongitude(salaDto.getLongitude());
        postojecaSala.setSlike(salaDto.getSlika());

        // azuriraj karakteristike
        if (salaDto.getKarakteristike() != null) {
            List<Karakteristika> praveKarakteristike = new java.util.ArrayList<>();
            for (var kDto : salaDto.getKarakteristike()) {
                // vraca objekat direktno, ne Optional
                Karakteristika k = karakteristikaRepository.findById(kDto.getIdKarakteristike());

                // da li postoji
                if (k == null) {
                    throw new EntitetNijePronadjenException("Karakteristika", kDto.getIdKarakteristike());
                }
                praveKarakteristike.add(k);
            }
            postojecaSala.setKarakteristike(praveKarakteristike);
        }

        // azuriraj tipove
        if (salaDto.getTipoviDogadjaja() != null) {
            List<TipDogadjaja> praviTipovi = new java.util.ArrayList<>();
            for (var tDto : salaDto.getTipoviDogadjaja()) {
                // Provera da li postoji
                TipDogadjaja t = tipRepository.findById(tDto.getIdTipDogadjaja());

                if (t == null) {
                    throw new EntitetNijePronadjenException("Tip događaja", tDto.getIdTipDogadjaja());
                }
                praviTipovi.add(t);
            }
            postojecaSala.setTipoviDogadjaja(praviTipovi);
        }

        // cuvanje
        salaRepository.save(postojecaSala);

        return salaMapper.toDto(postojecaSala);
    }


    
    @Transactional
    public void obrisi(Long id) {
        Sala sala = salaRepository.findById(id); 

        // provera rezervacije
        List<Rezervacija> rezervacije = rezervacijaRepository.findBySalaId(id);
        for (Rezervacija r : rezervacije) {
            if (r.getStatus() != StatusRezervacije.ZAVRSENA && r.getStatus() != StatusRezervacije.OTKAZANA) {
                throw new KonfliktException("Sala se ne može obrisati jer postoje aktivne rezervacije.");
            }
        }
        rezervacijaRepository.deleteAll(rezervacije);

        // cistimo veze pre brisanja
        sala.setKarakteristike(new ArrayList<>());
        sala.setTipoviDogadjaja(new ArrayList<>());
        salaRepository.save(sala); // cuva se sala sa praznim listama, brise se sve iz pivot tabele

     
        salaRepository.deleteById(id);
    }
}
