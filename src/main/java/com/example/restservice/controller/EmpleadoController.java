package com.example.restservice.controller;

import com.example.restservice.entity.Empleado;
import com.example.restservice.repository.EmpleadoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class EmpleadoController {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoController(EmpleadoRepository empleadoRepository){
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping("/holamundo")
    public ResponseEntity<String> holaMundo(){
        return ResponseEntity.ok("Hola Mundo!");
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<Empleado>> listAll(){
        return  ResponseEntity.ok(empleadoRepository.findAll());
    }

    // Buscar empleado
    @GetMapping("/empleados/{id}")
    public ResponseEntity<Empleado> findById(@PathVariable Long id){
        // Usamos Optional para gestional nulos
        Optional<Empleado> empleado = empleadoRepository.findById(id);
        if(empleado.isPresent()){
            return ResponseEntity.ok(empleado.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    // Guardar empleado
    @PostMapping("/empleados")
    public ResponseEntity<Empleado> create(@RequestBody Empleado empleado, UriComponentsBuilder ucb){
        Empleado empleadoGuardado = empleadoRepository.save(empleado);
        URI uriEmpleado = ucb
                .path("empleados/{id}")
                .buildAndExpand(empleadoGuardado.getId())
                .toUri();

        return ResponseEntity.created(uriEmpleado).build();
    }

    // Actualiza un empleado existente
    @PutMapping("/empleados/{id}")
    public ResponseEntity<?> update(@RequestBody Empleado empleadoActualizar,
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

    // Elimina un empleado existente.
    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long id) {
        empleadoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
