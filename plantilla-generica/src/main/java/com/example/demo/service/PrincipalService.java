package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Capa de NEGOCIO: aquí va la lógica (validar que existan las FK, armar entidades, convertir a DTO).
 * El controller solo recibe/devuelve; el repository solo accede a datos.
 * @Transactional: cada método corre en una transacción (si hay excepción se revierte todo).
 * Prefijo e2_ en las variables = convención de nombres pedida por el examen (cámbiala por la que pidan).
 */
@Service
@Transactional
public class PrincipalService {
    private final PrincipalRepository e2_principalRepository;
    private final PadreARepository e2_padreARepository;
    private final PadreBRepository e2_padreBRepository;

    // Inyección de dependencias por constructor (Spring pasa los repositorios solo)
    public PrincipalService(PrincipalRepository e2_principalRepository,
                              PadreARepository e2_padreARepository,
                              PadreBRepository e2_padreBRepository) {
        this.e2_principalRepository = e2_principalRepository;
        this.e2_padreARepository = e2_padreARepository;
        this.e2_padreBRepository = e2_padreBRepository;
    }

    // CREATE (POST): crea una Principal nueva con los datos del request y la guarda (INSERT)
    public PrincipalResponse registrar(PrincipalRequest e2_request) {
        Principal e2_principal = new Principal();
        copiarDatos(e2_principal, e2_request);
        return convertir(e2_principalRepository.save(e2_principal));
    }

    // READ (GET): devuelve todas. readOnly = true porque solo lee (más eficiente)
    @Transactional(readOnly = true)
    public List<PrincipalResponse> listar() {
        return e2_principalRepository.findAll().stream().map(this::convertir).toList();
    }

    // UPDATE (PUT): busca por id (si no existe -> 404), pisa los datos y guarda (UPDATE)
    public PrincipalResponse actualizar(Long e2_codigoPrincipal, PrincipalRequest e2_request) {
        Principal e2_principal = e2_principalRepository.findById(e2_codigoPrincipal)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el registro principal con id " + e2_codigoPrincipal));
        copiarDatos(e2_principal, e2_request);
        return convertir(e2_principalRepository.save(e2_principal));
    }

    // DELETE: verifica que exista (si no -> 404) y borra
    public void eliminar(Long e2_codigoPrincipal) {
        if (!e2_principalRepository.existsById(e2_codigoPrincipal)) {
            throw new RecursoNoEncontradoException("No existe el registro principal con id " + e2_codigoPrincipal);
        }
        e2_principalRepository.deleteById(e2_codigoPrincipal);
    }

    // REPORTE: convierte cada fila de la proyección en un ReporteResponse
    @Transactional(readOnly = true)
    public List<ReporteResponse> reporteAgrupado() {
        return e2_principalRepository.obtenerReporteAgrupado().stream()
                .map(e2_resultado -> new ReporteResponse(
                        e2_resultado.getNombrePadreA(),
                        e2_resultado.getCategoriaPadreB(),
                        e2_resultado.getMontoTotal()))
                .toList();
    }

    // Método auxiliar compartido por registrar y actualizar: valida las FK y copia los campos del DTO a la entidad.
    // Si el padre A o el padre B no existen -> RecursoNoEncontradoException (404).
    private void copiarDatos(Principal e2_principal, PrincipalRequest e2_request) {
        PadreA e2_padreA = e2_padreARepository.findById(e2_request.padreAId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el padre A con id " + e2_request.padreAId()));
        PadreB e2_padreB = e2_padreBRepository.findById(e2_request.padreBId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el padre B con id " + e2_request.padreBId()));
        e2_principal.setCantidad(e2_request.cantidad());
        e2_principal.setFecha(e2_request.fecha());
        e2_principal.setMonto(e2_request.monto());
        e2_principal.setEstado(e2_request.estado());
        e2_principal.setPadreA(e2_padreA);
        e2_principal.setPadreB(e2_padreB);
    }

    // Entidad -> DTO de salida (aplana los datos del padre A y del padre B)
    private PrincipalResponse convertir(Principal e2_principal) {
        return new PrincipalResponse(
                e2_principal.getId(), e2_principal.getCantidad(), e2_principal.getFecha(),
                e2_principal.getMonto(), e2_principal.getEstado(),
                e2_principal.getPadreA().getId(), e2_principal.getPadreA().getNombre(),
                e2_principal.getPadreB().getId(), e2_principal.getPadreB().getNombre(),
                e2_principal.getPadreB().getCategoria());
    }
}
