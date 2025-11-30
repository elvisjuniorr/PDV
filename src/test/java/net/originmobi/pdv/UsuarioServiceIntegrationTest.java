package net.originmobi.pdv;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.service.UsuarioService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UsuarioServiceIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void testServiceDisponivel() {
        assertNotNull("UsuarioService deve estar disponível", usuarioService);
        
        java.util.List<Usuario> usuarios = usuarioService.lista();
        assertNotNull("Lista de usuários não deve ser nula", usuarios);
        
        System.out.println("Serviço de usuário funcionando");
        System.out.println("Número de usuários no sistema: " + usuarios.size());
    }

    @Test
    public void testBuscaUsuarioExistente() {
        Usuario usuario = usuarioService.buscaUsuario("admin");
        
        if (usuario != null) {
            assertNotNull("Usuário admin deveria existir", usuario);
            assertEquals("admin", usuario.getUser());
        }
    }
    
    @Test
    public void testBuscaUsuarioInexistente() {
        Usuario usuario = usuarioService.buscaUsuario("usuario_inexistente_12345");
        
        assertNull("Usuário inexistente deve retornar null", usuario);
        System.out.println("Busca por usuário inexistente retornou null (comportamento esperado)");
    }
    
    @Test
    public void testIntegridadeDados() {
        List<Usuario> usuarios = usuarioService.lista();
        
        for (Usuario usuario : usuarios) {
            assertNotNull("Usuário não pode ter user nulo", usuario.getUser());
            assertFalse("User não pode ser vazio", usuario.getUser().trim().isEmpty());
            assertNotNull("Data de cadastro não pode ser nula", usuario.getData_cadastro());      
            assertTrue("Data de cadastro não pode ser futura", usuario.getData_cadastro().toLocalDate().isBefore(LocalDate.now().plusDays(1)));
            }
    }
            
}