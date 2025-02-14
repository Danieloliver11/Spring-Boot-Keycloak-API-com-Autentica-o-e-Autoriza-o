package br.com.curso.spring_keycloak.utils;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class HttpParamsMapBuilder {

	private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

	public static HttpParamsMapBuilder builder() {
		return new HttpParamsMapBuilder();
	}

	public HttpParamsMapBuilder setClient(String clientId) {
		params.add("client_id", clientId);
		return this;
	}
	
	public HttpParamsMapBuilder setSecret(String clientSecret) {
		params.add("client_secret", clientSecret);
		return this;
	}
	
	public HttpParamsMapBuilder setGrantType(String granrType) {
		params.add("grant_type", granrType);
		return this;
	}
	
	public HttpParamsMapBuilder setUserName(String userName) {
		params.add("username", userName);
		return this;
	}
	

	public HttpParamsMapBuilder setPassword(String password) {
		params.add("password", password);
		return this;
	}
	
	public HttpParamsMapBuilder setRefreshToken(String refreshToken) {
		params.add("refresh_token", refreshToken);
		return this;
	}
	
	public MultiValueMap<String, String> build(){
		
		return params;
	}


}
