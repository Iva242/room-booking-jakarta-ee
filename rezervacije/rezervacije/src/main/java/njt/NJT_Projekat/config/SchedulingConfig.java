package njt.NJT_Projekat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

// zakazani poslovi se ne pokrecu pod test profilom, da ne bi menjali
// podatke u toku izvrsavanja testova
@Configuration
@EnableScheduling
@Profile("!test")
public class SchedulingConfig {
}
