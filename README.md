# Documentação PhysioApp (MVP)

## Visão Geral

  - **Framework:** Spring Boot + Spring Data JPA
  - **Banco de Dados:** PostgreSQL
  - **Serialização:** Jackson (com módulos customizados para Hibernate)
  - **Arquitetura:** 3-Tier (Controller-Service-Repository)
  - **Observações:** O projeto utiliza uma estratégia de herança `SINGLE_TABLE` para os modelos de `User`, `Patient` e `Physiotherapist`.

-----

## Endpoints da API

### Login e Cadastro de Fisioterapeutas e Pacientes

#### POST '/auth/register'
  
  - **Descrição:** Cadastro de novo usuário (Paciente) 
  - **Body (para Paciente):**
    ```json
    {
      "fullname": "João Silva",
      "email": "<joao.silva.paciente@exemplo.com>",
      "password": "senha123",
      "user_type": "PATIENT"
    }
    ```
  
  - **Resposta:** 201 Created.

  - **Descrição:** Cadastro de novo usuário (Fisioterapeuta)
  - **Body (para Fisioterapeuta):**

    ```json
    {
      "fullname": "Dra. Maria Oliveira",
      "email": "<maria.oliveira.fisio@exemplo.com>",
      "password": "senha456",
      "user_type": "PHYSIO",
      "crefito": "414243-F"
    }
    ```

  - **Resposta:** 201 Created.

#### POST '/auth/login'
  
- **Descrição:** Login de usuário (Paciente ou Fisioterapeuta)
- **Body:**
    ```json
    {
      "email": "<joao.silva.paciente@exemplo.com>",
      "password": "senha123"
    }
    ```
  
  - **Resposta:** 200 OK + ```{ 'token': 'eyJhbGciOiJIUzI1NiJ9...' } ```
  ###

    ```json
    {
      "email": "<maria.oliveira.fisio@exemplo.com>",
      "password": "senha456"
    }
    ```
  - **Resposta:** 200 OK + ```{ 'token': 'eyJhbGciOiJIUzI1NiJ9...' }```

  ###

### Usuários (Patients e Physiotherapists)

#### `POST /users`

  - **Descrição:** Cadastro de novo usuário (Paciente ou Fisioterapeuta).
  - **Body (para Paciente):**
    ```json
    {
      "user_type": "PATIENT",
      "username": "john.doe.patient",
      "fullname": "John Doe",
      "email": "john.doe@example.com",
      "phone": "555-1234",
      "password": "password123",
      "role": "PATIENT"
    }
    ```
  - **Body (para Fisioterapeuta):**
    ```json
    {
      "user_type": "PHYSIO",
      "username": "jane.smith.physio",
      "fullname": "Dr. Jane Smith",
      "email": "jane.smith@example.com",
      "phone": "555-5678",
      "password": "password456",
      "role": "PHYSIO",
      "crefito": "PHYSIO-12345"
    }
    ```
  - **Resposta:** 201 Created + dados do usuário criado.

#### `GET /users`

  - **Descrição:** Lista todos os usuários.
  - **Resposta:** 200 OK + lista de usuários.

#### `GET /users/{id}`

  - **Descrição:** Detalha um usuário específico.
  - **Resposta:** 200 OK + dados do usuário.

#### `PUT /users/{id}`

  - **Descrição:** Atualiza dados de um usuário.
  - **Body:** Mesmo do POST.
  - **Resposta:** 200 OK + dados atualizados.

#### `DELETE /users/{id}`

  - **Descrição:** Remove um usuário.
  - **Resposta:** 204 No Content.

-----

### Agendamentos (Appointments)

#### `POST /appointments`

  - **Descrição:** Cria um novo agendamento.
  - **Body:**
    ```json
    {
      "physiotherapistId": 2,
      "patientId": 1,
      "dateTime": "2025-10-20T14:00:00",
      "durationMinutes": 45,
      "notes": "Consulta inicial."
    }
    ```
  - **Resposta:** 201 Created + dados do agendamento.

#### `GET /appointments/{id}`

  - **Descrição:** Detalha um agendamento específico.
  - **Resposta:** 200 OK + dados do agendamento.

#### `GET /appointments`

  - **Descrição:** Lista agendamentos, com suporte a filtros.
  - **Query Params:**
      - `?patientId={id}`: Filtra agendamentos por ID do paciente.
      - `?physiotherapistId={id}`: Filtra agendamentos por ID do fisioterapeuta.
  - **Resposta:** 200 OK + lista de agendamentos filtrada.

#### `POST /appointments/{id}/cancel`

  - **Descrição:** Altera o status de um agendamento para `CANCELLED`.
  - **Resposta:** 200 OK + dados do agendamento atualizado.

-----

### Notificações (Notifications)

#### `POST /notifications`

  - **Descrição:** Cria uma nova notificação para um usuário.
  - **Body:**
    ```json
    {
      "recipientId": 1,
      "title": "Lembrete de Consulta",
      "message": "Sua consulta amanhã às 14h está confirmada.",
      "type": "REMINDER"
    }
    ```
  - **Resposta:** 201 Created + dados da notificação.

#### `GET /notifications`

  - **Descrição:** Lista notificações de um usuário.
  - **Query Params:**
      - `?userId={id}` (obrigatório): ID do usuário para buscar notificações.
      - `?unreadOnly=true` (opcional): Retorna apenas notificações não lidas.
  - **Resposta:** 200 OK + lista de notificações.

#### `POST /notifications/{id}/read`

  - **Descrição:** Marca uma notificação como lida.
  - **Resposta:** 200 OK + dados da notificação atualizada.

#### `DELETE /notifications/{id}`

  - **Descrição:** Remove uma notificação.
  - **Resposta:** 204 No Content.

-----

## Estrutura de Pastas

```
physioapp/
├── src/
│   └── main/
│       ├── java/
│       │   └── br/com/physioapp/api/physioapp/
│       │       ├── config/              # Configurações (Jackson, Security)
│       │       ├── controller/          # UserController, AppointmentController, etc.
│       │       ├── dto/                 # Data Transfer Objects (DTOs)
│       │       ├── exception/           # Exceções customizadas
│       │       ├── model/               # Entidades JPA (User, Appointment, etc.)
│       │       ├── repository/          # Interfaces Spring Data JPA
│       │       └── service/             # Lógica de negócio
│       └── resources/
│           └── application.properties   # Configurações da aplicação
├── pom.xml                              # Dependências e build do projeto
└── ...
```