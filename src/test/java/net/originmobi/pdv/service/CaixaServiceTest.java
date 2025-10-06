package net.originmobi.pdv.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.filter.BancoFilter;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.CaixaRepository;
import net.originmobi.pdv.singleton.Aplicacao;

@RunWith(MockitoJUnitRunner.class)
public class CaixaServiceTest {

    @InjectMocks
    private CaixaService caixaService;

    @Mock
    private CaixaRepository caixaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CaixaLancamentoService lancamentoService;
    
    @Mock
    private Aplicacao aplicacao;

    private Usuario usuario;
    private Caixa caixa;
    
    @Before
    public void setUp() {

        usuario = new Usuario();
        usuario.setCodigo(1L);

        usuario.setSenha(new BCryptPasswordEncoder().encode("123")); 

        caixa = new Caixa();
        caixa.setCodigo(1L);
        caixa.setTipo(CaixaTipo.CAIXA);
        caixa.setDescricao("");
        caixa.setValor_abertura(100.0);
        caixa.setAgencia("Ag-123");
        caixa.setConta("C-123");

        when(usuarioService.buscaUsuario(anyString())).thenReturn(usuario);
        
        when(aplicacao.getUsuarioAtual()).thenReturn("user");
    }
    
    @Test
    public void deveCadastrarCaixaComValorDeAberturaMaiorQueZeroELancamento() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.empty());
        when(caixaRepository.save(any(Caixa.class))).thenReturn(caixa);

        Long codigo = caixaService.cadastro(caixa);

        assertEquals(Long.valueOf(1L), codigo);
        verify(lancamentoService, times(1)).lancamento(any(CaixaLancamento.class));
    }

    @Test
    public void deveCadastrarCaixaComValorAberturaNuloEConverterParaZero() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.empty());
        when(caixaRepository.save(any(Caixa.class))).thenReturn(caixa);
        caixa.setValor_abertura(null);

        caixaService.cadastro(caixa);

        assertEquals(0.0, caixa.getValor_abertura(), 0.0001);
        assertEquals(0.0, caixa.getValor_total(), 0.0001);
        verify(lancamentoService, times(0)).lancamento(any(CaixaLancamento.class));
    }
    
    @Test
    public void deveUsarDescricaoPadraoParaCaixaQuandoDescricaoVazia() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.empty());
        when(caixaRepository.save(any(Caixa.class))).thenReturn(caixa);
        caixa.setDescricao("");
        caixa.setValor_abertura(0.0);
        caixa.setTipo(CaixaTipo.CAIXA);

        caixaService.cadastro(caixa);
        assertEquals("Caixa diário", caixa.getDescricao());
    }

    @Test
    public void deveUsarDescricaoPadraoParaCofreQuandoDescricaoVazia() {
        when(caixaRepository.save(any(Caixa.class))).thenReturn(caixa);
        caixa.setDescricao("");
        caixa.setValor_abertura(0.0);
        caixa.setTipo(CaixaTipo.COFRE);

        caixaService.cadastro(caixa);
        assertEquals("Cofre", caixa.getDescricao());
    }
    
    @Test
    public void deveUsarDescricaoPadraoParaBancoQuandoDescricaoVazia() {
        when(caixaRepository.save(any(Caixa.class))).thenReturn(caixa);
        caixa.setDescricao("");
        caixa.setValor_abertura(0.0);
        caixa.setTipo(CaixaTipo.BANCO);

        caixaService.cadastro(caixa);
        assertEquals("Banco", caixa.getDescricao());
    }
    
    @Test
    public void deveManterDescricaoPersonalizadaQuandoNaoVazia() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.empty());
        when(caixaRepository.save(any())).thenReturn(caixa);
        caixa.setDescricao("Caixa Personalizado");
        caixa.setTipo(CaixaTipo.CAIXA);
        
        caixaService.cadastro(caixa);
        assertEquals("Caixa Personalizado", caixa.getDescricao());
    }

    @Test
    public void deveRemoverNaoDigitosDaAgenciaEContaParaBanco() {
        when(caixaRepository.save(any())).thenReturn(caixa);
        caixa.setTipo(CaixaTipo.BANCO);
        caixa.setAgencia("Ag-123_test");
        caixa.setConta("C-456-X7");

        caixaService.cadastro(caixa);

        assertEquals("123", caixa.getAgencia());
        assertEquals("4567", caixa.getConta());
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoQuandoCaixaDoTipoCaixaJaEstaAberto() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.of(new Caixa()));
        caixa.setTipo(CaixaTipo.CAIXA);
        caixaService.cadastro(caixa);
    }
    
    @Test
    public void naoDeveLancarExcecaoQuandoCofreJaEstaAberto() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.of(new Caixa()));
        when(caixaRepository.save(any())).thenReturn(caixa);
        caixa.setTipo(CaixaTipo.COFRE);
        caixa.setValor_abertura(0.0);

        caixaService.cadastro(caixa);
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoQuandoValorAberturaNegativo() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.empty());
        caixa.setValor_abertura(-10.0);
        caixaService.cadastro(caixa);
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoAoSalvarCaixa() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.empty());
        doThrow(new RuntimeException()).when(caixaRepository).save(any());
        caixaService.cadastro(caixa);
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoAoCriarLancamento() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.empty());
        when(caixaRepository.save(any())).thenReturn(caixa);
        caixa.setValor_abertura(10.0);
        
        doThrow(new RuntimeException()).when(lancamentoService).lancamento(any());
        caixaService.cadastro(caixa);
    }

    @Test
    public void deveFecharCaixaComSucesso() {
        Caixa caixaAberto = new Caixa();
        caixaAberto.setValor_total(200.0);
        when(caixaRepository.findById(1L)).thenReturn(Optional.of(caixaAberto));
        when(caixaRepository.save(any())).thenReturn(caixaAberto);

        String result = caixaService.fechaCaixa(1L, "123");
        
        assertEquals("Caixa fechado com sucesso", result);
        assertNotNull(caixaAberto.getData_fechamento());
        assertEquals(200.0, caixaAberto.getValor_fechamento(), 0.0001);
    }

    @Test
    public void deveRetornarMensagemSenhaVazia() {
        String result = caixaService.fechaCaixa(1L, "");
        assertEquals("Favor, informe a senha", result);
    }

    @Test
    public void deveRetornarMensagemSenhaIncorreta() {
        String result = caixaService.fechaCaixa(1L, "senha errada");
        assertEquals("Senha incorreta, favor verifique", result);
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoAoFecharCaixaJaFechado() {
        Caixa caixaFechado = new Caixa();
        caixaFechado.setData_fechamento(new Timestamp(System.currentTimeMillis())); 
        when(caixaRepository.findById(1L)).thenReturn(Optional.of(caixaFechado));
        
        caixaService.fechaCaixa(1L, "123");
    }
    
    @Test
    public void deveDefinirValorTotalComoZeroSeNaoEstiverPresenteAoFechar() {
        Caixa caixaAberto = mock(Caixa.class);
        when(caixaAberto.getValor_total()).thenReturn(null);
        when(caixaRepository.findById(1L)).thenReturn(Optional.of(caixaAberto));
        when(caixaRepository.save(any())).thenReturn(caixaAberto);
        
        String result = caixaService.fechaCaixa(1L, "123"); 
        
        assertEquals("Caixa fechado com sucesso", result);
        verify(caixaAberto).setValor_fechamento(0.0);
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoAoSalvarFechamentoCaixa() {
        Caixa caixaAberto = new Caixa();
        when(caixaRepository.findById(1L)).thenReturn(Optional.of(caixaAberto));
        doThrow(new RuntimeException()).when(caixaRepository).save(any()); 
        
        caixaService.fechaCaixa(1L, "123");
    }
    
    @Test
    public void deveVerificarCaixaAbertoRetornandoTrue() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.of(new Caixa()));
        assertTrue(caixaService.caixaIsAberto());
    }

    @Test
    public void deveVerificarCaixaAbertoRetornandoFalse() {
        when(caixaRepository.caixaAberto()).thenReturn(Optional.empty());
        assertFalse(caixaService.caixaIsAberto());
    }

    @Test
    public void deveListarTodosCaixas() {
        when(caixaRepository.findByCodigoOrdenado()).thenReturn(Arrays.asList(new Caixa()));
        assertEquals(1, caixaService.listaTodos().size());
    }

    @Test
    public void deveListarCaixasPorData() {
        CaixaFilter filter = new CaixaFilter();
        filter.setData_cadastro("2025-10-25");
        when(caixaRepository.buscaCaixasPorDataAbertura(any(Date.class))).thenReturn(Arrays.asList(new Caixa()));
        
        assertEquals(1, caixaService.listarCaixas(filter).size());
    }

    @Test
    public void deveListarCaixasAbertosQuandoFiltroDeDataForVazio() {
        CaixaFilter filter = new CaixaFilter();
        filter.setData_cadastro("");
        when(caixaRepository.listaCaixasAbertos()).thenReturn(Arrays.asList(new Caixa()));
        
        assertEquals(1, caixaService.listarCaixas(filter).size());
        verify(caixaRepository, times(1)).listaCaixasAbertos();
    }
    
    @Test
    public void deveListarCaixasAbertosQuandoFiltroDeDataForNulo() {
        CaixaFilter filter = new CaixaFilter();
        filter.setData_cadastro(null);
        when(caixaRepository.listaCaixasAbertos()).thenReturn(Arrays.asList(new Caixa()));
        
        assertEquals(1, caixaService.listarCaixas(filter).size());
        verify(caixaRepository, times(1)).listaCaixasAbertos();
    }

    @Test
    public void deveListarBancosAbertosComData() {
        BancoFilter filter = new BancoFilter();
        filter.setData_cadastro("2025-10-05");
        when(caixaRepository.buscaCaixaTipoData(any(CaixaTipo.class), any(Date.class))).thenReturn(Arrays.asList(new Caixa()));
        
        assertEquals(1, caixaService.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filter).size());
    }

    @Test
    public void deveListarBancosAbertosSemData() {
        BancoFilter filter = new BancoFilter();
        filter.setData_cadastro(null);
        when(caixaRepository.buscaCaixaTipo(CaixaTipo.BANCO)).thenReturn(Arrays.asList(new Caixa()));
        
        assertEquals(1, caixaService.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filter).size());
    }
    
    @Test
    public void deveBuscarCaixaPorCodigo() {
        Caixa caixa = new Caixa();
        when(caixaRepository.findById(1L)).thenReturn(Optional.of(caixa));
        
        Optional<Caixa> result = caixaService.busca(1L);
        assertTrue(result.isPresent());
    }

    @Test
    public void deveBuscarCaixaAbertoDoUsuario() {
        when(usuarioService.buscaUsuario("user")).thenReturn(usuario);
        when(caixaRepository.findByCaixaAbertoUsuario(anyLong())).thenReturn(new Caixa());
        
        Optional<Caixa> result = caixaService.buscaCaixaUsuario("user");
        assertTrue(result.isPresent());
    }
    
    @Test
    public void deveRetornarOptionalVazioQuandoCaixaDoUsuarioNaoEncontrado() {
        when(usuarioService.buscaUsuario("user")).thenReturn(usuario);
        when(caixaRepository.findByCaixaAbertoUsuario(anyLong())).thenReturn(null); 
        
        Optional<Caixa> result = caixaService.buscaCaixaUsuario("user");
        assertFalse(result.isPresent());
    }
    
    @Test
    public void deveListarCaixasAbertos() {
        when(caixaRepository.caixasAbertos()).thenReturn(Arrays.asList(new Caixa()));
        assertEquals(1, caixaService.caixasAbertos().size());
    }
    
    @Test
    public void deveListarApenasBancos() {
        when(caixaRepository.buscaBancos(CaixaTipo.BANCO)).thenReturn(Arrays.asList(new Caixa()));
        List<Caixa> bancos = caixaService.listaBancos();
        assertEquals(1, bancos.size());
    }
    
    @Test
    public void deveListarCaixasAbertosDeUmTipoEspecifico() {
        when(caixaRepository.buscaCaixaTipo(CaixaTipo.COFRE)).thenReturn(Arrays.asList(new Caixa(), new Caixa()));
        List<Caixa> cofres = caixaService.listaCaixasAbertosTipo(CaixaTipo.COFRE);
        assertEquals(2, cofres.size());
    }
}