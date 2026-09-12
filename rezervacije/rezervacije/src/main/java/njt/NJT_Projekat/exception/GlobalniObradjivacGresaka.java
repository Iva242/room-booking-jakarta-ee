package njt.NJT_Projekat.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalniObradjivacGresaka {

    private static final Logger logger = LoggerFactory.getLogger(GlobalniObradjivacGresaka.class);

    private ResponseEntity<OdgovorNaGresku> odgovor(HttpStatus status, String poruka,
            HttpServletRequest zahtev) {
        return ResponseEntity.status(status)
                .body(new OdgovorNaGresku(status.value(), poruka, zahtev.getRequestURI()));
    }

    @ExceptionHandler(EntitetNijePronadjenException.class)
    public ResponseEntity<OdgovorNaGresku> nijePronadjen(EntitetNijePronadjenException greska,
            HttpServletRequest zahtev) {
        return odgovor(HttpStatus.NOT_FOUND, greska.getMessage(), zahtev);
    }

    @ExceptionHandler(NevalidanZahtevException.class)
    public ResponseEntity<OdgovorNaGresku> nevalidanZahtev(NevalidanZahtevException greska,
            HttpServletRequest zahtev) {
        return odgovor(HttpStatus.BAD_REQUEST, greska.getMessage(), zahtev);
    }

    @ExceptionHandler(TerminZauzetException.class)
    public ResponseEntity<OdgovorNaGresku> terminZauzet(TerminZauzetException greska,
            HttpServletRequest zahtev) {
        return odgovor(HttpStatus.CONFLICT, greska.getMessage(), zahtev);
    }

    @ExceptionHandler(NemaDozvoluException.class)
    public ResponseEntity<OdgovorNaGresku> nemaDozvolu(NemaDozvoluException greska,
            HttpServletRequest zahtev) {
        return odgovor(HttpStatus.FORBIDDEN, greska.getMessage(), zahtev);
    }

    // greske iz @Valid anotacija na DTO-ovima
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<OdgovorNaGresku> neispravnaPolja(MethodArgumentNotValidException greska,
            HttpServletRequest zahtev) {
        String poruka = greska.getBindingResult().getFieldErrors().stream()
                .map(polje -> polje.getField() + ": " + polje.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Podaci u zahtevu nisu ispravni.");
        return odgovor(HttpStatus.BAD_REQUEST, poruka, zahtev);
    }

    // neispravan tip u putanji, npr. /api/sale/abc umesto broja
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<OdgovorNaGresku> neispravanTip(MethodArgumentTypeMismatchException greska,
            HttpServletRequest zahtev) {
        return odgovor(HttpStatus.BAD_REQUEST,
                "Vrednost \"" + greska.getValue() + "\" nije ispravna za parametar "
                        + greska.getName() + ".", zahtev);
    }

    // telo zahteva nije ispravan JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<OdgovorNaGresku> neispravnoTelo(HttpMessageNotReadableException greska,
            HttpServletRequest zahtev) {
        return odgovor(HttpStatus.BAD_REQUEST, "Telo zahteva nije ispravno.", zahtev);
    }

    // sve sto nije predvidjeno: korisnik dobija opstu poruku, a pravi uzrok
    // zavrsava u logu umesto da nestane
    @ExceptionHandler(Exception.class)
    public ResponseEntity<OdgovorNaGresku> neocekivanaGreska(Exception greska,
            HttpServletRequest zahtev) {
        logger.error("Neocekivana greska na {}", zahtev.getRequestURI(), greska);
        return odgovor(HttpStatus.INTERNAL_SERVER_ERROR,
                "Došlo je do greške na serveru.", zahtev);
    }
}
