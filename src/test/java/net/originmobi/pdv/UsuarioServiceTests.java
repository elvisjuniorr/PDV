package net.originmobi.pdv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.originmobi.pdv.model.GrupoUsuario;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.UsuarioRepository;
import net.originmobi.pdv.service.GrupoUsuarioService;
import net.originmobi.pdv.service.UsuarioService;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTests {

    @Mock
    private UsuarioRepository usuarios;

    @Mock
    private GrupoUsuarioService grupos;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private GrupoUsuario grupoUsuario;
    private LocalDate dataAtual;

    @Before
    public void setUp() {
        dataAtual = LocalDate.now();

        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(1L);
        pessoa.setNome("Pessoa Teste");

        usuario = new Usuario();
        usuario.setCodigo(1L);
        usuario.setUser("teste");
        usuario.setSenha("senha123");
        usuario.setData_cadastro(Date.valueOf(dataAtual));
        usuario.setGrupoUsuario(new ArrayList<>());
        usuario.setPessoa(pessoa);

        grupoUsuario = new GrupoUsuario();
        grupoUsuario.setCodigo(1L);
        grupoUsuario.setNome("Administrador");
    }

    // TESTES DE ADICIONAR GRUPO
    
    @Test
    public void testAddGrupoComSucesso() {
        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1L)).thenReturn(grupoUsuario);

        String resultado = usuarioService.addGrupo(1L, 1L);

        assertEquals("ok", resultado);
        assertTrue(usuario.getGrupoUsuario().contains(grupoUsuario));
        verify(usuarios).save(usuario);
    }

    @Test
    public void testAddGrupoNull() {
        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1L)).thenReturn(null);

        String resultado = usuarioService.addGrupo(1L, 1L);

        assertFalse(usuario.getGrupoUsuario().contains(null));
        assertTrue(!resultado.equals("ok"));
    }
    
    @Test
    public void testAddGrupoUsuarioNull() {
        when(usuarios.findByCodigoIn(1L)).thenReturn(null);

        assertThrows(NullPointerException.class, () ->
            usuarioService.addGrupo(1L, 1L)
        );
    }

    @Test
    public void testAddGrupoJaExistente() {
        usuario.getGrupoUsuario().add(grupoUsuario);

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1L)).thenReturn(grupoUsuario);

        String resultado = usuarioService.addGrupo(1L, 1L);

        assertEquals("ja existe", resultado);
        verify(usuarios, never()).save(any(Usuario.class));
    }

    // TESTES DE CADASTRAR USUARIO

    @Test
    public void testCadastrarNovoUsuarioComSucesso() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(10L);

        Usuario novoUsuario = new Usuario();
        novoUsuario.setCodigo(null);
        novoUsuario.setUser("novoUser");
        novoUsuario.setSenha("123");
        novoUsuario.setPessoa(pessoa);

        when(usuarios.findByUserEquals("novoUser")).thenReturn(null);
        when(usuarios.findByPessoaCodigoEquals(10L)).thenReturn(null);
        when(usuarios.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioSalvo = invocation.getArgument(0);
            assertNotNull(usuarioSalvo.getData_cadastro());
            return usuarioSalvo;
        });

        String resultado = usuarioService.cadastrar(novoUsuario);

        assertEquals("Usuário salvo com sucesso", resultado);
        verify(usuarios).save(novoUsuario);
    }

    @Test
    public void testCadastrarSenhaSetada() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(11L);

        Usuario novoUsuario = new Usuario();
        novoUsuario.setCodigo(null);
        novoUsuario.setUser("novoUserSenha");
        novoUsuario.setSenha("senha123");
        novoUsuario.setPessoa(pessoa);

        when(usuarios.findByUserEquals("novoUserSenha")).thenReturn(null);
        when(usuarios.findByPessoaCodigoEquals(11L)).thenReturn(null);
        when(usuarios.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioSalvo = invocation.getArgument(0);
            assertEquals("senha123", usuarioSalvo.getSenha());
            return usuarioSalvo;
        });

        String resultado = usuarioService.cadastrar(novoUsuario);

        assertEquals("Usuário salvo com sucesso", resultado);
    }

    @Test
    public void testCadastrarUsuarioJaExistente() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(10L);

        Usuario existente = new Usuario();
        existente.setCodigo(1L);
        existente.setUser("joao");

        Usuario novoUsuario = new Usuario();
        novoUsuario.setCodigo(null);
        novoUsuario.setUser("joao");
        novoUsuario.setSenha("123");
        novoUsuario.setPessoa(pessoa);

        when(usuarios.findByUserEquals("joao")).thenReturn(existente);

        String resultado = usuarioService.cadastrar(novoUsuario);

        assertEquals("Usuário já existe", resultado);
        verify(usuarios, never()).save(any());
    }

    @Test
    public void testCadastrarPessoaJaVinculada() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(10L);

        Usuario usuarioPessoaJaUsada = new Usuario();
        usuarioPessoaJaUsada.setCodigo(2L);

        Usuario novoUsuario = new Usuario();
        novoUsuario.setCodigo(null);
        novoUsuario.setUser("novoUser");
        novoUsuario.setSenha("123");
        novoUsuario.setPessoa(pessoa);

        when(usuarios.findByUserEquals("novoUser")).thenReturn(null);
        when(usuarios.findByPessoaCodigoEquals(10L)).thenReturn(usuarioPessoaJaUsada);

        String resultado = usuarioService.cadastrar(novoUsuario);

        assertEquals("Pessoa já vinculada a outro usuário", resultado);
        verify(usuarios, never()).save(any());
    }

    @Test
    public void testAtualizarUsuarioComSucesso() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setCodigo(1L);
        usuarioExistente.setUser("joao");
        usuarioExistente.setSenha("123");

        when(usuarios.save(usuarioExistente)).thenReturn(usuarioExistente);

        String resultado = usuarioService.cadastrar(usuarioExistente);

        assertEquals("Usuário atualizado com sucesso", resultado);
        verify(usuarios).save(usuarioExistente);
    }

    @Test
    public void testAtualizarUsuarioComErro() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setCodigo(1L);
        usuarioExistente.setUser("joao");
        usuarioExistente.setSenha("123");

        when(usuarios.save(usuarioExistente))
            .thenThrow(new RuntimeException("Erro de banco"));

        String resultado = usuarioService.cadastrar(usuarioExistente);

        assertEquals("Erro de banco", resultado);
        verify(usuarios).save(usuarioExistente);
    }

    // TESTE DE BUSCAR USUÁRIO

    @Test
    public void testBuscaUsuarioExistente() {
        when(usuarios.findByUserEquals("teste")).thenReturn(usuario);

        Usuario resultado = usuarioService.buscaUsuario("teste");

        assertEquals(usuario, resultado);
        verify(usuarios).findByUserEquals("teste");
    }

    @Test
    public void testBuscaUsuarioNull() {
        when(usuarios.findByUserEquals(null)).thenReturn(null);

        Usuario resultado = usuarioService.buscaUsuario(null);

        assertNull(resultado);
        verify(usuarios).findByUserEquals(null);
    }

    // TESTE DE LISTAR USUÁRIOS

    @Test
    public void testListarUsuarios() {
        List<Usuario> listaUsuarios = Arrays.asList(usuario);
        when(usuarios.findAll()).thenReturn(listaUsuarios);

        List<Usuario> resultado = usuarioService.lista();

        assertEquals(1, resultado.size());
        assertEquals(usuario, resultado.get(0));
        verify(usuarios).findAll();
    }
    
    // TESTE DE REMOVER USUARIO
    
    @Test
    public void testRemoveGrupoSucesso() {
        List<GrupoUsuario> gruposUsuario = new ArrayList<>();
        gruposUsuario.add(grupoUsuario);

        usuario.getGrupoUsuario().add(grupoUsuario);

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1L)).thenReturn(grupoUsuario);
        when(grupos.buscaGrupos(usuario)).thenReturn(gruposUsuario);
        when(usuarios.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioSalvo = invocation.getArgument(0);
            assertFalse(usuarioSalvo.getGrupoUsuario().contains(grupoUsuario));
            return usuarioSalvo;
        });

        String resultado = usuarioService.removeGrupo(1L, 1L);

        assertEquals("ok", resultado);
        verify(usuarios).save(usuario);
    }
    
    @Test
    public void testRemoveGrupoGrupoNaoEncontrado() {
        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1L)).thenReturn(null);

        assertThrows(NullPointerException.class, () ->
            usuarioService.removeGrupo(1L, 1L)
        );

        verify(usuarios).findByCodigoIn(1L);
        verify(grupos).buscaGrupo(1L);
        verify(usuarios, never()).save(any());
    }
    
    @Test
    public void testRemoveGrupoUsuarioIdNull() {
        when(usuarios.findByCodigoIn(null)).thenReturn(null);

        assertThrows(NullPointerException.class, () ->
            usuarioService.removeGrupo(null, 1L)
        );

        verify(usuarios).findByCodigoIn(null);
        verify(grupos, never()).buscaGrupo(anyLong());
        verify(usuarios, never()).save(any());
    }
    
    @Test
    public void testRemoveGrupoGrupoIdNull() {
        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(null)).thenReturn(null);

        assertThrows(NullPointerException.class, () ->
            usuarioService.removeGrupo(1L, null)
        );

        verify(usuarios).findByCodigoIn(1L);
        verify(grupos).buscaGrupo(null);
        verify(usuarios, never()).save(any());
    }

    @Test
    public void testRemoveGrupoSetGrupoUsuarioChamado() {
        usuario.getGrupoUsuario().add(grupoUsuario);

        List<GrupoUsuario> gruposAntes = new ArrayList<>(usuario.getGrupoUsuario());

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1L)).thenReturn(grupoUsuario);
        when(grupos.buscaGrupos(usuario)).thenReturn(gruposAntes);
        when(usuarios.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioSalvo = invocation.getArgument(0);
            assertNotNull(usuarioSalvo.getGrupoUsuario());
            return usuarioSalvo;
        });

        String resultado = usuarioService.removeGrupo(1L, 1L);

        assertEquals("ok", resultado);
        verify(usuarios).save(usuario);
    }

    @Test
    public void testRemoveGrupoComListaVazia() {
        usuario.setGrupoUsuario(new ArrayList<>());

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1L)).thenReturn(grupoUsuario);
        when(grupos.buscaGrupos(usuario)).thenReturn(new ArrayList<>());

        String resultado = usuarioService.removeGrupo(1L, 1L);

        assertEquals("ok", resultado);
        verify(usuarios).save(usuario);
    }

    @Test
    public void testRemoveGrupoQuandoGrupoNaoExisteNaLista() {
        GrupoUsuario grupoDiferente = new GrupoUsuario();
        grupoDiferente.setCodigo(999L);
        usuario.getGrupoUsuario().add(grupoDiferente);

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1L)).thenReturn(grupoUsuario);
        when(grupos.buscaGrupos(usuario)).thenReturn(new ArrayList<>(usuario.getGrupoUsuario()));

        String resultado = usuarioService.removeGrupo(1L, 1L);

        assertEquals("ok", resultado);
        verify(usuarios).save(usuario);
    }
}