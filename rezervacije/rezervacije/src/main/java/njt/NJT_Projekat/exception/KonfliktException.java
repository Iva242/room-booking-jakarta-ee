package njt.NJT_Projekat.exception;

// zahtev je ispravan, ali se kosi sa postojecim stanjem -> 409
public class KonfliktException extends RuntimeException {

    public KonfliktException(String poruka) {
        super(poruka);
    }
}
