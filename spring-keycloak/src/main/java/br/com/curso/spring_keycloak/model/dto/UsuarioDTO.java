package br.com.curso.spring_keycloak.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioDTO {
	
	@NotBlank(message = "nome do usuario é obrigatório")
	private String nome;
	
	@NotBlank(message = "senha do usuario é obrigatório")
	@Size(min = 4, max = 20, message = "senha deve ter entre 4 a 2o caracteres ")
	private String senha;

}
