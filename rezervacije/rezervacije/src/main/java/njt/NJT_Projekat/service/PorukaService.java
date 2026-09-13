package njt.NJT_Projekat.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import njt.NJT_Projekat.dto.impl.KonverzacijaDto;
import njt.NJT_Projekat.dto.impl.NovaPorukaDto;
import njt.NJT_Projekat.dto.impl.PorukaDto;
import njt.NJT_Projekat.entity.impl.Konverzacija;
import njt.NJT_Projekat.entity.impl.Korisnik;
import njt.NJT_Projekat.entity.impl.Poruka;
import njt.NJT_Projekat.entity.impl.Sala;
import njt.NJT_Projekat.exception.EntitetNijePronadjenException;
import njt.NJT_Projekat.exception.NemaDozvoluException;
import njt.NJT_Projekat.exception.NevalidanZahtevException;
import njt.NJT_Projekat.mapper.impl.KonverzacijaMapper;
import njt.NJT_Projekat.mapper.impl.PorukaMapper;
import njt.NJT_Projekat.repository.impl.KonverzacijaRepository;
import njt.NJT_Projekat.repository.impl.KorisnikRepository;
import njt.NJT_Projekat.repository.impl.PorukaRepository;
import njt.NJT_Projekat.repository.impl.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PorukaService {

    private static final String ULOGA_ADMIN = "ROLE_ADMIN";
    private static final int DUZINA_PREGLEDA = 60;

    private final KonverzacijaRepository konverzacijaRepository;
    private final PorukaRepository porukaRepository;
    private final KorisnikRepository korisnikRepository;
    private final SalaRepository salaRepository;
    private final KonverzacijaMapper konverzacijaMapper;
    private final PorukaMapper porukaMapper;

    @Autowired
    public PorukaService(KonverzacijaRepository konverzacijaRepository,
            PorukaRepository porukaRepository,
            KorisnikRepository korisnikRepository,
            SalaRepository salaRepository,
            KonverzacijaMapper konverzacijaMapper,
            PorukaMapper porukaMapper) {
        this.konverzacijaRepository = konverzacijaRepository;
        this.porukaRepository = porukaRepository;
        this.korisnikRepository = korisnikRepository;
        this.salaRepository = salaRepository;
        this.konverzacijaMapper = konverzacijaMapper;
        this.porukaMapper = porukaMapper;
    }

    private Korisnik nadjiKorisnika(String korisnickoIme) {
        return korisnikRepository.findByKorisnickoIme(korisnickoIme)
                .orElseThrow(() -> new EntitetNijePronadjenException("Korisnik nije pronađen."));
    }

    private boolean jeAdmin(Korisnik korisnik) {
        return ULOGA_ADMIN.equals(korisnik.getUloga());
    }

    // razgovor sme da vidi njegov vlasnik i administrator
    private void proveriPristup(Konverzacija konverzacija, Korisnik korisnik) {
        boolean vlasnik = konverzacija.getKorisnik().getIdKorisnika()
                .equals(korisnik.getIdKorisnika());
        if (!vlasnik && !jeAdmin(korisnik)) {
            throw new NemaDozvoluException("Nemate pristup ovom razgovoru.");
        }
    }

    @Transactional
    public KonverzacijaDto zapocniKonverzaciju(NovaPorukaDto zahtev, String korisnickoIme) {
        Korisnik korisnik = nadjiKorisnika(korisnickoIme);
        if (jeAdmin(korisnik)) {
            throw new NemaDozvoluException("Razgovor može započeti samo korisnik.");
        }

        Konverzacija konverzacija = new Konverzacija();
        konverzacija.setKorisnik(korisnik);
        konverzacija.setKreirana(LocalDateTime.now());
        konverzacija.setPoslednjaAktivnost(LocalDateTime.now());

        if (zahtev.getIdSale() != null) {
            Sala sala = salaRepository.findById(zahtev.getIdSale());
            konverzacija.setSala(sala);
        }

        Konverzacija sacuvana = konverzacijaRepository.saveAndReturn(konverzacija);
        dodajPoruku(sacuvana, korisnik, zahtev.getTekst());

        return konverzacijaMapper.toDto(sacuvana);
    }

    @Transactional
    public PorukaDto posaljiPoruku(Long idKonverzacije, NovaPorukaDto zahtev, String korisnickoIme) {
        Korisnik autor = nadjiKorisnika(korisnickoIme);
        Konverzacija konverzacija = konverzacijaRepository.findById(idKonverzacije);
        proveriPristup(konverzacija, autor);

        Poruka poruka = dodajPoruku(konverzacija, autor, zahtev.getTekst());
        return porukaMapper.toDto(poruka);
    }

    private Poruka dodajPoruku(Konverzacija konverzacija, Korisnik autor, String tekst) {
        if (tekst == null || tekst.isBlank()) {
            throw new NevalidanZahtevException("Poruka ne sme biti prazna.");
        }

        Poruka poruka = new Poruka();
        poruka.setKonverzacija(konverzacija);
        poruka.setAutor(autor);
        poruka.setTekst(tekst.trim());
        poruka.setVreme(LocalDateTime.now());
        poruka.setProcitana(false);
        Poruka sacuvana = porukaRepository.saveAndReturn(poruka);

        konverzacija.setPoslednjaAktivnost(poruka.getVreme());
        konverzacijaRepository.save(konverzacija);

        return sacuvana;
    }

    public List<KonverzacijaDto> konverzacije(String korisnickoIme) {
        Korisnik korisnik = nadjiKorisnika(korisnickoIme);

        List<Konverzacija> konverzacije = jeAdmin(korisnik)
                ? konverzacijaRepository.findAll()
                : konverzacijaRepository.findByKorisnik(korisnik.getIdKorisnika());

        return konverzacije.stream()
                .map(k -> dopuniPregledom(k, korisnik))
                .collect(Collectors.toList());
    }

    private KonverzacijaDto dopuniPregledom(Konverzacija konverzacija, Korisnik citalac) {
        KonverzacijaDto dto = konverzacijaMapper.toDto(konverzacija);

        List<Poruka> poruke = porukaRepository.findByKonverzacija(konverzacija.getIdKonverzacije());
        if (!poruke.isEmpty()) {
            String tekst = poruke.get(poruke.size() - 1).getTekst();
            dto.setPregledPoslednjePoruke(tekst.length() > DUZINA_PREGLEDA
                    ? tekst.substring(0, DUZINA_PREGLEDA) + "…"
                    : tekst);
        }

        dto.setBrojNeprocitanih(porukaRepository.brojNeprocitanihUKonverzaciji(
                konverzacija.getIdKonverzacije(), citalac.getIdKorisnika()));
        return dto;
    }

    @Transactional
    public List<PorukaDto> poruke(Long idKonverzacije, String korisnickoIme) {
        Korisnik citalac = nadjiKorisnika(korisnickoIme);
        Konverzacija konverzacija = konverzacijaRepository.findById(idKonverzacije);
        proveriPristup(konverzacija, citalac);

        // otvaranje razgovora znaci da su tudje poruke procitane
        porukaRepository.oznaciProcitane(idKonverzacije, citalac.getIdKorisnika());

        return porukaRepository.findByKonverzacija(idKonverzacije).stream()
                .map(porukaMapper::toDto)
                .collect(Collectors.toList());
    }

    public long brojNeprocitanih(String korisnickoIme) {
        Korisnik korisnik = nadjiKorisnika(korisnickoIme);
        return jeAdmin(korisnik)
                ? porukaRepository.brojNeprocitanihZaAdmina(korisnik.getIdKorisnika())
                : porukaRepository.brojNeprocitanihZaKorisnika(korisnik.getIdKorisnika());
    }
}
