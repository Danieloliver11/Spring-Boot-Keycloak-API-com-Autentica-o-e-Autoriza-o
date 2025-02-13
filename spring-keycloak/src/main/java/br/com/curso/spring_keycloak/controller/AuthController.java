package br.com.curso.spring_keycloak.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.curso.spring_keycloak.model.dto.UsuarioDTO;
import br.com.curso.spring_keycloak.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody UsuarioDTO usuarioDto) {		
		return ResponseEntity.ok(authService.login(usuarioDto));
	}
	

}
