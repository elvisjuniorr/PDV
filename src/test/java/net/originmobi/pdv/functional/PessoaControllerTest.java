package net.originmobi.pdv.functional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import net.originmobi.pdv.controller.PessoaController;
import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Telefone;
import net.originmobi.pdv.service.CidadeService;
import net.originmobi.pdv.service.EnderecoService;
import net.originmobi.pdv.service.PessoaService;
import net.originmobi.pdv.service.TelefoneService;

@RunWith(SpringRunner.class)
@WebMvcTest(PessoaController.class)
@WithMockUser(username = "gerente", roles = "ADMINISTRADOR")

public class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private PessoaService pessoas;
    @MockBean private CidadeService cidades;
    @MockBean private EnderecoService enderecos;
    @MockBean private TelefoneService telefones;

    private Pessoa pessoa;
    private Cidade cidade;
    private Endereco endereco;
    private Telefone telefone;

    @Before
    public void setup() {

        cidade = new Cidade();
        cidade.setCodigo(1L);
        cidade.setNome("São Paulo");

        telefone = new Telefone();
        telefone.setCodigo(1L);
        telefone.setFone("11988887777");

        endereco = new Endereco();
        endereco.setCodigo(1L);
        endereco.setRua("Av Paulista");

        pessoa = new Pessoa();
        pessoa.setCodigo(1L);
        pessoa.setNome("João Silva");
        pessoa.setCpfcnpj("12345678900");
        pessoa.setEndereco(endereco);
        pessoa.setTelefone(Arrays.asList(telefone));

        when(cidades.lista()).thenReturn(Arrays.asList(cidade));
    }

    @Test
    public void deveRetornarFormularioPessoa() throws Exception {
        mockMvc.perform(get("/pessoa/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("pessoa/form"))
                .andExpect(model().attributeExists("pessoa"))
                .andExpect(model().attributeExists("endereco"))
                .andExpect(model().attributeExists("telefone"));
    }

    @Test
    public void deveListarPessoas() throws Exception {
        when(pessoas.filter(any())).thenReturn(Arrays.asList(pessoa));

        mockMvc.perform(get("/pessoa"))
                .andExpect(status().isOk())
                .andExpect(view().name("pessoa/list"))
                .andExpect(model().attributeExists("pessoas"));
    }

 
    @Test
    public void deveCadastrarPessoaComSucesso() throws Exception {

        when(pessoas.cadastrar(anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyLong(), anyLong(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyLong(), anyString(), anyString(), any()))
                .thenReturn("Cadastro concluído");

        mockMvc.perform(post("/pessoa")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nome", "João Silva")
                .param("apelido", "João")
                .param("cpfcnpj", "12345678900")
                .param("data_nascimento", "1990-01-01")
                .param("observacao", "Cliente novo")
                .param("cidade", "1")
                .param("rua", "Av Teste")
                .param("bairro", "Centro")
                .param("numero", "100")
                .param("cep", "01000000")
                .param("referencia", "Próximo ao metrô")
                .param("fone", "11999998888")
                .param("tipo", "CELULAR"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cadastro concluído"));
    }

    /**
 * ATENÇÃO:
 * O teste pode falhar com erro 500 se o controller não for adaptado para receber Long ao invés de Pessoa.
 * Alternativa: usar a versão comentada em PessoaController.java de public ModelAndView edite(@PathVariable("codigo") Pessoa pessoa)...
 */

    @Test
    public void deveEditarPessoa() throws Exception {
        when(pessoas.busca(1L)).thenReturn(pessoa);           
        when(enderecos.enderecoCodigo(1L)).thenReturn(endereco);
        when(telefones.telefoneCodigo(1L)).thenReturn(telefone);

        mockMvc.perform(get("/pessoa/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pessoa/form"))
                .andExpect(model().attributeExists("pessoa"))
                .andExpect(model().attributeExists("endereco"))
                .andExpect(model().attributeExists("telefone"));
    }


    @Test
    public void deveBuscarPessoaPorId() throws Exception {
        when(pessoas.busca(1L)).thenReturn(pessoa);

        mockMvc.perform(put("/pessoa/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpfcnpj").value("12345678900"));
    }
}