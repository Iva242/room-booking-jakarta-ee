package njt.NJT_Projekat.repository;

import java.util.List;
import njt.NJT_Projekat.entity.impl.Sala;
import njt.NJT_Projekat.repository.impl.SalaRepository;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SalaRepositoryTests {

    @Autowired
    private SalaRepository salaRepository;

    private Sala sacuvajSalu(String naziv, String lokacija, Integer kapacitet) {
        Sala sala = new Sala();
        sala.setNaziv(naziv);
        sala.setLokacija(lokacija);
        sala.setKapacitet(kapacitet);
        salaRepository.save(sala);
        return sala;
    }

    @BeforeEach
    public void pripremiPodatke() {
        sacuvajSalu("Plavi salon", "Knez Mihailova 35, Beograd", 34);
        sacuvajSalu("Amfiteatar", "Bulevar kralja Aleksandra 73, Beograd", 300);
        sacuvajSalu("IT Lab 404", "Trg slobode 1, Novi Sad", 40);
    }

    @Test
    public void saveTest() {
        Sala sala = sacuvajSalu("Mala sala", "Nis", 10);
        assertNotNull(sala);
        assertNotNull(sala.getIdSale());
    }

    @Test
    public void findByIdTest() throws Exception {
        Sala sacuvana = sacuvajSalu("Svecana dvorana", "Kragujevac", 120);

        Sala pronadjena = salaRepository.findById(sacuvana.getIdSale());

        assertNotNull(pronadjena);
        Assertions.assertEquals("Svecana dvorana", pronadjena.getNaziv());
    }

    @Test
    public void findByIdNepostojeciFailureTest() {
        Assertions.assertThrows(Exception.class, () -> {
            salaRepository.findById(999999L);
        });
    }

    @Test
    public void findAllTest() {
        List<Sala> sale = salaRepository.findAll();
        Assertions.assertTrue(sale.size() >= 3);
    }

    @Test
    public void pretragaPoNazivuTest() {
        List<Sala> rezultat = salaRepository
                .findByNazivContainingIgnoreCaseOrLokacijaContainingIgnoreCase("plavi", "plavi");

        Assertions.assertEquals(1, rezultat.size());
        Assertions.assertEquals("Plavi salon", rezultat.get(0).getNaziv());
    }

    @Test
    public void pretragaPoLokacijiTest() {
        List<Sala> rezultat = salaRepository
                .findByNazivContainingIgnoreCaseOrLokacijaContainingIgnoreCase("Novi Sad", "Novi Sad");

        Assertions.assertEquals(1, rezultat.size());
        Assertions.assertEquals("IT Lab 404", rezultat.get(0).getNaziv());
    }

    @Test
    public void pretragaNeRazlikujeVelicinuSlovaTest() {
        List<Sala> malim = salaRepository
                .findByNazivContainingIgnoreCaseOrLokacijaContainingIgnoreCase("amfiteatar", "amfiteatar");
        List<Sala> velikim = salaRepository
                .findByNazivContainingIgnoreCaseOrLokacijaContainingIgnoreCase("AMFITEATAR", "AMFITEATAR");

        Assertions.assertEquals(1, malim.size());
        Assertions.assertEquals(malim.size(), velikim.size());
    }

    @Test
    public void pretragaBezRezultataTest() {
        List<Sala> rezultat = salaRepository
                .findByNazivContainingIgnoreCaseOrLokacijaContainingIgnoreCase("nepostoji", "nepostoji");

        Assertions.assertTrue(rezultat.isEmpty());
    }

    @Test
    public void deleteByIdTest() {
        Sala sala = sacuvajSalu("Za brisanje", "Beograd", 5);
        Long id = sala.getIdSale();

        salaRepository.deleteById(id);

        Assertions.assertThrows(Exception.class, () -> {
            salaRepository.findById(id);
        });
    }
}
