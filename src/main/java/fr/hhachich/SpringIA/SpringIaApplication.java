package fr.hhachich.SpringIA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class SpringIaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringIaApplication.class, args);
	}

}
