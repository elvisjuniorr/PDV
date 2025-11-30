package net.originmobi.pdv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import net.originmobi.pdv.model.GrupoUsuario;
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

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private GrupoUsuario grupoUsuario;
    private LocalDate dataAtual;

    @Before
    public void setUp() {
        dataAtual = LocalDate.now();
        
        usuario = new Usuario();
        usuario.setCodigo(null);
        usuario.setUser("teste");
        usuario.setSenha("senha123");
        usuario.setData_cadastro(Date.valueOf(dataAtual));
        usuario.setGrupoUsuario(new ArrayList<>());
        
        grupoUsuario = new GrupoUsuario();
        grupoUsuario.setCodigo(1L);
        grupoUsuario.setNome("Administrador");
    }   

    @Test
    public void testAddGrupoJaExistente() {
        usuario.getGrupoUsuario().add(grupoUsuario);
        
        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1L)).thenReturn(grupoUsuario);

        String resultado = usuarioService.addGrupo(1L, 1L);

        assertEquals("ja existe", resultado);
        assertEquals(1, usuario.getGrupoUsuario().size());
        verify(usuarios, never()).save(any(Usuario.class));
        verify(usuarios, times(1)).findByCodigoIn(1L);
        verify(grupos, times(1)).buscaGrupo(1L);
    }

    @Test
    public void testAddGrupoUsuarioNaoEncontrado() {
        when(usuarios.findByCodigoIn(1L)).thenReturn(null);

        try {
            usuarioService.addGrupo(1L, 1L);
            assertTrue("Deveria ter lançado NullPointerException", false);
        } catch (NullPointerException e) {
        }

        verify(usuarios, times(1)).findByCodigoIn(1L);
        verify(grupos, never()).buscaGrupo(anyLong());
        verify(usuarios, never()).save(any(Usuario.class));
    }

    @Test
    public void testAddGrupoGrupoNaoEncontrado() {
        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1L)).thenReturn(null);

        try {
            usuarioService.addGrupo(1L, 1L);
            assertTrue("Deveria ter lançado NullPointerException", false);
        } catch (NullPointerException e) {
        }

        verify(usuarios, times(1)).findByCodigoIn(1L);
        verify(grupos, times(1)).buscaGrupo(1L);
        verify(usuarios, never()).save(any(Usuario.class));
    }
}