package net.originmobi.pdv.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.enumerado.produto.ProdutoControleEstoque;
import net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria;
import net.originmobi.pdv.filter.ProdutoFilter;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoService.class);

    private final ProdutoRepository produtos;
    private final VendaProdutoService vendaProdutos;

    public ProdutoService(ProdutoRepository produtos, VendaProdutoService vendaProdutos) {
        this.produtos = produtos;
        this.vendaProdutos = vendaProdutos;
    }

    public List<Produto> listar() {
        return produtos.findAll();
    }

    public List<Produto> listaProdutosVendaveis() {
        return produtos.produtosVendaveis();
    }

    public Produto busca(Long codigoProduto) {
        return produtos.findByCodigoIn(codigoProduto);
    }

    public Optional<Produto> buscaProduto(Long codigo) {
        return produtos.findById(codigo);
    }

    public Page<Produto> filter(ProdutoFilter filter, Pageable pageable) {
        String descricao = filter.getDescricao() == null ? "%" : filter.getDescricao();
        return produtos.findByDescricaoContaining(descricao, pageable);
    }

    @SuppressWarnings("java:S107")
    public String merger(Long codprod, Long codforne, Long codcategoria, Long codgrupo, int balanca, String descricao,
            Double valorCusto, Double valorVenda, java.util.Date dataValidade, String controleEstoque, String situacao,
            String unitario, ProdutoSubstTributaria subtribu, String ncm, String cest, Long tributacao, Long modbc,
            String vendavel) {

        LocalDate dataAtual = LocalDate.now();

        try {
            if (codprod == 0) {
                produtos.insere(codforne, codcategoria, codgrupo, balanca, descricao, valorCusto, valorVenda,
                        dataValidade, controleEstoque, situacao, unitario, subtribu.ordinal(), Date.valueOf(dataAtual),
                        ncm, cest, tributacao, modbc, vendavel);
                return "Produdo cadastrado com sucesso";
            } else {
                produtos.atualiza(codprod, codforne, codcategoria, codgrupo, balanca, descricao, valorCusto, valorVenda,
                        dataValidade, controleEstoque, situacao, unitario, subtribu.ordinal(), ncm, cest, tributacao,
                        modbc, vendavel);
                return "Produto atualizado com sucesso";
            }
        } catch (Exception e) {
            logger.error("Erro ao salvar/atualizar produto", e);
            return "Erro ao salvar produto, chame o suporte";
        }
    }

    @SuppressWarnings("static-access")
    public void movimentaEstoque(Long codvenda, EntradaSaida tipo) {
        List<Object[]> resultado = vendaProdutos.buscaQtdProduto(codvenda);

        for (Object[] item : resultado) {
            Long codprod = Long.decode(item[0].toString());
            int qtd = Integer.parseInt(item[1].toString());

            Produto produto = produtos.findByCodigoIn(codprod);

            if (ProdutoControleEstoque.SIM.equals(produto.getControla_estoque())) {
                processarBaixaEstoque(codprod, codvenda, qtd);
            } else {
                logger.info("Produto {} não controla estoque, ignorando movimentação.", codprod);
            }
        }
    }

    private void processarBaixaEstoque(Long codProd, Long codVenda, int qtd) {
        int qtdEstoque = produtos.saldoEstoque(codProd); 
        String origemOperacao = "Venda " + codVenda;    
        LocalDate dataAtual = LocalDate.now();           

        if (qtd <= qtdEstoque) {
            produtos.movimentaEstoque(codProd, EntradaSaida.SAIDA.toString(), qtd, origemOperacao,
                    Date.valueOf(dataAtual));
        } else {
            throw new IllegalStateException(
                    "O produto de código " + codProd + " não tem estoque suficiente, verifique");
        }
    }

    public void ajusteEstoque(Long codprod, int qtd, EntradaSaida tipo, String origemOperacao, Date dataMovimentacao) {
        Produto produto = produtos.findByCodigoIn(codprod);

        if (ProdutoControleEstoque.NAO.equals(produto.getControla_estoque())) {
            throw new IllegalStateException("O produto de código " + codprod + " não controla estoque, verifique");
        }

        produtos.movimentaEstoque(codprod, tipo.toString(), qtd, origemOperacao, dataMovimentacao);
    }
}