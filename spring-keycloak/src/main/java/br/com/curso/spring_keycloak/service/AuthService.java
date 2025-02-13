package br.com.curso.spring_keycloak.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.curso.spring_keycloak.model.dto.UsuarioDTO;
import jakarta.validation.Valid;

@Service
public class AuthService {

	public ResponseEntity<?> login(@Valid UsuarioDTO usuarioDto) {
		// TODO Auto-generated method stub
		return null;
	}

}
