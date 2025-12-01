package net.originmobi.pdv.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.repository.ProdutoRepository;

import java.util.ArrayList;
import java.util.List;

public class ProdutoServiceTest {
     @Mock
    private ProdutoRepository produtos;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    public void testListarVazia() {
      List<Produto> listaMock = new ArrayList<>();
          when(produtos.findAll()).thenReturn(listaMock); List<Produto> resultado = produtoService.listar();

    }
}
