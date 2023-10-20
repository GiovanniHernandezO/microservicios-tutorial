package com.usuario.service.controlador;

import com.usuario.service.entidades.Usuario;
import com.usuario.service.modelos.Carro;
import com.usuario.service.modelos.Moto;
import com.usuario.service.servicio.UsuarioService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.getAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable("id") int id) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario> guardarUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @GetMapping("/carros/{usuarioId}")
    @CircuitBreaker(name = "carrosCB", fallbackMethod = "fallBackGetCarros")
    public ResponseEntity<List<Carro>> listarCarros(@PathVariable("usuarioId") int usuarioId) {
        Usuario usuario = usuarioService.getUsuarioById(usuarioId);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        List<Carro> carros = usuarioService.getCarros(usuarioId);
        return ResponseEntity.ok(carros);
    }

    @PostMapping("/carro/{usuarioId}")
    @CircuitBreaker(name = "carrosCB", fallbackMethod = "fallBackSaveCarro")
    public ResponseEntity<Carro> guardarCarro(@PathVariable("usuarioId") int usuarioId, @RequestBody Carro carro) {
        Carro nuevoCarro = usuarioService.saveCarro(usuarioId, carro);
        return ResponseEntity.ok(nuevoCarro);
    }

    @GetMapping("/motos/{usuarioId}")
    @CircuitBreaker(name = "motosCB", fallbackMethod = "fallBackGetMotos")
    public ResponseEntity<List<Moto>> listarMotos(@PathVariable("usuarioId") int usuarioId) {
        Usuario usuario = usuarioService.getUsuarioById(usuarioId);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        List<Moto> motos = usuarioService.getMotos(usuarioId);
        return ResponseEntity.ok(motos);
    }

    @PostMapping("/moto/{usuarioId}")
    @CircuitBreaker(name = "motosCB", fallbackMethod = "fallBackSaveMoto")
    public ResponseEntity<Moto> guardarMoto(@PathVariable("usuarioId") int usuarioId, @RequestBody Moto moto) {
        Moto nuevaMoto = usuarioService.saveMoto(usuarioId, moto);
        return ResponseEntity.ok(nuevaMoto);
    }

    @GetMapping("/todos/{usuarioId}")
    @CircuitBreaker(name = "todosCB", fallbackMethod = "fallBackGetTodos")
    public ResponseEntity<Map<String, Object>> listarTodosLosVehiculos(@PathVariable("usuarioId") int usuarioId) {
        Map<String, Object> resultado = usuarioService.getUsuarioAndVehiculos(usuarioId);
        return ResponseEntity.ok(resultado);
    }

    private ResponseEntity<List<Carro>> fallBackGetCarros(@PathVariable("usuarioId") int usuarioId, RuntimeException exception) {
        return new ResponseEntity("El usuario " + usuarioId + " tiene los carros en el taller", HttpStatus.OK);
    }

    private ResponseEntity<Carro> fallBackSaveCarro(@PathVariable("usuarioId") int usuarioId,
            @RequestBody Carro carro, RuntimeException exception) {
        return new ResponseEntity("El usuario " + usuarioId + " no tiene dinero para los carros", HttpStatus.OK);
    }
    
    private ResponseEntity<List<Moto>> fallBackGetMotos(@PathVariable("usuarioId") int usuarioId, RuntimeException exception) {
        return new ResponseEntity("El usuario " + usuarioId + " tiene las motos en el taller", HttpStatus.OK);
    }

    private ResponseEntity< Moto> fallBackSaveMoto(@PathVariable("usuarioId") int usuarioId,
            @RequestBody Moto moto, RuntimeException exception) {
        return new ResponseEntity("El usuario " + usuarioId + " no tiene dinero para las motos", HttpStatus.OK);
    }
    
    private ResponseEntity<List<Carro>> fallBackGetTodos(@PathVariable("usuarioId") int usuarioId, RuntimeException exception) {
        return new ResponseEntity("El usuario " + usuarioId + " tiene los vehiculos en el taller", HttpStatus.OK);
    }
}
