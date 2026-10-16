package com.example.demo.controller;

import jakarta.validation.Valid;
import com.example.demo.dto.*;
import com.example.demo.service.PrincipalService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Capa WEB: recibe las peticiones HTTP, valida el JSON y delega al service.
 *
 * SEGURIDAD (lo único nuevo respecto al ejercicio base): cada endpoint se protege con @PreAuthorize.
 *   - Sin token o token inválido ........ 401 Unauthorized (lo responde JwtAuthenticationEntryPoint)
 *   - Token válido pero rol incorrecto .. 403 Forbidden
 *   - hasRole('X') busca la autoridad ROLE_X. JwtUserDetailsService ya antepone "ROLE_", así que
 *     aquí se escribe el nombre SIN prefijo y EXACTAMENTE igual al de la tabla roles (ADMIN, USUARIO).
 */
@RestController
@RequestMapping("/api/principal")
public class PrincipalController {
    private final PrincipalService e1_principalService;

    // Spring inyecta el service por constructor
    public PrincipalController(PrincipalService e1_principalService) {
        this.e1_principalService = e1_principalService;
    }

    // CREATE -> POST /api/principal/registrar  | responde 201 Created | roles: ADMIN u USUARIO
    // @Valid activa las validaciones del DTO; @RequestBody convierte el JSON a objeto Java
    @PreAuthorize("hasAnyRole('ADMIN','USUARIO')")
    @PostMapping("/registrar")
    public ResponseEntity<PrincipalResponse> registrar(@Valid @RequestBody PrincipalRequest e1_request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(e1_principalService.registrar(e1_request));
    }

    // READ -> GET /api/principal/listar | roles: ADMIN u USUARIO
    @PreAuthorize("hasAnyRole('ADMIN','USUARIO')")
    @GetMapping("/listar")
    public List<PrincipalResponse> listar() {
        return e1_principalService.listar();
    }

    // UPDATE -> PUT /api/principal/actualizar/{id} | solo ADMIN
    // @PathVariable toma el id de la URL; el body trae los nuevos datos
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/actualizar/{id}")
    public PrincipalResponse actualizar(@PathVariable("id") Long e1_codigoPrincipal,
                                          @Valid @RequestBody PrincipalRequest e1_request) {
        return e1_principalService.actualizar(e1_codigoPrincipal, e1_request);
    }

    // DELETE -> DELETE /api/principal/eliminar/{id} | responde 204 No Content (sin body) | solo ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") Long e1_codigoPrincipal) {
        e1_principalService.eliminar(e1_codigoPrincipal);
    }

    // REPORTE -> GET /api/principal/reporteAgrupado | solo ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reporteAgrupado")
    public List<ReporteResponse> reporteAgrupado() {
        return e1_principalService.reporteAgrupado();
    }
}
