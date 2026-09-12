package njt.NJT_Projekat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RezervacijeApplication {

	public static void main(String[] args) {
		SpringApplication.run(RezervacijeApplication.class, args);
	}

}
