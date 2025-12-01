package net.originmobi.pdv.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.originmobi.pdv.model.GrupoUsuario;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarios;
    private final GrupoUsuarioService grupos;

    public UsuarioService(UsuarioRepository usuarios, GrupoUsuarioService grupos) {
        this.usuarios = usuarios;
        this.grupos = grupos;
    }

    public String cadastrar(Usuario usuario) {
        
    	usuario.setData_cadastro(Date.valueOf(LocalDate.now()));
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setSenha(encoder.encode(usuario.getSenha()));

        if (usuario.getCodigo() == null) {
            Usuario usuarioExiste = usuarios.findByUserEquals(usuario.getUser());
            Usuario pessoaUsuario = usuarios.findByPessoaCodigoEquals(usuario.getPessoa().getCodigo());

            if (usuarioExiste != null) {
                return "Usuário já existe";
            } else if (pessoaUsuario != null) {
                return "Pessoa já vinculada a outro usuário";
            } else {
                usuarios.save(usuario);
                return "Usuário salvo com sucesso";
            }
        } else {
            try {
                usuarios.save(usuario);
                return "Usuário atualizado com sucesso";
            } catch (Exception e) {
                logger.error("Erro ao atualizar usuário", e);
                return "Erro ao atualizar: " + e.getMessage();
            }
        }
    }

    public List<Usuario> lista() {
        return usuarios.findAll();
    }

    public String addGrupo(Long codUsu, Long codGru) {
        Usuario usuario = usuarios.findByCodigoIn(codUsu);
        GrupoUsuario grupoParaAdicionar = grupos.buscaGrupo(codGru);

        if (!usuario.getGrupoUsuario().contains(grupoParaAdicionar)) {
            usuario.getGrupoUsuario().add(grupoParaAdicionar);
            usuarios.save(usuario);
            return "ok";
        } else {
            return "ja existe";
        }
    }

    public String removeGrupo(Long codUsu, Long codGru) {
        Usuario usuario = usuarios.findByCodigoIn(codUsu);
        
        List<GrupoUsuario> gruposDoUsuario = usuario.getGrupoUsuario();

        boolean removeu = gruposDoUsuario.removeIf(grupo -> Objects.equals(grupo.getCodigo(), codGru));

        if (removeu) {
            try {
                usuarios.save(usuario);
            } catch (Exception e) {
                logger.error("Erro ao remover grupo do usuário", e);
                return "erro";
            }
        }

        return "ok";
    }

    public Usuario buscaUsuario(String username) {
        return usuarios.findByUserEquals(username);
    }
}