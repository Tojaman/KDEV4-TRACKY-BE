package kernel360.trackybatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "kernel360")
@EnableJpaRepositories(basePackages = "kernel360")
@EntityScan(basePackages = "kernel360")
public class TrackyBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackyBatchApplication.class, args);
    }
}
