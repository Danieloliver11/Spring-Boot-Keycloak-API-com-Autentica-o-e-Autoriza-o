# Spring-Boot-Keycloak-API-com-Autentica-o-e-Autoriza-o

# Configuração do Keycloak para Spring Security

Este guia passo a passo ajudará a configurar o Keycloak para integração com uma aplicação Spring Security.

## 1. Criar o Realm

1. Acesse o Keycloak Admin.
2. No menu lateral, clique em **Realms** e depois em **Create Realm**.
3. No campo **Name**, insira `REALM_SPRING_API`.
4. Clique em **Save**.

## 2. Criar Usuários

1. No menu lateral, clique em **Users**.
2. Clique em **Add User** e crie dois usuários.
3. Após criar cada usuário, vá até a aba **Credentials**, defina uma senha e desative a opção "Temporary".

## 3. Criar Grupos

1. No menu lateral, clique em **Groups**.
2. Clique em **Create Group** e crie os seguintes grupos:
   - `admin`
   - `operation`
3. Clique em **Save** para cada grupo.

## 4. Associar Usuários aos Grupos

1. No menu lateral, clique em **Users**.
2. Selecione um dos usuários e vá até a aba **Groups**.
3. Associe um usuário ao grupo `admin` e outro ao grupo `operation`.
4. Clique em **Save**.

## 5. Criar Roles (Papéis)

1. No menu lateral, clique em **Roles**.
2. Crie as seguintes roles para administradores:
   - **Nome:** `ADMIN_WRITE`
     - **Descrição:** "Permissão para escrever tudo"
   - **Nome:** `ADMIN_READ`
     - **Descrição:** "Permissão para ler tudo"
3. Crie as seguintes roles para operadores:
   - **Nome:** `OPERATION_READ`
     - **Descrição:** "Apenas ler de operações"
   - **Nome:** `OPERATION_WRITE`
     - **Descrição:** "Permissão para escrever operações"

## 6. Associar Grupos às Roles

1. No menu lateral, clique em **Groups**.
2. Selecione o grupo `admin` e vá até a aba **Role Mappings**.
3. Clique em **Assign Role** e associe as roles `ADMIN_READ` e `ADMIN_WRITE`.
4. Faça o mesmo para o grupo `operation`, atribuindo `OPERATION_READ` e `OPERATION_WRITE`.

## 7. Criar o Cliente

1. No menu lateral, clique em **Clients**.
2. Clique em **Create Client**.
3. No campo **Client ID**, insira `CLIENT_SPRING` e clique em **Next**.
4. Habilite as opções:
   - **Client authentication:** `ON`
   - **Authorization:** `ON`
5. Configure os seguintes parâmetros:
   - **Name:** `${client_account}`
   - **Root URL:** `${authBaseUrl}`
   - **Home URL:** `/realms/REALM_SPRING_API/account/`
   - **Valid Redirect URIs:** `/realms/REALM_SPRING_API/account/*`
6. Clique em **Save**.

## 8. Associar Roles ao Cliente

1. No menu lateral, clique em **Client Scopes**.
2. Clique em **Roles**, depois em **Scope Assign Role**.
3. Selecione todas as roles criadas anteriormente.
4. Vá até a aba **Mappers**, clique em **Client Roles** e selecione o **Client ID** `CLIENT_SPRING`.

## 9. Testar a Configuração

1. Acesse **Clients** e clique no **Client ID** `CLIENT_SPRING`.
2. Copie a URL de autenticação (exemplo: `http://localhost:8080/auth/realms/REALM_SPRING_API/account/`).
3. Efetue login com um dos usuários criados.
4. No navegador, use a ferramenta "Inspecionar" para capturar o token JWT.
5. Decodifique o token no site [jwt.io](https://jwt.io) e verifique se os `realm_access.roles` estão corretos.

### Exemplo esperado do payload JWT:
```json
{
  "realm_access": {
    "roles": [
      "ADMIN_READ",
      "ADMIN_WRITE"
    ]
  }
}
```

## 10. Configuração de Políticas de Senha

As políticas de senha servem para definir regras que evitam senhas fracas.

1. No menu lateral, clique em **Authentication**.
2. Vá até a aba **Policies**.
3. Defina as seguintes configurações:
   - **Tamanho mínimo da senha:** `8`
   - **Tamanho máximo da senha:** `20`
   - **Exigir caracteres especiais:** Ativado (opcional)

Agora seu Keycloak está configurado corretamente para integração com o Spring Security!

