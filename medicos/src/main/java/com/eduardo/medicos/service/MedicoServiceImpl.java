package com.eduardo.medicos.service;

import com.eduardo.commons.clients.CitaClient;
import com.eduardo.commons.dto.medicos.MedicoRequest;
import com.eduardo.commons.dto.medicos.MedicoResponse;
import com.eduardo.commons.enums.DisponibilidadMedico;
import com.eduardo.commons.enums.EspecialidadMedico;
import com.eduardo.commons.enums.EstadoRegistro;
import com.eduardo.commons.exceptions.DatoInvalidoException;
import com.eduardo.commons.exceptions.RecursoNoEncontradoException;
import com.eduardo.medicos.entity.Medico;
import com.eduardo.medicos.mapper.MedicoMapper;
import com.eduardo.medicos.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MedicoServiceImpl implements MedicoService{
    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final CitaClient citaClient;

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("Buscando medico sin estado con id {}", id);

        return medicoMapper.entidadAResponse(medicoRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "Médico sin estado no encontrado con id: " + id)));
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoActivoPorId(idMedico);

        log.info("Actualizando disponibilidad del médico con id: {}", idMedico);

        DisponibilidadMedico nuevaDisponivilidad = DisponibilidadMedico.obtenerDisponibilidadPorCodigo(idDisponibilidad);
        DisponibilidadMedico antiguaDisponibilidad = medico.getDisponibilidad();
        medico.actualizarDisponibilidad(nuevaDisponivilidad);
        medicoRepository.saveAndFlush(medico);

        log.info("Disponibilidad del médico con id {} cambio de {} a {}",
                idMedico, antiguaDisponibilidad, nuevaDisponivilidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        log.info("Listando todos los medicos");
        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        Medico medico = obtenerMedicoActivoPorId(id);
        log.info("Medico con id {} obtenido exitosamente", id);
        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        log.info("Registrando nuevo medico {}", request.nombre());

        validarDatosUnicos(request);

        Medico medico = medicoMapper.requestAEntidad(
                request, EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        medicoRepository.saveAndFlush(medico);
        log.info("Nuevo medico registrado {}", request.nombre());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        Medico medico = obtenerMedicoActivoPorId(id);
        //Validar que no tenga citas activas
        citaClient.medicoConCitaActiva(medico.getId());
        validarCambiosUnicos(request, id);

        log.info("Actualizando medico con id: {}", id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad())
        );
        medicoRepository.saveAndFlush(medico);
        log.info("Medico con id {} actualizado exitosamente", id);

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void eliminar(Long id) {
        Medico medico = obtenerMedicoActivoPorId(id);
        //Validar que no tenga citas activas
        citaClient.medicoConCitaActiva(medico.getId());

        log.info("Crucificando medico con id: {}", id);

        medico.eliminar();
        medicoRepository.saveAndFlush(medico);

        log.info("Médico ha sido crucificado exitosamente");
    }

    private Medico obtenerMedicoActivoPorId(Long id){
        log.info("Obteniendo médico activo por id {}", id);

        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Medico activo no encontrado con id: " + id));
    }

    private void validarDatosUnicos(MedicoRequest request){
        log.info("Validando email unico");
        if(medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email(), EstadoRegistro.ACTIVO))
            throw new DatoInvalidoException("Ya existe un medico con ese email");

        log.info("Validando telefono unico");
        if(medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono(), EstadoRegistro.ACTIVO))
            throw new DatoInvalidoException("Ya existe un médico con ese telefono");

        log.info("Validando cedula profesional unica");

        if(medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional(), EstadoRegistro.ACTIVO))
            throw new DatoInvalidoException("Ya existe un médico registrado con la cedula profesional " + request.cedulaProfesional());
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id){
        log.info("Validando cambio en email unico");
        if(medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email(), EstadoRegistro.ACTIVO, id))
            throw new DatoInvalidoException("Ya existe un medico con ese email");

        log.info("Validando cambio en telefono unico");
        if(medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono(), EstadoRegistro.ACTIVO, id))
            throw new DatoInvalidoException("Ya existe un médico con ese telefono");

        log.info("Validando cambio en cedula profesional unica");

        if(medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional(), EstadoRegistro.ACTIVO, id))
            throw new DatoInvalidoException("Ya existe un médico registrado con la cedula profesional " + request.cedulaProfesional());
    }
}
