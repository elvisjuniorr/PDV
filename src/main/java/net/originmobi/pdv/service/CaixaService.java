package net.originmobi.pdv.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.filter.BancoFilter;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.CaixaRepository;
import net.originmobi.pdv.singleton.Aplicacao;

@Service
public class CaixaService {

    private static final Logger logger = LoggerFactory.getLogger(CaixaService.class);

    private final CaixaRepository caixas;
    private final UsuarioService usuarios;
    private final CaixaLancamentoService lancamentos;

    // Injeção via construtor
    public CaixaService(CaixaRepository caixas, UsuarioService usuarios, CaixaLancamentoService lancamentos) {
        this.caixas = caixas;
        this.usuarios = usuarios;
        this.lancamentos = lancamentos;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Long cadastro(Caixa caixa) {
        validarCaixa(caixa);

        Double vlAbertura = caixa.getValor_abertura() == null ? 0.0 : caixa.getValor_abertura();
        caixa.setValor_abertura(vlAbertura);

        if (caixa.getValor_abertura() < 0) {
            throw new IllegalArgumentException("Valor informado é inválido");
        }

        Aplicacao aplicacao = Aplicacao.getInstancia();
        Usuario usuarioAtual = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());

        caixa.setDescricao(definirDescricao(caixa));
        caixa.setUsuario(usuarioAtual);
        caixa.setData_cadastro(Date.valueOf(LocalDate.now()));

        if (CaixaTipo.BANCO.equals(caixa.getTipo())) {
            sanitizarDadosBanco(caixa);
        }

        try {
            caixas.save(caixa);
        } catch (Exception e) {
            throw new IllegalStateException("Erro no processo de abertura, chame o suporte técnico", e);
        }

        if (caixa.getValor_abertura() > 0) {
            processarLancamentoInicial(caixa, usuarioAtual);
        } else {
            caixa.setValor_total(0.0);
        }

        return caixa.getCodigo();
    }

    private void validarCaixa(Caixa caixa) {
        if (CaixaTipo.CAIXA.equals(caixa.getTipo()) && caixaIsAberto()) {
            throw new IllegalStateException("Existe caixa de dias anteriores em aberto, favor verifique");
        }
    }

    private String definirDescricao(Caixa caixa) {
        if (caixa.getDescricao() != null && !caixa.getDescricao().isEmpty()) {
            return caixa.getDescricao();
        }
        
        if (CaixaTipo.CAIXA.equals(caixa.getTipo())) return "Caixa diário";
        if (CaixaTipo.COFRE.equals(caixa.getTipo())) return "Cofre";
        if (CaixaTipo.BANCO.equals(caixa.getTipo())) return "Banco";
        
        return "Caixa";
    }

    private void sanitizarDadosBanco(Caixa caixa) {
        logger.debug("Sanitizando dados bancários: Agência {}, Conta {}", caixa.getAgencia(), caixa.getConta());
        if (caixa.getAgencia() != null) {
            caixa.setAgencia(caixa.getAgencia().replaceAll("\\D", ""));
        }
        if (caixa.getConta() != null) {
            caixa.setConta(caixa.getConta().replaceAll("\\D", ""));
        }
    }

    private void processarLancamentoInicial(Caixa caixa, Usuario usuario) {
        try {
            // CORREÇÃO AQUI: Substituído o switch moderno por if/else clássico
            String observacao = "Abertura";

            if (CaixaTipo.CAIXA.equals(caixa.getTipo())) {
                observacao = "Abertura de caixa";
            } else if (CaixaTipo.COFRE.equals(caixa.getTipo())) {
                observacao = "Abertura de cofre";
            } else if (CaixaTipo.BANCO.equals(caixa.getTipo())) {
                observacao = "Abertura de banco";
            }

            CaixaLancamento lancamento = new CaixaLancamento(observacao, caixa.getValor_abertura(),
                    TipoLancamento.SALDOINICIAL, EstiloLancamento.ENTRADA, caixa, usuario);

            lancamentos.lancamento(lancamento);
        } catch (Exception e) {
            throw new IllegalStateException("Erro no processo, chame o suporte", e);
        }
    }

    public String fechaCaixa(Long idCaixa, String senha) {
        if (senha == null || senha.isEmpty()) {
            return "Favor, informe a senha";
        }

        Aplicacao aplicacao = Aplicacao.getInstancia();
        Usuario usuario = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(senha, usuario.getSenha())) {
            return "Senha incorreta, favor verifique";
        }

        Caixa caixaAtual = caixas.findById(idCaixa)
            .orElseThrow(() -> new IllegalArgumentException("Caixa não encontrado"));

        if (caixaAtual.getData_fechamento() != null) {
            throw new IllegalStateException("Caixa já esta fechado");
        }

        Double valorTotal = caixaAtual.getValor_total() != null ? caixaAtual.getValor_total() : 0.0;

        caixaAtual.setData_fechamento(new Timestamp(System.currentTimeMillis()));
        caixaAtual.setValor_fechamento(valorTotal);

        try {
            caixas.save(caixaAtual);
        } catch (Exception e) {
            throw new IllegalStateException("Ocorreu um erro ao fechar o caixa, chame o suporte", e);
        }

        return "Caixa fechado com sucesso";
    }

    public boolean caixaIsAberto() {
        return caixas.caixaAberto().isPresent();
    }

    public List<Caixa> listaTodos() {
        return caixas.findByCodigoOrdenado();
    }

    public List<Caixa> listarCaixas(CaixaFilter filter) {
        if (filter.getData_cadastro() != null && !filter.getData_cadastro().isEmpty()) {
            String dataFormatada = filter.getData_cadastro().replace("/", "-");
            return caixas.buscaCaixasPorDataAbertura(Date.valueOf(dataFormatada));
        }
        return caixas.listaCaixasAbertos();
    }

    public Optional<Caixa> caixaAberto() {
        return caixas.caixaAberto();
    }

    public List<Caixa> caixasAbertos() {
        return caixas.caixasAbertos();
    }

    public Optional<Caixa> busca(Long codigo) {
        return caixas.findById(codigo);
    }

    public Optional<Caixa> buscaCaixaUsuario(String loginUsuario) {
        Usuario usu = usuarios.buscaUsuario(loginUsuario);
        return Optional.ofNullable(caixas.findByCaixaAbertoUsuario(usu.getCodigo()));
    }

    public List<Caixa> listaBancos() {
        return caixas.buscaBancos(CaixaTipo.BANCO);
    }

    public List<Caixa> listaCaixasAbertosTipo(CaixaTipo tipo) {
        return caixas.buscaCaixaTipo(tipo);
    }

    public List<Caixa> listaBancosAbertosTipoFilterBanco(CaixaTipo tipo, BancoFilter filter) {
        if (filter.getData_cadastro() != null && !filter.getData_cadastro().isEmpty()) {
            String dataFormatada = filter.getData_cadastro().replace("/", "-");
            return caixas.buscaCaixaTipoData(tipo, Date.valueOf(dataFormatada));
        }
        return caixas.buscaCaixaTipo(CaixaTipo.BANCO);
    }
}