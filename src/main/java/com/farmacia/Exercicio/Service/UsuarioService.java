package com.farmacia.Exercicio.Service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.farmacia.Exercicio.Model.UserLogin;
import com.farmacia.Exercicio.Model.UsuarioModel;
import com.farmacia.Exercicio.Repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;

	/*
	 * public UsuarioModel CadastrarUsuario(UsuarioModel usuario) {
	 * BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	 * 
	 * String senhaEncoder = encoder.encode(usuario.getSenhaUsuario());
	 * usuario.setSenhaUsuario(senhaEncoder);
	 * 
	 * return repository.save(usuario);
	 * 
	 * }
	 */

	public Optional<Object> CadastrarUsuario(UsuarioModel usuarioParaCadastrar) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return repository.findByEmailUsuario(usuarioParaCadastrar.getEmailUsuario()).map(usuarioExistente -> {
			return Optional.empty();
		}).orElseGet(() -> {
			usuarioParaCadastrar.setSenhaUsuario(encoder.encode(usuarioParaCadastrar.getSenhaUsuario()));
			return Optional.ofNullable(repository.save(usuarioParaCadastrar));
		});

	}

	public Optional<UserLogin> Logar(Optional<UserLogin> user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Optional<UsuarioModel> usuario = repository.findByEmailUsuario(user.get().getEmail());

		if (usuario.isPresent()) {
			if (encoder.matches(user.get().getSenha(), usuario.get().getSenhaUsuario())) {
				String auth = user.get().getEmail() + ":" + user.get().getSenha();
				byte[] encodeAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodeAuth);

				user.get().setToken(authHeader);
				user.get().setNome(usuario.get().getNomeUsuario());
				user.get().setId(usuario.get().getIdUsuario());
				user.get().setSenha(usuario.get().getSenhaUsuario());
				user.get().setEmail(usuario.get().getEmailUsuario());
				user.get().setSobrenome(usuario.get().getSobrenomeUsuario());

				return user;
			}
		}
		return null;
	}
}
