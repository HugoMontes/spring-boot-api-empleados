package org.benek.empleadoservice.controller;

import org.benek.empleadoservice.model.Empleado;
import org.benek.empleadoservice.repository.EmpleadoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para la entidad Empleado.
 *
 * Ofrece operaciones para obtener la lista de todos los empleados, buscar un empleado por su ID,
 * crear un nuevo empleado y eliminar un empleado existente.
 */
@RestController
public class EmpleadoController {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoController(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    /**
     * Obtiene la lista de todos los empleados.
     *
     * @return ResponseEntity con la lista de empleados
     */
    @GetMapping("/empleados")
    public ResponseEntity<List<Empleado>> listAll() {
        return ResponseEntity.ok(empleadoRepository.findAll());
    }

    /**
     * Busca un empleado por su ID.
     *
     * @param id el identificador del empleado a buscar
     * @return ResponseEntity con el empleado encontrado o un estado de no encontrado si no existe
     */
    @GetMapping("/empleados/{id}")
    public ResponseEntity<Empleado> findById(@PathVariable Long id) {
        Optional<Empleado> empleado = empleadoRepository.findById(id);
        return empleado.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo empleado.
     *
     * @param nuevoEmpleado el empleado a crear
     * @return ResponseEntity con la ubicacion del empleado creado
     */
    @PostMapping("/empleados")
    public ResponseEntity<Empleado> createEmpleado(@RequestBody Empleado nuevoEmpleado, UriComponentsBuilder ucb) {
        Empleado empleadoGuardado = empleadoRepository.save(nuevoEmpleado);
        URI uriEmpleado = ucb
                .path("empleados/{id}")
                .buildAndExpand(empleadoGuardado.getId())
                .toUri();

        return ResponseEntity.created(uriEmpleado).build();
    }

    /**
     * Actualiza un empleado existente.
     *
     * @param empleadoActualizar el empleado con los datos actualizados
     * @param id el ID del empleado a actualizar
     * @return ResponseEntity con el empleado actualizado o un estado de no encontrado si no existe
     */
    @PutMapping("/empleados/{id}")
    public ResponseEntity<?> updateEmpleado(@RequestBody Empleado empleadoActualizar,
                                            @PathVariable Long id) {
        return ResponseEntity.ok(empleadoRepository.findById(id)
                .map(empleado -> {
                    empleado.setNombre(empleadoActualizar.getNombre());
                    empleado.setPuesto(empleadoActualizar.getPuesto());
                    empleadoRepository.save(empleado);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> {
                    empleadoRepository.save(empleadoActualizar);
                    return ResponseEntity.ok(empleadoActualizar);
                }));
    }

    /**
     * Elimina un empleado existente.
     *
     * @param id el ID del empleado a eliminar
     * @return ResponseEntity con el estado de eliminacion del empleado
     */
    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long id) {
        empleadoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
