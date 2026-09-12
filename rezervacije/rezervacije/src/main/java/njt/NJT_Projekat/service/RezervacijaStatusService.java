package njt.NJT_Projekat.service;

import jakarta.transaction.Transactional;
import njt.NJT_Projekat.entity.impl.Rezervacija;
import njt.NJT_Projekat.entity.enums.StatusRezervacije;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import njt.NJT_Projekat.repository.impl.RezervacijaRepository;

@Service
public class RezervacijaStatusService {

    @Autowired
    private RezervacijaRepository rezervacijaRepository;

    // Ovaj metod se pokreće na svakih 60 sekundi (60000 ms)
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void azurirajStatuseRezervacija() {
        System.err.println("!!! PROVERA STATUS SE POKRENULA !!!"); 
        // Dohvati sve rezervacije koje nisu otkazane ni zavrsene
        List<Rezervacija> rezervacije = rezervacijaRepository.findAll(); // 
        LocalDateTime sada = LocalDateTime.now();

        for (Rezervacija r : rezervacije) {
            // Ako je već ZAVRSENA ili OTKAZANA, preskoci
            if (r.getStatus() == StatusRezervacije.ZAVRSENA || r.getStatus() == StatusRezervacije.OTKAZANA) {
                continue;
            }

            // Logika: U TOKU
            if (sada.isAfter(r.getPocetak()) && sada.isBefore(r.getKraj())) {
                if (r.getStatus() == StatusRezervacije.POTVRDJENA) {
                    r.setStatus(StatusRezervacije.U_TOKU);
                    rezervacijaRepository.save(r);
                }
            }
            // Logika: ZAVRSENA
            else if (sada.isAfter(r.getKraj())) {
                r.setStatus(StatusRezervacije.ZAVRSENA);
                rezervacijaRepository.save(r);
            }
        }
    }
}