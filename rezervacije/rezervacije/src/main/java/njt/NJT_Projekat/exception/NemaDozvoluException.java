package njt.NJT_Projekat.exception;

// korisnik je prijavljen, ali nad ovim zapisom nema prava -> 403
public class NemaDozvoluException extends RuntimeException {

    public NemaDozvoluException(String poruka) {
        super(poruka);
    }
}
