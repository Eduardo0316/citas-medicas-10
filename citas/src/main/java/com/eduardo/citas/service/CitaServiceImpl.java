package com.eduardo.citas.service;

import com.eduardo.citas.dto.CitaRequest;
import com.eduardo.citas.dto.CitaResponse;
import com.eduardo.citas.entity.Cita;
import com.eduardo.citas.enums.EstadoCita;
import com.eduardo.citas.mapper.CitaMapper;
import com.eduardo.citas.repository.CitaRepository;
import com.eduardo.commons.clients.MedicoClient;
import com.eduardo.commons.dto.medicos.MedicoResponse;
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

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        Cita cita = obtenerCitaOExepcion(idCita);

        log.info("Actualizando cita con id {}",idCita);
        cita.actualizarEstadoCita(
                EstadoCita.obtenerEstadoCitaPorCódigo(idEstadoCita));

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
                        null,
                        obtenerMedicoSinEstado(cita.getIdMedico())))
                .toList();
    }

    @Override
    public CitaResponse obtenerPorId(Long id) {
        Cita cita = obtenerCitaOExepcion(id);
        return citaMapper.entidadAResponse(
                cita,
                null,
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        log.info("Registrando una nueva cita");
        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        validarMedicoActivoDisponible(medico);

        Cita cita = citaMapper.requestAEntidad(request);
        citaRepository.saveAndFlush(cita);

        log.info("Cita registrada exitosamente");

        cambiarDisponiblidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        return citaMapper.entidadAResponse(
                cita,
                null,
                medico
        );
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        Cita cita = obtenerCitaOExepcion(id);
        if(cita.getEstadoCita() == EstadoCita.EN_CURSO)
            throw new ConflictoException("La cita esta en curso y no puede actualizarse");

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
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
                null,
                medico
        );
    }

    @Override
    public void eliminar(Long id) {
        Cita cita = obtenerCitaOExepcion(id);
        log.info("Eliminando cita con id {}",id);
        cita.eliminar();
        if(cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponivilidadMedico(cita.getIdMedico(),
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

    private void actualizarDisponivilidadMedico(Long idMedico, Long idDisponibilidad){
        log.info("Actualizando disponibilidad del medico en el servicio remoto");
        medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
        log.info("Disponibilidad del medico actualizada en el servicio remoto");
    }

    private void cambiarDisponiblidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita){
        switch (estadoCita){
            case PENDIENTE, CONFIRMADA ->
                actualizarDisponivilidadMedico(idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo());
            case EN_CURSO ->
                actualizarDisponivilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
            case FINALIZADA, CANCELADA ->
                actualizarDisponivilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());
        }
    }
}
