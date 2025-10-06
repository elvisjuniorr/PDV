package net.originmobi.pdv.service;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.sql.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.Empresa;
import net.originmobi.pdv.model.EmpresaParametro;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.RegimeTributario;
import net.originmobi.pdv.repository.EmpresaRepository;
import net.originmobi.pdv.repository.EmpresaParametrosRepository;

@RunWith(MockitoJUnitRunner.class)
public class EmpresaServiceTest {

    @InjectMocks
    private EmpresaService empresaService;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaParametrosRepository parametroRepository;

    @Mock
    private RegimeTributarioService regimeTributarioService;

    @Mock
    private EnderecoService enderecoService;

    @Mock
    private CidadeService cidadeService;
    
    private Empresa empresa;
    
    @Before
    public void setUp() {

    }

    @Test
    public void cadastroComSucesso() {
        doNothing().when(empresaRepository.save(any(Empresa.class)));

        empresaService.cadastro(empresa);

        verify(empresaRepository, times(1)).save(any(Empresa.class));
    }

    @Test
    public void cadastroComErro() {
        // Simula que save lança exceção
        doThrow(new RuntimeException("Erro ao salvar empresa"))
            .when(empresaRepository).save(any(Empresa.class));

        empresaService.cadastro(empresa);

        // Verifica se o save foi chamado mesmo assim
        verify(empresaRepository, times(1)).save(empresa);
    }

    @Test
    public void verificaEmpresaCadastradaPresente() {
        when(empresaRepository.buscaEmpresaCadastrada()).thenReturn(Optional.of(empresa));

        Optional<Empresa> resultado = empresaService.verificaEmpresaCadastrada();
        assertTrue(resultado.isPresent());
        assertEquals("Empresa Nome", resultado.get().getNome());
        verify(empresaRepository, times(1)).buscaEmpresaCadastrada();
    }

    @Test
    public void verificaEmpresaCadastradaAusente() {
        when(empresaRepository.buscaEmpresaCadastrada()).thenReturn(Optional.empty());

        Optional<Empresa> resultado = empresaService.verificaEmpresaCadastrada();
        assertFalse(resultado.isPresent());
        verify(empresaRepository, times(1)).buscaEmpresaCadastrada();
    }

    @Test
    public void mergerCodigoDiferenteDeNull() {
        Long codigo = 1L;
        String nome = "Nome Empresa";
        String nome_fantasia = "Nome Fantasia";
        String cnpj = "12345678000195";
        String ie = "123456789";
        int serie = 9;
		int ambiente = 2;
        Long codRegime=2L;
        Long codendereco = 6L;
        Long codcidade =8L;
        String rua = "Rua Teste";
        String bairro = "Bairro Teste";
        String numero = "123";
        String cep = "12345000";
        String referencia = "Perto de algo";
        Double aliqCalcCredito = 80.2;

        doNothing().when(enderecoService).update(codendereco, codcidade, rua, bairro, numero, cep, referencia);

        doNothing().when(empresaRepository).update(codigo, nome, nome_fantasia, cnpj, ie, codRegime);
        doNothing().when(parametroRepository).update(serie, ambiente, aliqCalcCredito);


        String resultado = empresaService.merger(codigo, nome, nome_fantasia, cnpj, ie, serie, ambiente, codRegime, codendereco, codcidade, rua, bairro, numero, cep, referencia, aliqCalcCredito);
        
        assertEquals("Empresa salva com sucesso", resultado);

        verify(parametroRepository, times(1)).update(serie, ambiente, aliqCalcCredito);
        verify(empresaRepository, times(1)).update(codigo, nome, nome_fantasia, cnpj, ie, codRegime);
        verify(enderecoService, times(1)).update(codendereco, codcidade, rua, bairro, numero, cep, referencia);
    }

    @Test
    public void mergerCodigoIgualANull() {
        Long codigo = null;
        String nome = "Nome Empresa";
        String nome_fantasia = "Nome Fantasia";
        String cnpj = "12345678000195";
        String ie = "123456789";
        int serie = 9;
		int ambiente = 2;
        Long codRegime=2L;
        Long codendereco = 6L;
        Long codcidade =8L;
        String rua = "Rua Teste";
        String bairro = "Bairro Teste";
        String numero = "123";
        String cep = "12345000";
        String referencia = "Perto de algo";
        Double aliqCalcCredito = 80.2;

        EmpresaParametro parametro = new EmpresaParametro(); 
        parametro.setCodigo(codigo); 
        parametro.setSerie_nfe(serie); 
        parametro.setAmbiente(ambiente); 
        parametro.setpCredSN(aliqCalcCredito); 

        when(parametroRepository.save(parametro)).thenReturn(parametro); 

        Optional<RegimeTributario> regimeTributarioOpt = Optional.of(new RegimeTributario()); 
        regimeTributarioOpt.get().setCodigo(codRegime); 
        when(regimeTributarioService.busca(codRegime)).thenReturn(regimeTributarioOpt);

        Optional<Cidade> cidadeOpt = Optional.of(new Cidade()); 
        cidadeOpt.get().setCodigo(codcidade); 
        when(cidadeService.busca(codcidade)).thenReturn(cidadeOpt); 

        Endereco endereco = new Endereco(rua, bairro, numero, cep, referencia, Date.valueOf("2024-01-01"), cidadeOpt.get()); 
        when((enderecoService).cadastrar(endereco)).thenReturn(endereco); 

        Empresa empresa = new Empresa( nome, nome_fantasia, cnpj, ie, regimeTributarioOpt.get(), endereco, parametro ); 
        when(empresaRepository.save(empresa)).thenReturn(empresa);

        String resultado = empresaService.merger( codigo, nome, nome_fantasia, cnpj, ie, serie, ambiente, codRegime, codendereco, codcidade, rua, bairro, numero, cep, referencia, aliqCalcCredito ); 
        assertEquals("Empresa salva com sucesso", resultado); 
        verify(parametroRepository, times(1)).save(parametro);

    }
}
