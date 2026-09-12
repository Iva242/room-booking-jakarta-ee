package njt.NJT_Projekat.service;

import jakarta.transaction.Transactional;
import njt.NJT_Projekat.dto.impl.RezervacijaDto;
import njt.NJT_Projekat.entity.impl.Rezervacija;
import njt.NJT_Projekat.entity.impl.Sala;
import njt.NJT_Projekat.entity.impl.TipDogadjaja;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.repository.impl.RezervacijaRepository;
import njt.NJT_Projekat.repository.impl.SalaRepository;
import njt.NJT_Projekat.repository.impl.TipDogadjajaRepository;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import njt.NJT_Projekat.mapper.impl.RezervacijaMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RezervacijaService {

    private final RezervacijaRepository rezervacijaRepository;
    private final SalaRepository salaRepository;
    private final TipDogadjajaRepository tipDogadjajaRepository;
    private final KorisnikRepository korisnikRepository;
    private final RezervacijaMapper rezervacijaMapper; 

    @Autowired
    public RezervacijaService(RezervacijaRepository rezervacijaRepository,
            SalaRepository salaRepository,
            TipDogadjajaRepository tipDogadjajaRepository,
            KorisnikRepository korisnikRepository,
            RezervacijaMapper rezervacijaMapper) {
        this.rezervacijaRepository = rezervacijaRepository;
        this.salaRepository = salaRepository;
        this.tipDogadjajaRepository = tipDogadjajaRepository;
        this.korisnikRepository = korisnikRepository;
        this.rezervacijaMapper = rezervacijaMapper;
    }

    @Transactional
    public RezervacijaDto kreirajRezervaciju(RezervacijaDto dto) throws Exception {
        // proveravamo da li podaci postoje
        if (dto.getKorisnik() == null || dto.getTipDogadjaja() == null) {
            throw new Exception("Korisnik ili Tip Dogadjaja nisu prosledjeni.");
        }

        // povlacenje entiteta iz baze (navigacija kroz DTO klasu)
        Sala sala = salaRepository.findById(dto.getIdSale());
        if (sala == null) {
            throw new Exception("Sala nije pronađena");
        }

        TipDogadjaja tip = tipDogadjajaRepository.findById(dto.getTipDogadjaja().getIdTipDogadjaja());
        if (tip == null) {
            throw new Exception("Tip događaja nije pronađen");
        }

        Korisnik korisnik = korisnikRepository.findById(dto.getKorisnik().getId());
        if (korisnik == null) {
            throw new Exception("Korisnik nije pronađen");
        }

        // proveravamo preklapanje termina
        if (rezervacijaRepository.existsOverlapping(dto.getIdSale(), dto.getPocetak(), dto.getKraj())) {
            throw new Exception("Sala je zauzeta u tom terminu.");
        }

        // kreiramo rezervaciju
        Rezervacija rez = new Rezervacija();
        rez.setPocetak(dto.getPocetak());
        rez.setKraj(dto.getKraj());
        rez.setStatus(dto.getStatus() != null ? dto.getStatus() : "na_cekanju");

        // vezivanje entiteta koji smo nasli iz baze
        rez.setSala(sala);
        rez.setTipDogadjaja(tip);
        rez.setKorisnik(korisnik);

       
        Rezervacija sacuvana = rezervacijaRepository.saveAndReturn(rez);

   
        return rezervacijaMapper.toDto(sacuvana);
    }

    public List<RezervacijaDto> rezervacije() {
        return rezervacijaRepository.findAll().stream()
                .map(rezervacijaMapper::toDto)
                .collect(Collectors.toList());
    }


   
    public List<RezervacijaDto> vratiRezervacijeKorisnika(String korisnickoIme) {
   
        Korisnik korisnik = korisnikRepository.findByKorisnickoIme(korisnickoIme).orElse(null);

        if (korisnik == null) {
            return java.util.Collections.emptyList();
        }

        return rezervacijaRepository.findByKorisnikIdKorisnika(korisnik.getIdKorisnika()).stream()
                .map(rezervacijaMapper::toDto)
                .collect(Collectors.toList());
    }

    public void otkaziRezervaciju(Long id, String korisnickoIme) throws Exception {
        Rezervacija rezervacija = rezervacijaRepository.findById(id);

        if (rezervacija == null) {
            throw new Exception("Rezervacija nije pronađena.");
        }

        // trazimo korisnika koji salje zahtev
        Korisnik currentUser = korisnikRepository.findByKorisnickoIme(korisnickoIme)
                .orElseThrow(() -> new Exception("Korisnik nije nađen."));

        // provera vlasnistva
        boolean isVlasnik = rezervacija.getKorisnik().getKorisnickoIme().equals(korisnickoIme);

        
        // koristimo "ROLE_ADMIN".equals(...) jer je bezbednije ako je currentUser.getUloga() null
        boolean isAdmin = "ROLE_ADMIN".equals(currentUser.getUloga());

        // dozvoljeno brisanje ako je vlanik rezervacije ili admin
        if (!isVlasnik && !isAdmin) {
            throw new Exception("Nemate dozvolu da otkažete tuđu rezervaciju!");
        }

        rezervacija.setStatus("otkazana");
        rezervacijaRepository.save(rezervacija);
    }

    public void potvrdiRezervaciju(Long id) throws Exception {
        Rezervacija rezervacija = rezervacijaRepository.findById(id);

        // proveravamo da li uopste postoji rezervacija
        if (rezervacija == null) {
            throw new Exception("Rezervacija sa ID-jem " + id + " nije pronađena.");
        }

        rezervacija.setStatus("potvrdjena");

        rezervacijaRepository.save(rezervacija);
    }

}
