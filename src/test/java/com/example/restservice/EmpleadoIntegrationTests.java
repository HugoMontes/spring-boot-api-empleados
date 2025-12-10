package com.example.restservice;

import com.example.restservice.entity.Empleado;
import com.example.restservice.repository.EmpleadoRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

/**
 * Prueba de integracion para la entidad Empleado utilizando TestContainers.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmpleadoIntegrationTests {

    // Hacer la inyeccion de repository por constructor
    private final EmpleadoRepository empleadoRepository;

    @Autowired
    EmpleadoIntegrationTests(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    // Mediante Inyeccion de Dependencias obtenemos el puerto para
    // acceder al contenedor
    @LocalServerPort
    private Integer port;

    // Generar un contenedor con postgres a partir de la imagen postgres:16-alpine
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    // Antes de ejecutar las pruebas iniciamos postgres
    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    // Despues de ejecutar las pruebas apagamos el contenedor
    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    // Añadir un properties efimero (Solo para estas pruebas)
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Obtenemos los datos de conexion de la clase testcontainer
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // Añadimos BeforeEach para indicarle hacer algo antes de ejecutar
    // cada una de las pruebas/test
    @BeforeEach
    void setUp() {
        // Le indicamos que para todas las pruebas de debe usar
        // una unica URL base
        RestAssured.baseURI = "http://localhost:" + port;
        // Eliminamos toda la informacion de la tabla empleado
        empleadoRepository.deleteAll();
    }

    // Añadimos una prueba para obtener todos los empleados
    // Crea tres empleados y luego por medio de REST verifica que la respuesta contiene exactamente 3 empleados
    @Test
    void shouldGetAllEmpleados() {
        // Simulamos un guardado de varios empleados
        List<Empleado> empleados = List.of(
                new Empleado("Javier Ramirez", "CTO"),
                new Empleado("Juan Rodriguez", "CEO"),
                new Empleado("Blanca Pardo", "Project Manager")
        );
        // Guardamos una lista
        empleadoRepository.saveAll(empleados);
        // Usamos REST Assured para pruebas de API Rest que escribe
        // pruebas estido BDD (Behavior Driven Development).
        given() // DADO que...
                .contentType(ContentType.JSON)// el contenido es JSON
                .when() // CUANDO
                .get("/empleados")// hago GET a /empleados
                .then() // ENTONCES
                .statusCode(200) // espero status 200
                .body(".", hasSize(3)); // y el body tiene 3 elementos
    }

}
