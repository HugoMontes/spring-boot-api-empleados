package org.benek.empleadoservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.benek.empleadoservice.model.Empleado;
import org.benek.empleadoservice.repository.EmpleadoRepository;
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

    private final EmpleadoRepository empleadoRepository;

    @Autowired
    EmpleadoIntegrationTests(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    /**
     * Configura las propiedades de la base de datos para la prueba de integración.
     * @param registry DynamicPropertyRegistry
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        empleadoRepository.deleteAll();
    }

    /**
     * Crea tres empleados y luego por medio de REST verifica que la respuesta contiene exactamente 3 empleados
     */
    @Test
    void shouldGetAllEmpleados() {
        List<Empleado> empleados = List.of(
                new Empleado("Javier Ramirez", "CTO"),
                new Empleado("Juan Rodriguez", "CEO"),
                new Empleado("Blanca Pardo", "Project Manager")
        );
        empleadoRepository.saveAll(empleados);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/empleados")
                .then()
                .statusCode(200)
                .body(".", hasSize(3));
    }

}
