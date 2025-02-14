package br.com.curso.spring_keycloak.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.curso.spring_keycloak.model.dto.UsuarioCadastroDTO;
import br.com.curso.spring_keycloak.model.dto.UsuarioDTO;
import br.com.curso.spring_keycloak.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class AuthController {
	
	
	private final AuthService authService;
	
	 @PostMapping("/salvar-usuario")
	    public ResponseEntity<String> criarUsuario(@RequestBody UsuarioCadastroDTO usuarioDTO) {
	        return authService.criarUsuario(usuarioDTO);
	    }
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody UsuarioDTO usuarioDto) {		
		return ResponseEntity.ok(authService.login(usuarioDto));
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestParam("refresh_token") String refreshToken) {		
		return ResponseEntity.ok(authService.refreshToken(refreshToken));
	}

}
