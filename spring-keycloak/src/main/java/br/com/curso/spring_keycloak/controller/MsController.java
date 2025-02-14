package br.com.curso.spring_keycloak.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class MsController {
	
	
	
	
	@GetMapping("/admin")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN_READ', 'ROLE_ADMIN_WRITE')")
	public String adminAccess() {
	    return "Acesso concedido ao ADMIN";
	}
	
	@GetMapping("/operational")
	@PreAuthorize("hasAnyAuthority('ROLE_OPERATION_READ', 'ROLE_OPERATION_WRITE')")
	public String operadorAcess() {
	    return "Acesso concedido ao OPERACIONAL";
	}
}
