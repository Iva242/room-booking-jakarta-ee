package njt.NJT_Projekat.exception;

// trazeni zapis ne postoji -> 404
public class EntitetNijePronadjenException extends RuntimeException {

    public EntitetNijePronadjenException(String poruka) {
        super(poruka);
    }

    public EntitetNijePronadjenException(String naziv, Long id) {
        super(naziv + " sa ID-jem " + id + " ne postoji.");
    }
}
