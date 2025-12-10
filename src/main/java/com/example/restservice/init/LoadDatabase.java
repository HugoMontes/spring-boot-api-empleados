package com.example.restservice.init;

import com.example.restservice.entity.Empleado;
import com.example.restservice.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDb(EmpleadoRepository repository) {
        // Devolver el resultado de la funcion lambda
        return args -> {
            Empleado emp1 = new Empleado("Pedro Gomez", "CTO");
            Empleado emp2 = new Empleado("Juan Lopez", "Trainner");
            Empleado emp3 = new Empleado("Maria Mendez", "PM");

            // Guardar en la base de datos
            log.info("Carga inicial {}", repository.save(emp1));
            log.info("Carga inicial {}", repository.save(emp2));
            log.info("Carga inicial {}", repository.save(emp3));
        };
    }
}
