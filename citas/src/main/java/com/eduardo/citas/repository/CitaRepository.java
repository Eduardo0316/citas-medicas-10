package com.eduardo.citas.repository;

import com.eduardo.citas.entity.Cita;
import com.eduardo.citas.enums.EstadoCita;
import com.eduardo.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaNotIn(Long idPaciente, EstadoRegistro estadoRegistro, Collection<EstadoCita> estadoCitas);
}
