package org.benek.empleadoservice.init;

import org.benek.empleadoservice.model.Empleado;
import org.benek.empleadoservice.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    /**
     * Metodo para inicializar la base de datos con informacion de prueba.
     *
     * Utiliza la interfaz CommandLineRunner para ejecutar el metodo initDatabase al
     * inicio de la aplicacion.
     *
     * Si no hay empleados en la base de datos, crea 3 empleados de prueba y los guarda
     * en la base de datos. Muestra un mensaje en el log con la informacion de cada
     * empleado guardado.
     */
    @Bean
    CommandLineRunner initDatabase(EmpleadoRepository empleadoRepository) {
        return args -> {
            if (empleadoRepository.count() == 0) {
                Empleado javier = new Empleado("Javier Ramirez", "CTO");
                Empleado juan = new Empleado("Juan Rodriguez", "CEO");
                Empleado blanca = new Empleado("Blanca Pardo", "Project Manager");

                for (Empleado empleado : Arrays.asList(javier, juan, blanca)) {
                    log.info("Carga inicial: {}", empleadoRepository.save(empleado));
                }
            }
        };
    }

}
