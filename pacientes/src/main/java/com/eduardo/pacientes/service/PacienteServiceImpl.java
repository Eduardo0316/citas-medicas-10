package com.eduardo.pacientes.service;

import com.eduardo.commons.dto.pacientes.PacienteRequest;
import com.eduardo.commons.dto.pacientes.PacienteResponse;
import com.eduardo.commons.enums.EstadoRegistro;
import com.eduardo.commons.exceptions.DatoInvalidoException;
import com.eduardo.commons.exceptions.RecursoNoEncontradoException;
import com.eduardo.pacientes.entity.Paciente;
import com.eduardo.pacientes.mapper.PacienteMapper;
import com.eduardo.pacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService{
    private final PacienteMapper pacienteMapper;
    private final PacienteRepository pacienteRepository;

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        log.info("Buscando paciente sin estado con id {}", id);
        return pacienteMapper.entidadAResponse(pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Paciente sin estado no encontrado con id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(pacienteMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        Paciente paciente = obtenerPacienteActivoPorId(id);
        log.info("Paciente con id {} obtenido exitosamente", id);
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        log.info("Registrando nuevo paciente {}", request.nombre());

        validarDatosUnicos(request);
        Paciente paciente = pacienteMapper.requestAEntidad(request);

        pacienteRepository.saveAndFlush(paciente);
        log.info("Paciente {} registrado exitosamente", request.nombre());

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        Paciente paciente = obtenerPacienteActivoPorId(id);
        log.info("Actalizando paciente con id {}", id);

        validarCambiosUnicos(request, id);
        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion());
        pacienteRepository.saveAndFlush(paciente);

        log.info("Paciente con id {} actualizado exitosamente", id);
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = obtenerPacienteActivoPorId(id);
        log.info("Eliminando paciente con id {}", id);

        paciente.eliminar();
        pacienteRepository.saveAndFlush(paciente);

        log.info("Paciente con id {} eliminado", id);
    }

    private Paciente obtenerPacienteActivoPorId(Long id){
        log.info("Obteniendo paciente activo con id {}", id);

        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Paciente activo no encontrado con id: " + id));
    }

    private void validarDatosUnicos(PacienteRequest request){
        log.info("Validando email unico");
        if(pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(request.email(), EstadoRegistro.ACTIVO))
            throw new DatoInvalidoException("Ya existe un paciente activo con este email");

        log.info("Validando telefono unico");
        if(pacienteRepository.existsByTelefonoAndEstadoRegistro(request.telefono(), EstadoRegistro.ACTIVO))
            throw new DatoInvalidoException("Ya existe un paciente activo con este telefono");
    }

    private void validarCambiosUnicos(PacienteRequest request, Long id){
        log.info("Validando cambio de email unico");
        if(pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(request.email(), EstadoRegistro.ACTIVO, id))
            throw new DatoInvalidoException("Ya existe un paciente activo con este email");

        log.info("Validando cambio de telefono unico");
        if(pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(request.telefono(), EstadoRegistro.ACTIVO, id))
            throw new DatoInvalidoException("Ya existe un paciente activo con este telefono");
    }
}
