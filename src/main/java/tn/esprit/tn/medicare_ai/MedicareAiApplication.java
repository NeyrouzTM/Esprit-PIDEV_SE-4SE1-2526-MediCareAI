package tn.esprit.tn.medicare_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // ← Ajoute cette annotation
public class MedicareAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicareAiApplication.class, args);
    }

}
