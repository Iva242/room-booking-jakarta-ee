package njt.NJT_Projekat.service;

import jakarta.transaction.Transactional;
import njt.NJT_Projekat.dto.impl.RezervacijaDto;
import njt.NJT_Projekat.entity.impl.Rezervacija;
import njt.NJT_Projekat.entity.impl.Sala;
import njt.NJT_Projekat.entity.impl.TipDogadjaja;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.exception.EntitetNijePronadjenException;
import njt.NJT_Projekat.exception.NevalidanZahtevException;
import njt.NJT_Projekat.exception.TerminZauzetException;
import njt.NJT_Projekat.exception.NemaDozvoluException;
import njt.NJT_Projekat.entity.enums.StatusRezervacije;
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
    public RezervacijaDto kreirajRezervaciju(RezervacijaDto dto) {
        // proveravamo da li podaci postoje
        if (dto.getKorisnik() == null || dto.getTipDogadjaja() == null) {
            throw new NevalidanZahtevException("Korisnik ili tip događaja nisu prosleđeni.");
        }

        // povlacenje entiteta iz baze (navigacija kroz DTO klasu)
        // zakljucavamo salu da dva istovremena zahteva ne bi oba prosla proveru
        Sala sala = salaRepository.findByIdForUpdate(dto.getIdSale());
        if (sala == null) {
            throw new EntitetNijePronadjenException("Sala nije pronađena.");
        }

        TipDogadjaja tip = tipDogadjajaRepository.findById(dto.getTipDogadjaja().getIdTipDogadjaja());
        if (tip == null) {
            throw new EntitetNijePronadjenException("Tip događaja nije pronađen.");
        }

        Korisnik korisnik = korisnikRepository.findById(dto.getKorisnik().getId());
        if (korisnik == null) {
            throw new EntitetNijePronadjenException("Korisnik nije pronađen.");
        }

        // proveravamo preklapanje termina
        if (rezervacijaRepository.existsOverlapping(dto.getIdSale(), dto.getPocetak(), dto.getKraj())) {
            throw new TerminZauzetException("Sala je zauzeta u tom terminu.");
        }

        // kreiramo rezervaciju
        Rezervacija rez = new Rezervacija();
        rez.setPocetak(dto.getPocetak());
        rez.setKraj(dto.getKraj());
        // pocetni status postavlja server, a ne klijent: svaka nova rezervacija
        // ceka na potvrdu administratora
        rez.setStatus(StatusRezervacije.NA_CEKANJU);

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

    @Transactional
    public void otkaziRezervaciju(Long id, String korisnickoIme) {
        Rezervacija rezervacija = rezervacijaRepository.findById(id);

        if (rezervacija == null) {
            throw new EntitetNijePronadjenException("Rezervacija nije pronađena.");
        }

        // trazimo korisnika koji salje zahtev
        Korisnik currentUser = korisnikRepository.findByKorisnickoIme(korisnickoIme)
                .orElseThrow(() -> new EntitetNijePronadjenException("Korisnik nije pronađen."));

        // provera vlasnistva
        boolean isVlasnik = rezervacija.getKorisnik().getKorisnickoIme().equals(korisnickoIme);

        
        // koristimo "ROLE_ADMIN".equals(...) jer je bezbednije ako je currentUser.getUloga() null
        boolean isAdmin = "ROLE_ADMIN".equals(currentUser.getUloga());

        // dozvoljeno brisanje ako je vlanik rezervacije ili admin
        if (!isVlasnik && !isAdmin) {
            throw new NemaDozvoluException("Nemate dozvolu da otkažete tuđu rezervaciju!");
        }

        rezervacija.setStatus(StatusRezervacije.OTKAZANA);
        rezervacijaRepository.save(rezervacija);
    }

    @Transactional
    public void potvrdiRezervaciju(Long id) {
        Rezervacija rezervacija = rezervacijaRepository.findById(id);

        // proveravamo da li uopste postoji rezervacija
        if (rezervacija == null) {
            throw new EntitetNijePronadjenException("Rezervacija", id);
        }

        rezervacija.setStatus(StatusRezervacije.POTVRDJENA);

        rezervacijaRepository.save(rezervacija);
    }

}
