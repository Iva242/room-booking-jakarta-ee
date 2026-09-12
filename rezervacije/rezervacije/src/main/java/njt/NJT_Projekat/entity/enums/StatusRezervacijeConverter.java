package njt.NJT_Projekat.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// upisuje status u bazu u obliku u kom je i do sada stajao, pa postojeci
// redovi ostaju upotrebljivi
@Converter
public class StatusRezervacijeConverter implements AttributeConverter<StatusRezervacije, String> {

    @Override
    public String convertToDatabaseColumn(StatusRezervacije status) {
        return status == null ? null : status.getVrednost();
    }

    @Override
    public StatusRezervacije convertToEntityAttribute(String vrednost) {
        return vrednost == null ? null : StatusRezervacije.odVrednosti(vrednost);
    }
}
