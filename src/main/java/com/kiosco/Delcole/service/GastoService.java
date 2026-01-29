package com.kiosco.Delcole.service;

import com.kiosco.Delcole.model.Gasto;
import com.kiosco.Delcole.repository.GastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GastoService {

    private final GastoRepository gastoRepository;

    @Transactional
    public Gasto guardar(Gasto gasto) {
        if (gasto.getFecha() == null) {
            gasto.setFecha(LocalDate.now());
        }
        return gastoRepository.save(gasto);
    }

    public Optional<Gasto> buscarPorId(Long id) {
        return gastoRepository.findById(id);
    }

    public List<Gasto> listarTodos() {
        return gastoRepository.findAll();
    }

    public List<Gasto> gastosEntre(LocalDate desde, LocalDate hasta) {
        if (desde == null && hasta == null) {
            return listarTodos();
        }
        if (desde == null) {
            desde = LocalDate.of(2000, 1, 1);
        }
        if (hasta == null) {
            hasta = LocalDate.now();
        }
        return gastoRepository.findByFechaBetweenOrderByFechaDesc(desde, hasta);
    }

    public BigDecimal sumarGastosEntre(LocalDate desde, LocalDate hasta) {
        if (desde == null && hasta == null) {
            return gastoRepository.sumMontoEntre(LocalDate.of(2000, 1, 1), LocalDate.now());
        }
        if (desde == null) {
            desde = LocalDate.of(2000, 1, 1);
        }
        if (hasta == null) {
            hasta = LocalDate.now();
        }
        BigDecimal suma = gastoRepository.sumMontoEntre(desde, hasta);
        return suma != null ? suma : BigDecimal.ZERO;
    }

    @Transactional
    public void eliminar(Long id) {
        gastoRepository.deleteById(id);
    }
}
