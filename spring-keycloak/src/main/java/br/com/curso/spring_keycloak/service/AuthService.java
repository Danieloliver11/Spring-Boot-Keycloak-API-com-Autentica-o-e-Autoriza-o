package br.com.curso.spring_keycloak.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import br.com.curso.spring_keycloak.component.HttpComponent;
import br.com.curso.spring_keycloak.model.dto.UsuarioCadastroDTO;
import br.com.curso.spring_keycloak.model.dto.UsuarioDTO;
import br.com.curso.spring_keycloak.utils.HttpParamsMapBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	@Value("${keycloak.aut-server-url}")
	private String keycloakServerUrl;
	
	
	@Value("${keycloak.realm}")
	private String realm;
	
	@Value("${keycloak.client}")
	private String client;
	
	@Value("${keycloak.resource}")
	private String clientId;
	
	@Value("${keycloak.credentials.secret}")
	private String clienteSecret;
	
	@Value("${keycloak.user-login.grant-type}")
	private String grantType;
	
	private final HttpComponent httpComponent;
	
    private final RestTemplate restTemplate = new RestTemplate();


	public ResponseEntity<?> login(@Valid UsuarioDTO usuarioDto) {
		
		httpComponent.httpHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		
		
		 MultiValueMap<String, String> params = HttpParamsMapBuilder.builder()
		.setClient(clientId)
		.setSecret(clienteSecret)
		.setGrantType(grantType)
//		.setRefreshToken(clientId)
		.setUserName(usuarioDto.getNome())
		.setPassword(usuarioDto.getSenha())
		.build();
		 
			return respostaKeycloakClient(params);

		}
	

	public ResponseEntity<?> refreshToken(@Valid String refreshToken) {
		
		httpComponent.httpHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		 MultiValueMap<String, String> params = HttpParamsMapBuilder.builder()
		.setClient(clientId)
		.setSecret(clienteSecret)
		.setGrantType("refresh_token")
		.setRefreshToken(refreshToken)
		.build();
		 
			return respostaKeycloakClient(params);

		}
	
	
	//------------------
	
    public ResponseEntity<String> criarUsuario(UsuarioCadastroDTO usuario) {
    	//http://<keycloak-host>/admin/realms/<realm>/users

    	String url = "http://localhost:8080/auth/admin/realms/REALM_SPRING_API/users";
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(obterTokenadminCli());

        Map<String, Object> userMap = Map.of(
            "username", usuario.getUsername(),
            "email", usuario.getEmail(),
            "firstName", usuario.getFirstName(),
            "lastName", usuario.getLastName(),
            "enabled", true,
            "credentials", List.of(Map.of("type", "password", "value", usuario.getPassword(), "temporary", false))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userMap, headers);

        ResponseEntity<String> response = null;
       
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

		} catch (HttpClientErrorException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}

        if (response.getStatusCode().is2xxSuccessful()) {
            adicionarGrupo(usuario.getUsername(), "operation");
            adicionarRoles(usuario.getUsername(), List.of("OPERATION_WRITE", "OPERATION_READ"));
        }

        return response;
    }
    
    private String obterTokenadminCli() {
        // versões mais antigas (< 18), a URL pode ser: com  /auth antes de /realms,
        String tokenUrl = "http://localhost:8080/auth/realms/master/protocol/openid-connect/token"; // 

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Construindo os parâmetros no formato correto
        String requestBody = "client_id=admin-cli&username=admin&password=admin&grant_type=password";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("access_token")) {
            return response.getBody().get("access_token").toString();
        }

        throw new RuntimeException("Falha ao obter token de admin do Keycloak");
    }	
    
    private String obterTokenAdmin() { 
        String tokenUrl = "http://localhost:8080/auth/realms/REALM_SPRING_API/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String clientId = "CLIENT_SPRING";
        String clientSecret = "yGGqbr8JQvupj9MRc209Fx3pYhX05vsB"; // Insira o client secret correto

        String requestBody = "client_id=" + clientId +
                             "&client_secret=" + clientSecret +
                             "&grant_type=client_credentials";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("access_token")) {
            return response.getBody().get("access_token").toString();
        }

        throw new RuntimeException("Falha ao obter token de admin do Keycloak");
    }
    
    private void adicionarGrupo(String username, String groupName) {
        String userId = buscarUserId(username);
        String groupId = buscarGroupId(groupName);

        String url = "http://localhost:8080/auth/" + "/admin/realms/" + realm + "/users/" + userId + "/groups/" + groupId;
      
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(obterTokenadminCli());

        restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(headers), String.class);
    }
    //TODO TA DANDO ERRO 
    private void adicionarRoles(String username, List<String> roles) {
        String userId = buscarUserId(username);
        
        String clientId = buscarClientId("CLIENT_SPRING"); // Buscar UUID correto do client

        
//        String url = "http://localhost:8080/auth/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
        
        String url = "http://localhost:8080/auth/admin/realms/" + realm +
                "/users/" + userId + "/role-mappings/clients/" + "CLIENT_SPRING";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(obterTokenadminCli());
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, String>> roleMappings = roles.stream()
            .map(role -> Map.of("name", role))
            .toList();

        HttpEntity<List<Map<String, String>>> request = new HttpEntity<>(roleMappings, headers);
        restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }
    
    
    
    private String buscarClientId(String clientName) {
        String url = "http://localhost:8080/auth/admin/realms/" + realm + "/clients?clientId=" + clientName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(obterTokenadminCli());

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            url, HttpMethod.GET, new HttpEntity<>(headers), (Class<List<Map<String, Object>>>) (Object) List.class
        );

        if (response.getBody() != null && !response.getBody().isEmpty()) {
            return response.getBody().get(0).get("id").toString();  // Retorna o UUID do Client
        }

        throw new RuntimeException("Client ID não encontrado para: " + clientName);
    }
    
    private String buscarUserId(String username) {
      
    	
    	String url = "http://localhost:8080/auth/admin/realms/" + realm + "/users?username=" + username;
     
    	HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(obterTokenadminCli());
    
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), List.class);
        return ((Map<String, String>) response.getBody().get(0)).get("id");
    }

    

    private String buscarGroupId(String groupName) {
        String url = "http://localhost:8080/auth" + "/admin/realms/" + realm + "/groups";
       
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(obterTokenadminCli());

        ResponseEntity<List> responseList = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), List.class);

        if (responseList.getBody() == null || responseList.getBody().isEmpty()) {
            throw new RuntimeException("Nenhum grupo encontrado no Keycloak.");
        }

        for (Object obj : responseList.getBody()) {
            if (obj instanceof Map) { // Verifica se o objeto é um Map
                Map<String, Object> group = (Map<String, Object>) obj;
                if (groupName.equals(group.get("name"))) {
                    return group.get("id").toString(); // Converte ID para String de forma segura
                }
            }
        }

        throw new RuntimeException("Grupo não encontrado: " + groupName);
    }
	
	
//	private String buscarGroupId(String groupName) {
//	    String url = keycloakServerUrl + "/admin/realms/" + realm + "/groups";
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setBearerAuth(obterTokenAdmin());
//
//	    ParameterizedTypeReference<List<Map<String, Object>>> responseType = new ParameterizedTypeReference<>() {};
//	    ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), responseType);
//
//	    if (response.getBody() == null || response.getBody().isEmpty()) {
//	        throw new RuntimeException("Nenhum grupo encontrado no Keycloak.");
//	    }
//
//	    return response.getBody().stream()
//	        .filter(group -> {
//	            Object nameObj = group.get("name");
//	            return nameObj instanceof String && nameObj.equals(groupName);
//	        })
//	        .map(group -> {
//	            Object idObj = group.get("id");
//	            if (idObj instanceof String) {
//	                return (String) idObj;
//	            } else {
//	                throw new RuntimeException("O ID do grupo não é uma string válida: " + idObj);
//	            }
//	        })
//	        .findFirst()
//	        .orElseThrow(() -> new RuntimeException("Grupo não encontrado: " + groupName));
//	}
	
	
	//---------- 
	
	private ResponseEntity<?> respostaKeycloakClient(MultiValueMap<String, String> params) {
		try {

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params,
					httpComponent.httpHeaders());

			ResponseEntity<String> response = httpComponent.restTemplate()
					.postForEntity(keycloakServerUrl + "/protocol/openid-connect/token", request, String.class);

			return ResponseEntity.ok(response.getBody());
 
		} catch (HttpClientErrorException e) {

			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

		}
	}
		
	}
