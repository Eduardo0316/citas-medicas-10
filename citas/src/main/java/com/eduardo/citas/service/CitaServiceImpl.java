package com.eduardo.citas.service;

import com.eduardo.citas.dto.CitaRequest;
import com.eduardo.citas.dto.CitaResponse;
import com.eduardo.citas.entity.Cita;
import com.eduardo.citas.enums.EstadoCita;
import com.eduardo.citas.mapper.CitaMapper;
import com.eduardo.citas.repository.CitaRepository;
import com.eduardo.commons.clients.MedicoClient;
import com.eduardo.commons.clients.PacienteClient;
import com.eduardo.commons.dto.medicos.MedicoResponse;
import com.eduardo.commons.dto.pacientes.PacienteResponse;
import com.eduardo.commons.enums.DisponibilidadMedico;
import com.eduardo.commons.enums.EstadoRegistro;
import com.eduardo.commons.exceptions.ConflictoException;
import com.eduardo.commons.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CitaServiceImpl implements CitaService{
    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    @Override
    public void pacienteConCitaActiva(Long idPaciente) {
        //Ocupo un metodo en el repository pa ver si el paciente tiene una cita con estado activa
        log.info("Validando si paciente no tiene una cita activa");
        //Ocupo buscar si existe (al menos una cita) del paciente que este pendiente
        //osea buscar por id paciente
        // si el paciente es activo (🫦)
        // y si tiene POR LO MENOS una cita con estado diferente a finalizada o cancelada
        if(citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente,
                EstadoRegistro.ACTIVO,
                //Osea solo debe devolver un paciente con estadocita Pendiente, Confirmada o En curso
                List.of(EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO)
        ))
            throw new ConflictoException("El paciente tiene una cita activa");
    }

    @Override
    public void medicoConCitaActiva(Long idMedico) {
        //Ahora a aplicar el mismo metodo que antes
        log.info("Validando si medico no tiene una cita activa");
        if(citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                List.of(EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO)
        ))
            throw new ConflictoException("El médico tiene una cita activa");
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        Cita cita = obtenerCitaOExepcion(idCita);

        log.info("Actualizando cita con id {}",idCita);
        cita.actualizarEstadoCita(
                EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));

        citaRepository.saveAndFlush(cita);
        cambiarDisponiblidadMedicoSegunEstadoCita(
                cita.getIdMedico(), cita.getEstadoCita());

        log.info("Cita actualizada con id: {}",idCita);
    }

    @Override
    public List<CitaResponse> listar() {
        log.info("Listando todas las citas activas");
        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        obtenerPaciente(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())))
                .toList();
    }

    @Override
    public CitaResponse obtenerPorId(Long id) {
        Cita cita = obtenerCitaOExepcion(id);
        return citaMapper.entidadAResponse(
                cita,
                obtenerPaciente(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        log.info("Registrando una nueva cita");
        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        pacienteConCitaActiva(request.idPaciente());
        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        validarMedicoActivoDisponible(medico);

        Cita cita = citaMapper.requestAEntidad(request);
        citaRepository.saveAndFlush(cita);

        log.info("Cita registrada exitosamente");

        cambiarDisponiblidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        Cita cita = obtenerCitaOExepcion(id);
        if(cita.getEstadoCita() == EstadoCita.EN_CURSO)
            throw new ConflictoException("La cita esta en curso y no puede actualizarse");

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        log.info("Actualizando cita con id {}",id);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );

        log.info("Cita actualizada con id: {}",id);

        citaRepository.saveAndFlush(cita);
        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }

    @Override
    public void eliminar(Long id) {
        Cita cita = obtenerCitaOExepcion(id);
        log.info("Eliminando cita con id {}",id);
        cita.eliminar();
        if(cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE.getCodigo());

        log.info("Cita con id {} ha sico marcada como eliminada", id);
    }

    private Cita obtenerCitaOExepcion(Long id){
        log.info("Buscando cita con id {}", id);

        return citaRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Cita no encontrada con id " + id));
    }

    private MedicoResponse obtenerMedicoActivo(Long id){
        log.info("Buscando medico activo con id {}", id);

        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id){
        log.info("Buscando medico sin estadi con id {} en el servicio remoto", id);

        return medicoClient.obtenerMedicoSinEstadPorId(id);
    }

    private void validarMedicoActivoDisponible(MedicoResponse medico){
        log.info("Validando si el medico activo esta disponible");

        if(!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw new ConflictoException("El medico activo no se encuentra disponible");
    }

    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad){
        log.info("Actualizando disponibilidad del medico en el servicio remoto");
        medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
        log.info("Disponibilidad del medico actualizada en el servicio remoto");
    }

    private void cambiarDisponiblidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita){
        switch (estadoCita){
            case PENDIENTE, CONFIRMADA ->
                actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo());
            case EN_CURSO ->
                actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
            case FINALIZADA, CANCELADA ->
                actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());
        }
    }

    private PacienteResponse obtenerPacienteActivo(Long id){
        log.info("Obteniendo paciente con id: {}", id);

        return pacienteClient.obtenerPacientePorIdActivo(id);
    }

    private PacienteResponse obtenerPaciente(Long id){
        log.info("Obteniendo paciente sin estado con id: {}", id);

        return pacienteClient.obtenerPacienteSinEstadoPorId(id);
    }
}
