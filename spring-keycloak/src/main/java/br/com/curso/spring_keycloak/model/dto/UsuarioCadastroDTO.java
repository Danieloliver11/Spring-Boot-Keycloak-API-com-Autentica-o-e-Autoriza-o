package br.com.curso.spring_keycloak.model.dto;

import lombok.Data;

@Data
public class UsuarioCadastroDTO {
	
	 private String username;
	    private String email;
	    private String firstName;
	    private String lastName;
	    private String password;

}
