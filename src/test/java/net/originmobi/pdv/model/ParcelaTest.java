package net.originmobi.pdv.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.sql.Date;
import java.sql.Timestamp;
import org.junit.Before;
import org.junit.Test;

public class ParcelaTest {

    private Parcela parcela;

    @Before // Usando @Before do JUnit 4
    public void setUp() {
        parcela = new Parcela();
    }

    @Test // Usando @Test do JUnit 4
    public void testSetAndGetCodigo() {
        parcela.setCodigo(1L);
        assertEquals(Long.valueOf(1L), parcela.getCodigo());
    }

    @Test
    public void testSetAndGetValorTotal() {
        parcela.setValor_total(150.75);
        assertEquals(150.75, parcela.getValor_total(), 0.001);
    }

    @Test
    public void testSetAndGetValorAcrescimo() {
        parcela.setValor_acrescimo(10.50);
        assertEquals(10.50, parcela.getValor_acrescimo(), 0.001);
    }

    @Test
    public void testSetAndGetValorDesconto() {
        parcela.setValor_desconto(5.25);
        assertEquals(5.25, parcela.getValor_desconto(), 0.001);
    }

    @Test
    public void testSetAndGetValorRecebido() {
        parcela.setValor_recebido(100.0);
        assertEquals(100.0, parcela.getValor_recebido(), 0.001);
    }

    @Test
    public void testSetAndGetValorRestante() {
        parcela.setValor_restante(50.75);
        assertEquals(50.75, parcela.getValor_restante(), 0.001);
    }

    @Test
    public void testSetAndGetQuitado() {
        parcela.setQuitado(1);
        assertEquals(1, parcela.getQuitado());
    }

    @Test
    public void testSetAndGetSequencia() {
        parcela.setSequencia(1);
        assertEquals(1, parcela.getSequencia());
    }


    @Test
    public void testIsQuitado_DeveRetornarTrue_QuandoQuitadoFor1() {
        parcela.setQuitado(1);
        assertTrue(parcela.isQuitado());
    }

    @Test
    public void testIsQuitado_DeveRetornarFalse_QuandoQuitadoFor0() {
        parcela.setQuitado(0);
        assertFalse(parcela.isQuitado());
    }

    @Test
    public void testIsQuitado_DeveRetornarFalse_ParaValoresDiferentesDe1() {
        parcela.setQuitado(2);
        assertFalse(parcela.isQuitado());

        parcela.setQuitado(-1);
        assertFalse(parcela.isQuitado());
    }
}