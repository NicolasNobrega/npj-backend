# Back-end GProJurídico

git ls-files | xargs cat | wc -l

Este repositório contém o back-end do sistema **GProJurídico**, um sistema **eficiente** para gestão e controle de **processos** e **atendimentos jurídicos**, destinado a uma organização que presta atendimentos às pessoas de baixa renda.

As tecnologias envolvidas são: **Java**, **Spring Boot**, **Spring Security**, **JWT** (JSON Web Token), **Firestore do GCP** (Google Cloud Platform), **Junit**, **Mockito** e **Swagger**.

As versões das tecnologias acima encontram-se no arquivo **pom.xml**.

## Sumário

- [Instalação](#instalação)
- [Uso](#uso)
  - [Firebase Suite Emulator](#firebase-suite-emulator)
- [Endpoints da API](#endpoints-da-api)
  - [Login e Autenticação](#login-e-autenticação)
- [Autorização](#autorização)
- [Mais sobre o Projeto](#mais-sobre-o-projeto)

## Instalação

1. Clone o repositório:

   1. Via HTTPS
      ```bash
      git clone https://github.com/fabuniprojecaotag/npj-backend.git
      ```
   
   2. Ou via SSH
        ```bash
        git clone git@github.com:fabuniprojecaotag/npj-backend.git
        ```

2. Instale as dependências com o Maven.

3. Instale e faça o login no [gcloud-cli](https://cloud.google.com/docs/authentication/provide-credentials-adc#google-idp).

   1. Ao fornecer suas **credenciais de usuário**, você está configurando o **ADC** (Application Default Credential) para ser autenticar seu usuário e interagir com o Firestore e outros produtos do GCP que o projeto faz uso;
   2. Certifique-se que você autenticou usando as **credenciais de usuário** em **ambiente de desenvolvimento local** do projeto, conforme o [link](https://cloud.google.com/docs/authentication/provide-credentials-adc#google-idp) acima;
   3. **OBS**: É necessário que algum proprietário do projeto adicione o seu usuário do Google para ter acesso ao projeto, tanto no **CLI** quanto no **console web do Firebase**.

4. (opcional) Instale o [Firebase Suite Emulator](https://firebase.google.com/docs/cli#install_the_firebase_cli).

## Uso

1. Inicie a aplicação com o Maven.
2. Realize o login (de algum usuário cadastro) para acessar os endpoints da aplicação.

### Firebase Suite Emulator

Para aqueles que necessitam desenvolver e testar **localmente**, sem interferir nos dados que operam no ambiente de desenvolvimento na nuvem, pode usar o **Firestore emulator**.

Após seguir o passo 4 da seção [Instalação](#instalação), você pode trocar **temporariamente** o perfil de projeto para **local-dev** e iniciar o emulador em seguida juntamente com aplicação. 

   1. Para trocar o perfil de projeto, acesse o arquivo `application.properties` e troque o nome `dev` para `local-dev`, de modo que fique conforme a instrução abaixo:

      ````properties
      spring.profiles.active=${APP_PROFILE:local-dev} # não esquecer de voltar para o valor inicial (dev)
      ````

   2. Para iniciar o emulador, execute o seguinte comando:

      ````bash
      firebase emulators:start --import=./dir --export-on-exit
      ````
      Sobre as flags:

      1. `--import`: sinaliza que ao iniciar o emulador, o emulador **deve** fazer a **importação** dos dados que ficaram salvos no diretório especificado.
      2. `--export-on-exit`: sinaliza que ao sair do emulador, através da combinação das teclas `Crtl + C`, o emulador **deve** fazer a **exportação** dos dados que estão disponíveis no momento em que o emulador está rodando.

Feito estes procedimentos, de ter **instalado o emulador**, ter **trocado o perfil** e ter **inicado o emulador**, lhe restará, portanto, apenas **iniciar a aplicação** normalmente, conforme a seção [Uso](#uso) instrui. 

## Endpoints da API

O sistema de back-end disponibiliza uma API REST para acesso aos recursos da aplicação por meio de endpoints. Os seguintes endpoints são fornecidos pela API:

```markdown
POST /login → Realiza uma ação de login ao sistema

(GET | POST | PUT | DELETE) /(usuários | assistidos | atendimentos | processos)/[id] → Realiza alguma ação desejada, expressa pelo verbo HTTP usado, ao recurso alvo.
```

Dicionário de nomenclaturas usadas: 

- Palavras entre **( )** são **obrigatórias** serem usadas
- Palavras entre **[ ]** são **opcionais** de serem usadas
- Uso de **|** , **pipe** em inglês, indica sentença **OU**. Ou seja, se houver duas ou mais palavras dentro de **( )** ou **[ ]** que usam o **|** , _apenas_ **uma** palavra poderá ser usada. 

### Login e Autenticação

Para realizar o login com algum usuário, é necessário que este esteja cadastrado previamente por algum usuário com permissão adequada. O payload da requisição deve seguir este formato:

````JSON
{
  "login": "******",
  "password": "*******"
}
````

Após o sistema **validar** o login, o usuário estará **autenticado** e receberá um **token de acesso**, em formato **JWT**, para acessar os recursos da aplicação. O acesso a esses endpoints _deve_ ser feita via **Bearer Token**.

## Autorização

Dado o login efetuado com sucesso, o usuário precisará ter a **role** necessária para acessar alguns **recursos protegidos**.

A API usa o **Spring Security** para o controle de autenticação. As seguintes roles estão disponíveis:

````
ESTAGIARIO → Possui acesso para executar GET em todos os endpoints
PROFESSOR → possui acesso para executar GET e POST em todos e um endpoint, respectivamente
SECRETARIA → possui acesso para executar GET, POST e PUT em todos os endpoints
COORDENADOR → Possui acesso total
````

## Mais sobre o Projeto

O sistema **GProJurídico** é fruto de um trabalho realizado pelo time da **Fábrica de Software** do **UniProjeção** de Taguatinga. 

Com o intuito de permitir que um **NPJ** (Núcleo de Práticas Jurídicas) vinculado à universidade tenha uma **gestão e controle eficientes dos atendimentos** que este presta para **comunidade de baixa de renda** e realiza entre os **estudantes e professores de Direito**, foi criado este projeto de software.

O trabalho encontra-se, atualmente, **em desenvolvimento**, segmentado entre **Back-end** e **Front-end** pela equipe e possui a previsão de ter a versão **1.0** lançada até o **final do 1º semestre de 2024**.
