package Termproject.Termproject2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FrunApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrunApplication.class, args);
	}

}
