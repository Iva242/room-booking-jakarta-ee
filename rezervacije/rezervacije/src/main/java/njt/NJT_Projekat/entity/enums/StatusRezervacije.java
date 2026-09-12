package njt.NJT_Projekat.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusRezervacije {

    NA_CEKANJU("na_cekanju"),
    POTVRDJENA("potvrdjena"),
    U_TOKU("u_toku"),
    ZAVRSENA("zavrsena"),
    OTKAZANA("otkazana");

    // vrednost koja stoji u bazi i koju ocekuje frontend
    private final String vrednost;

    StatusRezervacije(String vrednost) {
        this.vrednost = vrednost;
    }

    @JsonValue
    public String getVrednost() {
        return vrednost;
    }

    @JsonCreator
    public static StatusRezervacije odVrednosti(String vrednost) {
        if (vrednost == null) {
            return null;
        }
        for (StatusRezervacije status : values()) {
            if (status.vrednost.equalsIgnoreCase(vrednost)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Nepoznat status rezervacije: " + vrednost);
    }
}
