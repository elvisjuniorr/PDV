package net.originmobi.pdv.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.originmobi.pdv.enumerado.VendaSituacao;
import net.originmobi.pdv.filter.VendaFilter;
import net.originmobi.pdv.model.PagamentoTipo;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.Venda;
import net.originmobi.pdv.service.PagamentoTipoService;
import net.originmobi.pdv.service.PessoaService;
import net.originmobi.pdv.service.ProdutoService;
import net.originmobi.pdv.service.VendaProdutoService;
import net.originmobi.pdv.service.VendaService;
import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.controller.VendaController;


@RunWith(MockitoJUnitRunner.class)
public class VendaControllerTest {

    @InjectMocks
    private VendaController vendaController;

    @Mock
    private VendaService vendas;

    @Mock
    private PessoaService pessoas;

    @Mock
    private ProdutoService produtos;

    @Mock
    private VendaProdutoService vendaProdutos;

    @Mock
    private PagamentoTipoService pagamentoTipos;

    @Mock
    private TituloService titulos;

    private Venda venda;

    @Before
    public void setUp() {
        venda = new Venda();
        venda.setCodigo(1L);
        venda.setSituacao(VendaSituacao.ABERTA);

        when(vendas.abreVenda(any(Venda.class))).thenReturn(1L);
        when(vendas.busca(any(), anyString(), any())).thenReturn(Page.empty());
        when(vendaProdutos.listaProdutosVenda(any(Venda.class))).thenReturn(Arrays.asList());
        when(pessoas.lista()).thenReturn(Arrays.asList(new Pessoa()));
        when(produtos.listar()).thenReturn(Arrays.asList(new Produto()));
        when(produtos.listaProdutosVendaveis()).thenReturn(Arrays.asList(new Produto()));
        when(pagamentoTipos.listar()).thenReturn(Arrays.asList(new PagamentoTipo()));
        when(titulos.lista()).thenReturn(Arrays.asList(new Titulo()));
    }

    @Test
    public void testForm() {
        ModelAndView mv = vendaController.form();
        assertEquals("venda/form", mv.getViewName());
        assertNotNull(mv.getModel().get("venda"));
    }

    @Test
    public void testAbrirVenda() {
        RedirectAttributes attributes = mock(RedirectAttributes.class);
        Errors errors = mock(Errors.class);
        when(errors.hasErrors()).thenReturn(false);

        String redirect = vendaController.abrirVenda(venda, errors, attributes);
        assertEquals("redirect:/venda/1", redirect);
        verify(vendas, times(1)).abreVenda(any(Venda.class));
    }

    @Test
    public void testBuscaVenda() {
        ModelAndView mv = vendaController.buscaVenda(venda);
        assertEquals("venda/form", mv.getViewName());
        assertNotNull(mv.getModel().get("venda"));
        assertNotNull(mv.getModel().get("produtosVenda"));
    }

    @Test
    public void testAddProdutoVenda() {
        when(vendas.addProduto(anyLong(), anyLong(), anyDouble())).thenReturn("Produto adicionado");

        Map<String, String> request = new HashMap<>();
        request.put("codigoVen", "1");
        request.put("codigoPro", "2");
        request.put("valorBalanca", "10.5");

        String response = vendaController.addProdutoVenda(request);
        assertEquals("Produto adicionado", response);
    }

    @Test
    public void testRemoveProdutoVenda() {
        when(vendas.removeProduto(anyLong(), anyLong())).thenReturn("Produto removido");

        Map<String, String> request = new HashMap<>();
        request.put("posicaoPro", "0");
        request.put("codigoVen", "1");

        String response = vendaController.removeProdutoVenda(request);
        assertEquals("Produto removido", response);
    }

    @Test
    public void testFecharVenda() {
        when(vendas.fechaVenda(anyLong(), anyLong(), anyDouble(), anyDouble(), anyDouble(), any(), any())).thenReturn("Venda fechada");

        Map<String, String> request = new HashMap<>();
        request.put("venda", "1");
        request.put("pagamentotipo", "1");
        request.put("valor_produtos", "100");
        request.put("valor_desconto", "10");
        request.put("valor_acrescimo", "5");
        request.put("valores", "50,50");
        request.put("titulos", "t1,t2");

        String response = vendaController.fechar(request);
        assertEquals("Venda fechada", response);
    }

    @Test
    public void testTitulos() {
        List<Titulo> titulosList = vendaController.titulos();
        assertNotNull(titulosList);
        verify(titulos, times(1)).lista();
    }

    @Test
    public void testModelAttributes() {
        assertNotNull(vendaController.clientes());
        assertNotNull(vendaController.produtos());
        assertNotNull(vendaController.produtosVendaveis());
        assertNotNull(vendaController.pagamentoTipo());
        assertNotNull(vendaController.vendaSituacao());
    }

    @Test
    public void testListaPedidosComPagina() {
        Venda venda2 = new Venda();
        venda2.setCodigo(2L);
        venda2.setSituacao(VendaSituacao.ABERTA);

        @SuppressWarnings("unchecked")
        Page<Venda> page = new PageImpl(Arrays.asList(venda, venda2), PageRequest.of(0, 2), 2);
        when(vendas.busca(any(VendaFilter.class), anyString(), any())).thenReturn(page);

        ModelAndView mv = vendaController.listaPedidos(new VendaFilter(), "ABERTO", PageRequest.of(0, 2), mock(Model.class));
        assertEquals("venda/list", mv.getViewName());
        assertNotNull(mv.getModel().get("vendas"));
    }

    @Test
    public void testAbrirVendaComErros() {
        RedirectAttributes attributes = mock(RedirectAttributes.class);
        Errors errors = mock(Errors.class);

        when(errors.hasErrors()).thenReturn(true);

        String resultado = vendaController.abrirVenda(new Venda(), errors, attributes);

        assertEquals("venda/form", resultado);

        verify(vendas, never()).abreVenda(any(Venda.class));
    }

    @Test
    public void testAbrirVendaSemErros() {

        RedirectAttributes attributes = mock(RedirectAttributes.class);
        Errors errors = mock(Errors.class);

        when(errors.hasErrors()).thenReturn(false);
        when(vendas.abreVenda(any(Venda.class))).thenReturn(10L); 

        String resultado = vendaController.abrirVenda(new Venda(), errors, attributes);

        assertEquals("redirect:/venda/10", resultado);

        verify(vendas, times(1)).abreVenda(any(Venda.class));
    }

    @Test
public void testAddProdutoVendaRetornoEChamadaService() {
    Map<String, String> request = new HashMap<>();
    request.put("codigoVen", "5");
    request.put("codigoPro", "9");
    request.put("valorBalanca", "12.50");

    when(vendas.addProduto(5L, 9L, 12.50)).thenReturn("OK");

    String resposta = vendaController.addProdutoVenda(request);

    assertEquals("OK", resposta);
    verify(vendas, times(1)).addProduto(5L, 9L, 12.50);
}

@Test
    public void testRemoveProdutoVendaRetornoEChamadaService() {
        Map<String, String> request = new HashMap<>();
        request.put("posicaoPro", "3");
        request.put("codigoVen", "15");

        when(vendas.removeProduto(3L, 15L)).thenReturn("REMOVIDO");

        String resposta = vendaController.removeProdutoVenda(request);

        assertEquals("REMOVIDO", resposta);
        verify(vendas, times(1)).removeProduto(3L, 15L);
    }

    @Test
    public void testFecharVendaTodosParametrosRepasadosCorretamente() {
        Map<String, String> request = new HashMap<>();
        request.put("venda", "10");
        request.put("pagamentotipo", "2");
        request.put("valor_produtos", "100,50");
        request.put("valor_desconto", "5,00");
        request.put("valor_acrescimo", "3,25");
        request.put("valores", "10.00,90.50");
        request.put("titulos", "T1,T2");

        when(vendas.fechaVenda(
                10L, 2L,
                100.50, 5.00, 3.25,
                new String[]{"10.00","90.50"},
                new String[]{"T1","T2"}
        )).thenReturn("FECHADO");

        String retorno = vendaController.fechar(request);

        assertEquals("FECHADO", retorno);

        verify(vendas, times(1)).fechaVenda(
                10L, 2L,
                100.50, 5.00, 3.25,
                new String[]{"10.00","90.50"},
                new String[]{"T1","T2"}
        );
    }



}
