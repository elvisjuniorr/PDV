package net.originmobi.pdv.controller;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.originmobi.pdv.enumerado.TelefoneTipo;
import net.originmobi.pdv.filter.FornecedorFilter;
import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.Fornecedor;
import net.originmobi.pdv.model.Telefone;
import net.originmobi.pdv.service.CidadeService;
import net.originmobi.pdv.service.EnderecoService;
import net.originmobi.pdv.service.FornecedorService;
import net.originmobi.pdv.service.TelefoneService;
import net.originmobi.pdv.controller.FornecedorController;;

@RunWith(MockitoJUnitRunner.class)
public class FornecedorControllerTest {

    @InjectMocks
    private FornecedorController controller;

    @Mock
    private FornecedorService fornecedores;

    @Mock
    private CidadeService cidades;

    @Mock
    private EnderecoService enderecos;

    @Mock
    private TelefoneService telefones;

    @Mock
    private Errors errors;

    @Mock
    private RedirectAttributes redirectAttributes;

    private Fornecedor fornecedor;
    private Endereco endereco;
    private Telefone telefone;

    @Before
    public void setUp() {
        fornecedor = new Fornecedor();
        fornecedor.setEndereco(new Endereco());
        fornecedor.setTelefone(Arrays.asList(new Telefone()));

        endereco = new Endereco();
        endereco.setCodigo(1L);
        fornecedor.setEndereco(endereco);

        telefone = new Telefone();
        telefone.setCodigo(1L);
        fornecedor.setTelefone(Arrays.asList(telefone));
    }

    @Test
    public void testForm() {
        ModelAndView mv = controller.form();
        assertNotNull(mv);
        assertEquals("fornecedor/form", mv.getViewName());
        assertNotNull(mv.getModel().get("fornecedor"));
        assertNotNull(mv.getModel().get("endereco"));
        assertNotNull(mv.getModel().get("telefone"));
    }

    @Test
    public void testBusca() {
        FornecedorFilter filter = new FornecedorFilter();
        List<Fornecedor> lista = Arrays.asList(fornecedor);
        when(fornecedores.busca(filter)).thenReturn(lista);

        ModelAndView mv = controller.busca(filter);
        assertEquals("fornecedor/list", mv.getViewName());
        assertEquals(lista, mv.getModel().get("todosFornecedores"));
    }

    @Test
    public void testEditar() {
        fornecedor.setEndereco(endereco);
        fornecedor.setTelefone(Arrays.asList(telefone));

        when(enderecos.enderecoCodigo(anyLong())).thenReturn(endereco);
        when(telefones.telefoneCodigo(anyLong())).thenReturn(telefone);

        ModelAndView mv = controller.editar(fornecedor);
        assertEquals("fornecedor/form", mv.getViewName());
        assertEquals(fornecedor, mv.getModel().get("fornecedor"));
        assertEquals(endereco, mv.getModel().get("endereco"));
        assertEquals(telefone, mv.getModel().get("telefone"));
    }

    @Test
    public void testCadastrarSemErros() throws Exception {
        when(errors.hasErrors()).thenReturn(false);
        when(fornecedores.cadastrar(any(Fornecedor.class))).thenReturn("Fornecedor cadastrado com sucesso");

        String resultado = controller.codastrar(fornecedor, errors, endereco, telefone, redirectAttributes);

        verify(enderecos).cadastrar(endereco);
        verify(telefones).cadastrar(telefone);
        verify(fornecedores).cadastrar(fornecedor);
        assertEquals("redirect:/fornecedor/form", resultado);
    }

    @Test
    public void testCadastrarComErros() {
        when(errors.hasErrors()).thenReturn(true);
        String resultado = controller.codastrar(fornecedor, errors, endereco, telefone, redirectAttributes);
        assertEquals("fornecedor/form", resultado);
    }

    @Test
    public void testCidadesModelAttribute() {
        List<Cidade> lista = Arrays.asList(new Cidade());
        when(cidades.lista()).thenReturn(lista);
        List<Cidade> result = controller.cidades();
        assertEquals(lista, result);
    }

    @Test
    public void testTelefoneTipoModelAttribute() {
        List<TelefoneTipo> tipos = controller.telefoneTipo();
        assertNotNull(tipos);
        assertTrue(tipos.contains(TelefoneTipo.FIXO) || tipos.contains(TelefoneTipo.CELULAR));
    }

    @Test
    public void testCadastrarValidaSetEndereco() throws Exception {
        when(errors.hasErrors()).thenReturn(false);
        when(fornecedores.cadastrar(any(Fornecedor.class))).thenReturn("OK");

        controller.codastrar(fornecedor, errors, endereco, telefone, redirectAttributes);

     assertEquals("Endereço não aplicado corretamente", endereco, fornecedor.getEndereco());
    }
    
    @Test
    public void testCadastrarValidaSetTelefone() throws Exception {
        when(errors.hasErrors()).thenReturn(false);
        when(fornecedores.cadastrar(any(Fornecedor.class))).thenReturn("OK");

        controller.codastrar(fornecedor, errors, endereco, telefone, redirectAttributes);

        assertEquals(1, fornecedor.getTelefone().size());
        assertEquals(telefone.getCodigo(), fornecedor.getTelefone().get(0).getCodigo());
}
    @Test
    public void testCadastrarErroNoEnderecoAumentaCobertura() throws Exception {
     when(errors.hasErrors()).thenReturn(false);
     doThrow(new RuntimeException("falha endereço")).when(enderecos).cadastrar(any(Endereco.class));

     String resultado = controller.codastrar(fornecedor, errors, endereco, telefone, redirectAttributes);

      assertEquals("redirect:/fornecedor/form", resultado); 
    }

    @Test
    public void testCadastrarErroNoTelefoneAumentaCobertura() throws Exception {
        when(errors.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("falha telefone")).when(telefones).cadastrar(any(Telefone.class));

     String resultado = controller.codastrar(fornecedor, errors, endereco, telefone, redirectAttributes);

     assertEquals("redirect:/fornecedor/form", resultado);
    }
    
    @Test
    public void testCadastrarErroNoFornecedorServiceAumentaCobertura() throws Exception {
        when(errors.hasErrors()).thenReturn(false);
     when(fornecedores.cadastrar(any(Fornecedor.class))).thenThrow(new RuntimeException("falha fornecedor"));

    String resultado = controller.codastrar(fornecedor, errors, endereco, telefone, redirectAttributes);

     assertEquals("redirect:/fornecedor/form", resultado); 
}


}
