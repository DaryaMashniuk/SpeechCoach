package by.mashnyuk.orchestratorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableJpaAuditing
public class OrchestratorserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrchestratorserviceApplication.class, args);
	}

}
