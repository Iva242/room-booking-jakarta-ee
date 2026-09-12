package njt.NJT_Projekat.exception;

// podaci u zahtevu nisu ispravni -> 400
public class NevalidanZahtevException extends RuntimeException {

    public NevalidanZahtevException(String poruka) {
        super(poruka);
    }
}
