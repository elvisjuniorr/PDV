package net.originmobi.pdv.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.Empresa;
import net.originmobi.pdv.model.EmpresaParametro;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.RegimeTributario;
import net.originmobi.pdv.repository.EmpresaParametrosRepository;
import net.originmobi.pdv.repository.EmpresaRepository;

@Service
public class EmpresaService {

    private static final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private static final String MSG_ERRO_SALVAR = "Erro ao salvar dados da empresa, chame o suporte";

    private final EmpresaRepository empresas;
    private final EmpresaParametrosRepository parametros;
    private final RegimeTributarioService regimes;
    private final CidadeService cidades;
    private final EnderecoService enderecos;

    public EmpresaService(EmpresaRepository empresas, EmpresaParametrosRepository parametros,
                          RegimeTributarioService regimes, CidadeService cidades, EnderecoService enderecos) {
        this.empresas = empresas;
        this.parametros = parametros;
        this.regimes = regimes;
        this.cidades = cidades;
        this.enderecos = enderecos;
    }

    public void cadastro(Empresa empresa) {
        try {
            empresas.save(empresa);
        } catch (Exception e) {
            logger.error("Erro ao cadastrar empresa", e);
        }
    }

    public Optional<Empresa> verificaEmpresaCadastrada() {
        return empresas.buscaEmpresaCadastrada();
    }

    @SuppressWarnings("java:S107") 
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String merger(Long codigo, String nome, String nomeFantasia, String cnpj, String ie, int serie,
            int ambiente, Long codRegime, Long codEndereco, Long codCidade, String rua, String bairro, String numero,
            String cep, String referencia, Double aliqCalcCredito) {

        try {
            if (codigo != null) {
                return atualizarEmpresaExistente(codigo, nome, nomeFantasia, cnpj, ie, serie, ambiente, codRegime,
                        codEndereco, codCidade, rua, bairro, numero, cep, referencia, aliqCalcCredito);
            } else {
                return cadastrarNovaEmpresa(nome, nomeFantasia, cnpj, ie, serie, ambiente, codRegime, codCidade, rua,
                        bairro, numero, cep, referencia, aliqCalcCredito);
            }
        } catch (Exception e) {
            return MSG_ERRO_SALVAR;
        }
    }
    
    @SuppressWarnings("java:S107")
    private String atualizarEmpresaExistente(Long codigo, String nome, String nomeFantasia, String cnpj, String ie,
            int serie, int ambiente, Long codRegime, Long codEndereco, Long codCidade, String rua, String bairro,
            String numero, String cep, String referencia, Double aliqCalcCredito) {
        
        try {
            empresas.update(codigo, nome, nomeFantasia, cnpj, ie, codRegime);
            parametros.update(serie, ambiente, aliqCalcCredito);
            enderecos.update(codEndereco, codCidade, rua, bairro, numero, cep, referencia);
            return "Empresa salva com sucesso";
        } catch (Exception e) {
            throw new IllegalStateException(MSG_ERRO_SALVAR, e);
        }
    }

    @SuppressWarnings("java:S107")
    private String cadastrarNovaEmpresa(String nome, String nomeFantasia, String cnpj, String ie, int serie,
            int ambiente, Long codRegime, Long codCidade, String rua, String bairro, String numero, String cep,
            String referencia, Double aliqCalcCredito) {

        try {
            EmpresaParametro parametro = new EmpresaParametro();
            parametro.setAmbiente(ambiente);
            parametro.setSerie_nfe(serie);
            parametro.setpCredSN(aliqCalcCredito);
            parametros.save(parametro);

            RegimeTributario tributario = regimes.busca(codRegime)
                    .orElseThrow(() -> new IllegalArgumentException("Regime Tributário não encontrado"));

            Cidade cidade = cidades.busca(codCidade)
                    .orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada"));

            Endereco endereco = new Endereco(rua, bairro, numero, cep, referencia, Date.valueOf(LocalDate.now()), cidade);
            enderecos.cadastrar(endereco);

            Empresa empresa = new Empresa(nome, nomeFantasia, cnpj, ie, tributario, endereco, parametro);
            empresas.save(empresa);

            return "Empresa salva com sucesso";

        } catch (Exception e) {
            throw new IllegalStateException(MSG_ERRO_SALVAR, e);
        }
    }
}