package njt.NJT_Projekat.exception;

// zahtev je ispravan, ali se kosi sa postojecim stanjem -> 409
public class TerminZauzetException extends RuntimeException {

    public TerminZauzetException(String poruka) {
        super(poruka);
    }
}
