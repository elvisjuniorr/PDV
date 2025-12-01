package net.originmobi.pdv.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Fornecedor;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.service.CaixaLancamentoService;
import net.originmobi.pdv.service.CaixaService;
import net.originmobi.pdv.service.UsuarioService;
import net.originmobi.pdv.singleton.Aplicacao;
import net.originmobi.pdv.controller.CaixaController;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class CaixaControllerTest {

    @InjectMocks
    private CaixaController controller;

    @Mock
    private CaixaService caixas;

    @Mock
    private CaixaLancamentoService lancamentos;

    @Mock
    private UsuarioService usuarios;

    @Mock
    private Aplicacao aplicacao;

    @Before
    public void setup() {
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken("usuarioTeste", null)
    );
}

    @Test
    public void testForm() {
        ModelAndView mv = controller.form();
        assertEquals("caixa/form", mv.getViewName());
        assertNotNull(mv.getModel().get("caixa"));
    }

    @Test
    public void testLista() {
        CaixaFilter filter = new CaixaFilter();
        when(caixas.listarCaixas(filter)).thenReturn(Arrays.asList(new Caixa(), new Caixa()));

        ModelAndView mv = controller.lista(filter);
        assertEquals("caixa/list", mv.getViewName());
        assertNotNull(mv.getModel().get("caixas"));
        assertEquals(2, ((List)mv.getModel().get("caixas")).size());
    }

    @Test
    public void testCadastro() {
        Map<String, String> request = new HashMap<>();
        request.put("descricao", "Caixa Teste");
        request.put("tipo", "BANCO");
        request.put("valor_abertura", "100,00");
        request.put("agencia", "1234");
        request.put("conta", "5678");

        UriComponentsBuilder builder = mock(UriComponentsBuilder.class);
        UriComponents uri = mock(UriComponents.class);
        when(builder.path("/caixa/gerenciar/")).thenReturn(builder);
        when(builder.build()).thenReturn(uri);
        when(uri.toUri()).thenReturn(null);

        when(caixas.cadastro(any(Caixa.class))).thenReturn(1L);

        String response = controller.cadastro(request, builder);
        assertTrue(response.contains("1"));
    }

    @Test
    public void testGerenciar() {
        Caixa caixa = new Caixa();
        caixa.setDescricao("Caixa Teste");

        when(lancamentos.lancamentosDoCaixa(caixa)).thenReturn(Arrays.asList(new CaixaLancamento()));

        ModelAndView mv = controller.gerenciar(caixa);
        assertEquals("caixa/gerenciar", mv.getViewName());
        assertNotNull(mv.getModel().get("caixa"));
        assertNotNull(mv.getModel().get("lancamentos"));
        assertEquals(1, ((List) mv.getModel().get("lancamentos")).size());
    }

    @Test
    public void testFazSuprimento() {
        Map<String, String> request = new HashMap<>();
        request.put("valor", "50,00");
        request.put("obs", "Suprimento Teste");
        request.put("caixa", "1");

        Caixa caixa = new Caixa();
        when(caixas.busca(anyLong())).thenReturn(Optional.of(caixa));
        Usuario usuario = new Usuario();
        when(usuarios.buscaUsuario(anyString())).thenReturn(usuario);
        when(lancamentos.lancamento(any(CaixaLancamento.class))).thenReturn("Suprimento feito");

        String result = controller.fazSuprimento(request);
        assertEquals("Suprimento feito", result);
    }

    @Test
    public void testFazSangria() {
        Map<String, String> request = new HashMap<>();
        request.put("valor", "30,00");
        request.put("obs", "Sangria Teste");
        request.put("caixa", "1");

        Caixa caixa = new Caixa();
        when(caixas.busca(anyLong())).thenReturn(Optional.of(caixa));
        Usuario usuario = new Usuario();
        when(usuarios.buscaUsuario(anyString())).thenReturn(usuario);
        when(lancamentos.lancamento(any(CaixaLancamento.class))).thenReturn("Sangria feita");

        String result = controller.fazSangria(request);
        assertEquals("Sangria feita", result);
    }

    @Test
    public void testFecha() {
        Map<String, String> request = new HashMap<>();
        request.put("caixa", "1");
        request.put("senha", "123");

        when(caixas.fechaCaixa(anyLong(), anyString())).thenReturn("Caixa fechado");

        String result = controller.fecha(request);
        assertEquals("Caixa fechado", result);
    }

    @Test
    public void testUsuarioAtual() {
        Aplicacao aplicacaoMock = mock(Aplicacao.class);
        String usuario = controller.usuarioAtual();
        assertNotNull(usuario);
    }

    @Test
    public void testCaixatipo() {
        List<CaixaTipo> tipos = controller.caixatipo();
        assertNotNull(tipos);
        assertTrue(tipos.size() > 0);
    }

    @Test
    public void testDestinos() {
        when(caixas.caixasAbertos()).thenReturn(Arrays.asList(new Caixa()));
        List<Caixa> destinos = controller.destinos();
        assertNotNull(destinos);
        assertEquals(1, destinos.size());
    }

    @Test
    public void testFazSuprimento_caixaNaoExiste() {
     Map<String,String> req = new HashMap<>();
        req.put("valor","10,00");
        req.put("obs","teste");
        req.put("caixa","1");

        when(caixas.busca(anyLong())).thenReturn(Optional.empty()); // -> Caixa não encontrada

        String result = controller.fazSuprimento(req);

        assertEquals("", result);
    }

    @Test
    public void testCadastro_populaCamposCorretamente() {
        Map<String, String> request = new HashMap<>();
        request.put("descricao", "Caixa Teste");
        request.put("tipo", "BANCO");
        request.put("valor_abertura", "100,00");
        request.put("agencia", "1234");
        request.put("conta", "5678");

        UriComponentsBuilder builder = mock(UriComponentsBuilder.class);
        UriComponents uri = mock(UriComponents.class);

        when(builder.path(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(uri);
        when(uri.toUri()).thenReturn(null);

        ArgumentCaptor<Caixa> captor = ArgumentCaptor.forClass(Caixa.class);
        when(caixas.cadastro(captor.capture())).thenReturn(1L);

        controller.cadastro(request, builder);

        Caixa caixa = captor.getValue();

        assertEquals("Caixa Teste", caixa.getDescricao());
        assertEquals(CaixaTipo.BANCO, caixa.getTipo());
        assertEquals(100.00, caixa.getValor_abertura(), 0.01);
        assertEquals("1234", caixa.getAgencia());
        assertEquals("5678", caixa.getConta());
    }

    @Test
    public void testCadastroGeraHeaderLocation() {

        Map<String, String> request = new HashMap<>();
        request.put("descricao", "TesteMutante88");
        request.put("tipo", "BANCO");
        request.put("valor_abertura", "200,00");

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost");

        when(caixas.cadastro(any(Caixa.class))).thenReturn(7L);

        String resposta = controller.cadastro(request, builder);

        assertTrue("Não gerou header Location!", resposta.contains("Location"));

        assertTrue("URL incorreta!", resposta.contains("/caixa/gerenciar/"));
    }



}
