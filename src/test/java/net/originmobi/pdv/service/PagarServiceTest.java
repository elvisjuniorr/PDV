package net.originmobi.pdv;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Fornecedor;
import net.originmobi.pdv.model.Pagar;
import net.originmobi.pdv.model.PagarParcela;
import net.originmobi.pdv.model.PagarTipo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.PagarRepository;
import net.originmobi.pdv.service.CaixaLancamentoService;
import net.originmobi.pdv.service.CaixaService;
import net.originmobi.pdv.service.FornecedorService;
import net.originmobi.pdv.service.PagarParcelaService;
import net.originmobi.pdv.service.PagarService;
import net.originmobi.pdv.service.UsuarioService;

@RunWith(MockitoJUnitRunner.class)
public class PagarServiceTest {

    @Mock
    private PagarRepository pagarRepo;

    @Mock
    private PagarParcelaService pagarParcelaServ;

    @Mock
    private FornecedorService fornecedores;

    @Mock
    private CaixaService caixas;

    @Mock
    private UsuarioService usuarios;

    @Mock
    private CaixaLancamentoService lancamentos;

    @InjectMocks
    private PagarService pagarService;

    private PagarParcela parcela;
    private Caixa caixa;
    private Usuario usuario;
    private Pagar pagar;

    @Before
    public void setUp() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setCodigo(1L);
        
        PagarTipo pagarTipo = new PagarTipo();
        pagarTipo.setDescricao("Despesa Teste");
        
        pagar = new Pagar("Observação Teste", 100.0, LocalDate.now(), fornecedor, pagarTipo);

        parcela = new PagarParcela(100.0, 100.0, 0, null, LocalDate.now(), pagar);
        parcela.setCodigo(1L);
        parcela.setValor_desconto(0.0);
        parcela.setValor_acrescimo(0.0);
        parcela.setValor_pago(0.0);

        caixa = new Caixa();
        caixa.setCodigo(1L);
        caixa.setValor_total(200.0);

        usuario = new Usuario();
        usuario.setCodigo(1L);
        usuario.setUser("usuario_test");
    }

    @Test
    public void testQuitarSucessoPagamentoTotal() {
        // Arrange
        Long codparcela = 1L;
        Double vlPago = 100.0;
        Double vldesc = 0.0;
        Double vlacre = 0.0;
        Long codCaixa = 1L;

        when(pagarParcelaServ.busca(codparcela)).thenReturn(Optional.of(parcela));
        when(caixas.busca(codCaixa)).thenReturn(Optional.of(caixa));
        when(usuarios.buscaUsuario(any(String.class))).thenReturn(usuario);


        String resultado = pagarService.quitar(codparcela, vlPago, vldesc, vlacre, codCaixa);

        assertEquals("Pagamento realizado com sucesso", resultado);
        assertEquals(1, parcela.getQuitado());
        assertEquals(0.0, parcela.getValor_restante(), 0.001);
        
        verify(pagarParcelaServ).busca(codparcela);
        verify(pagarParcelaServ).merger(parcela);
        verify(lancamentos).lancamento(any(CaixaLancamento.class));
    }

    @Test
    public void testQuitarErroValorPagamentoInvalido() {
        // Arrange
        Long codparcela = 1L;
        Double vlPago = 150.0;
        Double vldesc = 0.0;
        Double vlacre = 0.0;
        Long codCaixa = 1L;

        when(pagarParcelaServ.busca(codparcela)).thenReturn(Optional.of(parcela));

        // Act
        try {
            pagarService.quitar(codparcela, vlPago, vldesc, vlacre, codCaixa);
            assertEquals("Deveria ter lançado exceção", false);
        } catch (RuntimeException e) {
            // Assert
            assertEquals("Valor de pagamento inválido", e.getMessage());
        }
        
        verify(pagarParcelaServ).busca(codparcela);
        verify(pagarParcelaServ, never()).merger(any(PagarParcela.class));
        verify(lancamentos, never()).lancamento(any(CaixaLancamento.class));
    }

    @Test
    public void testQuitarSucessoPagamentoParcial() {
        // Arrange
        Long codparcela = 1L;
        Double vlPago = 50.0; 
        Double vldesc = 5.0;  
        Double vlacre = 2.0;
        Long codCaixa = 1L;

        when(pagarParcelaServ.busca(codparcela)).thenReturn(Optional.of(parcela));
        when(caixas.busca(codCaixa)).thenReturn(Optional.of(caixa));
        when(usuarios.buscaUsuario(any(String.class))).thenReturn(usuario);

        // Act
        String resultado = pagarService.quitar(codparcela, vlPago, vldesc, vlacre, codCaixa);

        // Assert
        assertEquals("Pagamento realizado com sucesso", resultado);
        assertEquals(0, parcela.getQuitado());
        assertEquals(45.0, parcela.getValor_restante(), 0.001);
        assertEquals(52.0, parcela.getValor_pago(), 0.001);
        
        verify(pagarParcelaServ).busca(codparcela);
        verify(pagarParcelaServ).merger(parcela);
        verify(lancamentos).lancamento(any(CaixaLancamento.class));
    }
}