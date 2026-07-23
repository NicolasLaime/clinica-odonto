# Análisis Completo del Backend - Sistema Clínico Odontológico

---

## 1. ARQUITECTURA Y STACK

### Versión de Java y Spring Boot
- **Java:** 17
- **Spring Boot:** 4.1.0

### Dependencias Principales

| Dependencia | Propósito |
|---|---|
| `spring-boot-starter-web` | REST API, MVC |
| `spring-boot-starter-data-jpa` | ORM / acceso a datos |
| `spring-boot-starter-security` | Autenticación y autorización |
| `spring-boot-starter-validation` | Bean Validation (@NotBlank, etc.) |
| `mysql-connector-j` | Driver MySQL |
| `com.auth0:java-jwt:4.5.2` | Generación/verificación de JWT |
| `com.mercadopago:sdk-java:3.1.0` | Integración con MercadoPago |
| `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6` | Documentación Swagger/OpenAPI |
| `lombok` | Reducción de boilerplate |
| `spring-boot-devtools` | Hot-reload en desarrollo |

### Patrón Arquitectónico

**Arquitectura por Capas (Layered Architecture)** organizada por dominio:

```
Controller → Service (interfaz) → ServiceImpl → Repository → Entity
                  ↕
              DTO / Mapper / Enums
```

Cada módulo de dominio (`paciente`, `turno`, `clinica`, etc.) es autónomo con su propio controller, service, repository y entity. Esto combina una arquitectura por capas con una organización tipo **modular/vertical slice**.

### Estructura de Carpetas

```
com.backend_sistema_clinico/
├── BackendSistemaClinicoApplication.java    → Clase principal (@SpringBootApplication, @EnableScheduling)
│
├── config/                                   → Configuración general de Spring
│   ├── SecurityConfig.java                   → Cadena de filtros de seguridad, reglas de autorización por URL
│   ├── CorsConfig.java                       → Configuración CORS (origenes abiertos)
│   ├── RestTemplateConfig.java               → Bean RestTemplate (usado por n8n y MercadoPago)
│   ├── OpenAPIConfig.java                    → Configuración de Swagger
│   └── JpaAuditingConfig.java                → Habilita auditoría de fechas (createdAt, updatedAt)
│
├── security/                                 → Infraestructura de autenticación
│   ├── ApiKeyFilter.java                     → Filtro que valida x-api-key para rutas de n8n
│   ├── CustomUserDetails.java                → Implementación de UserDetails de Spring Security
│   ├── CustomUserDetailsService.java         → Carga usuarios por email desde la BD
│   └── jwt/
│       ├── JwtService.java                   → Generación y validación de JWT (HMAC256)
│       ├── JWTAuthenticationFilter.java      → Filtro que valida Bearer tokens
│       └── TokenType.java                    → Enum ACCESS / REFRESH
│
├── auth/                                     → Módulo de autenticación (login, registro)
│   ├── controller/AuthController.java
│   ├── service/AuthService.java
│   ├── serviceImpl/AuthServiceImpl.java
│   ├── mapper/AuthMapper.java
│   └── dto/                                  → LoginRequest, RegisterRequest, RefreshTokenRequest, AuthResponse
│
├── user/                                     → Gestión de usuarios
│   ├── entity/Usuario.java, Role.java
│   ├── dto/UserDTO, CrearUsuarioRequest, ActualizarUsuarioRequest
│   ├── repository/UserRepository.java
│   ├── service/UserService.java
│   ├── serviceImpl/UserServiceImpl.java
│   └── controller/UserController.java, TestController.java
│
├── clinica/                                  → Gestión de clínicas (multi-tenant)
│   ├── entity/Clinica.java
│   ├── dto/ClinicaDto, CreateClinicaRequest
│   ├── repository/ClinicaRepository.java
│   ├── service/ClinicaService.java
│   ├── serviceimpl/ClinicaServiceImpl.java
│   └── controller/ClinicaController.java
│
├── paciente/                                 → Gestión de pacientes
│   ├── entity/Paciente.java
│   ├── dto/PacienteDTO, CreatePacienteDTO
│   ├── repository/PacienteRepository.java
│   ├── service/PacienteService.java
│   ├── serviceimpl/PacienteServiceImpl.java
│   └── controller/PacienteController.java
│
├── especialidad/                             → Gestión de especialidades odontológicas
│   ├── entity/Especialidad.java
│   ├── dto/EspecialidadDTO, CreateEspecialidadRequest
│   ├── repository/EspecialidadRepository.java
│   ├── service/EspecialidadService.java
│   ├── serviceImpl/EspecialidadServiceImpl.java
│   └── controller/EspecialidadController.java
│
├── Horarios/                                 → Gestión de horarios de odontólogos
│   ├── entity/Horarios.java
│   ├── Dto/HorarioDTO, CreateHorarioRequest
│   ├── repository/HorarioRepository.java
│   ├── service/HorarioService.java
│   ├── serviceimpl/HorarioServiceImpl.java
│   └── Controller/HorarioController.java
│
├── turno/                                    → Gestión de turnos (entidad central)
│   ├── entity/Turno.java, TurnoImagen.java, TipoTurno.java, EstadoTurno.java
│   ├── dto/TurnoDTO, CreateTurnoRequest, CompletarTurnoRequest, SlotDTO, TurnoImagenDTO
│   ├── repository/TurnoRepository.java, TurnoImagenRepository.java
│   ├── service/TurnoService.java
│   ├── serviceImpl/TurnoServiceImpl.java, LiberacionTurnosTask.java
│   └── controller/TurnoController.java
│
├── mercadopago/                              → Integración con MercadoPago
│   ├── entity/MercadoPagoConfigInit.java
│   ├── dto/CreatePreferenceRequest, CreatePreferenceResponse, WebhookNotification
│   ├── services/MercadoPagoService.java, WebhookService.java
│   └── controller/MercadoPagoController.java
│
├── n8n/                                      → Integración con n8n (automatización WhatsApp)
│   ├── controller/N8nController.java
│   ├── service/N8nService.java
│   └── dto/CreateTurnoN8nRequest, TurnoN8NResponse, SlotN8NResponse
│
├── conversacion/                             → Estado de conversaciones del chatbot
│   ├── entity/Conversacion.java
│   ├── dto/ConversacionDto, UpdateConversacionRequest
│   ├── repository/ConversacionRepository.java
│   ├── service/ConversacionService.java
│   ├── serviceImpl/ConversacionServiceImpl.java
│   └── controller/ConversacionController.java
│
├── mensaje/                                  → Gestión de mensajes WhatsApp
│   ├── entity/Mensaje.java
│   ├── enums/MensajeTipo.java, MensajeDireccion.java
│   ├── dto/MensajeDTO, EnviarMensajeRequest, GuardarMensajeRequest
│   ├── repository/MensajeRepository.java
│   ├── service/MensajeService.java
│   ├── serviceimpl/MensajeServiceImpl.java
│   └── controller/MensajeController.java
│
├── reportes/                                 → Dashboard y reportes
│   ├── dto/ReportesDto.java
│   ├── service/ReporteService.java
│   ├── serviceimpl/ReportesServiceImpl.java
│   └── controller/ReportesController.java
│
└── shared/                                   → Utilidades compartidas
    ├── utils/SecurityUtils.java              → Extrae clinicaId del JWT
    ├── response/ApiResponse.java             → Wrapper genérico de respuesta
    └── exception/GlobalExceptionHandler.java → Manejo global de errores
```

---

## 2. MODELOS / ENTIDADES

### 2.1 Usuario (`users`)

| Campo | Tipo | Constraints |
|---|---|---|
| `id` | Long | PK, auto-increment |
| `firstName` | String | |
| `lastName` | String | |
| `email` | String | UNIQUE |
| `password` | String | BCrypt hashed |
| `role` | Role (enum: `ADMIN_SAAS`, `ODONTOLOGO`) | |
| `active` | boolean | default: true |
| `clinicaId` | Long | FK → clinicas.id (nullable para ADMIN_SAAS) |
| `createdAt` | LocalDateTime | Audited |
| `updatedAt` | LocalDateTime | Audited |

### 2.2 Clinica (`clinicas`)

| Campo | Tipo | Constraints |
|---|---|---|
| `id` | UUID | PK |
| `nombre` | String | |
| `email` | String | UNIQUE |
| `apiKey` | String | UNIQUE, auto-generado (UUID) |
| `activo` | boolean | default: true |
| `createdAt` | LocalDateTime | Audited |
| `updatedAt` | LocalDateTime | Audited |

### 2.3 Paciente (`pacientes`)

| Campo | Tipo | Constraints |
|---|---|---|
| `id` | Long | PK, auto-increment |
| `nombre` | String | |
| `apellido` | String | |
| `email` | String | |
| `telefono` | String | |
| `direccion` | String | |
| `fechaNacimiento` | LocalDate | |
| `dni` | String | |
| `alergias` | String | |
| `enfermedades` | String | |
| `grupoSanguineo` | String | |
| `observaciones` | String | |
| `clinicaId` | Long | FK → clinicas.id |
| `especialidad` | Especialidad | @ManyToOne |
| `activo` | boolean | default: true |
| `createdAt` | LocalDateTime | Audited |
| `updatedAt` | LocalDateTime | Audited |

### 2.4 Especialidad (`especialidades`)

| Campo | Tipo | Constraints |
|---|---|---|
| `id` | Long | PK, auto-increment |
| `nombre` | String | UNIQUE |
| `descripcion` | String | |
| `activo` | boolean | default: true |

### 2.5 Horarios (`horarios`)

| Campo | Tipo | Constraints |
|---|---|---|
| `id` | Long | PK, auto-increment |
| `odontologo` | Usuario | @ManyToOne |
| `diaSemana` | DayOfWeek (enum) | |
| `horaInicio` | LocalTime | |
| `horaFin` | LocalTime | |
| `activo` | boolean | default: true |
| `clinicaId` | Long | FK → clinicas.id |

### 2.6 Turno (`turnos`)

| Campo | Tipo | Constraints |
|---|---|---|
| `id` | Long | PK, auto-increment |
| `paciente` | Paciente | @ManyToOne |
| `odontologo` | Usuario | @ManyToOne |
| `especialidad` | Especialidad | @ManyToOne |
| `fechaHora` | LocalDateTime | |
| `duracionMinutos` | Integer | |
| `tipo` | TipoTurno (enum: `CONSULTA`, `TRATAMIENTO`) | |
| `estado` | EstadoTurno (enum: `PENDIENTE_PAGO`, `CONFIRMADO`, `CANCELADO`, `COMPLETADO`) | |
| `callbackUrl` | String | |
| `diagnostico` | String | |
| `precio` | BigDecimal | |
| `precioTotal` | BigDecimal | |
| `senia` | BigDecimal | |
| `pagoEnConsultorio` | BigDecimal | |
| `linkPago` | String | |
| `preferenceId` | String | ID de preferencia MercadoPago |
| `initPoint` | String | Link de pago MercadoPago |
| `imagenes` | List\<TurnoImagen\> | @OneToMany(cascade=ALL) |
| `clinicaId` | Long | FK → clinicas.id |
| `createdAt` | LocalDateTime | Audited |
| `updatedAt` | LocalDateTime | Audited |

> **Unique constraint:** `(odontologo_id, fecha_hora)` — un odontólogo no puede tener dos turnos al mismo tiempo.

### 2.7 TurnoImagen (`turno_imagenes`)

| Campo | Tipo | Constraints |
|---|---|---|
| `id` | Long | PK, auto-increment |
| `turno` | Turno | @ManyToOne |
| `nombreArchivo` | String | |
| `rutaArchivo` | String | |
| `tipoContenido` | String | |

### 2.8 Conversacion (`conversaciones`)

| Campo | Tipo | Constraints |
|---|---|---|
| `id` | Long | PK, auto-increment |
| `telefono` | String | |
| `estado` | String | Estado del flujo del chatbot |
| `clinicaId` | Long | FK → clinicas.id |
| `contexto` | String (TEXT) | JSON serializado con contexto de la conversación |
| `createdAt` | LocalDateTime | Audited |
| `updatedAt` | LocalDateTime | Audited |

> **Unique constraint:** `(telefono, clinicaId)` — un teléfono tiene una sola conversación activa por clínica.

### 2.9 Mensaje (`mensajes`)

| Campo | Tipo | Constraints |
|---|---|---|
| `id` | Long | PK, auto-increment |
| `clinicaId` | Long | FK → clinicas.id |
| `telefono` | String | |
| `nombre` | String | |
| `contenido` | String (TEXT) | |
| `tipo` | MensajeTipo (enum: `TEXTO`, `INTERACTIVO`) | |
| `direccion` | MensajeDireccion (enum: `ENTRANTE`, `SALIENTE`) | |
| `waMessageId` | String | ID del mensaje en WhatsApp |
| `leido` | boolean | default: false |
| `creadoAt` | LocalDateTime | |

### Resumen de Relaciones

```
Clinica (1) ──── (N) Usuario
Clinica (1) ──── (N) Paciente
Clinica (1) ──── (N) Horario
Clinica (1) ──── (N) Turno
Clinica (1) ──── (N) Conversacion
Clinica (1) ──── (N) Mensaje

Paciente  (N) ──── (1) Especialidad
Paciente  (1) ──── (N) Turno

Usuario   (1) ──── (N) Turno    (como odontólogo)
Usuario   (1) ──── (N) Horario  (como odontólogo)

Especialidad (1) ── (N) Turno

Turno     (1) ──── (N) TurnoImagen
```

---

## 3. LISTA COMPLETA DE RUTAS

### Auth

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| POST | `/api/v1/auth/register` | Registrar usuario | `RegisterRequest`: firstName, lastName, email, password, role, clinicaId | `AuthResponse`: accessToken, refreshToken |
| POST | `/api/v1/auth/login` | Iniciar sesión | `LoginRequest`: email, password | `AuthResponse`: accessToken, refreshToken |
| POST | `/api/v1/auth/refresh` | Renovar tokens | `RefreshTokenRequest`: refreshToken | `AuthResponse`: accessToken, refreshToken |
| GET | `/api/v1/auth/me` | Obtener usuario actual | — | `UserDTO` |

### Users

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| GET | `/api/v1/users` | Listar usuarios (paginado) | — | Página de `UserDTO` |
| POST | `/api/v1/users` | Crear usuario | `CrearUsuarioRequest`: firstName, lastName, email, password, role, clinicaId | `UserDTO` |
| PUT | `/api/v1/users/{id}` | Actualizar usuario | `ActualizarUsuarioRequest` (parcial) | `UserDTO` |
| DELETE | `/api/v1/users/{id}` | Desactivar usuario (soft delete) | — | `ApiResponse` |

### Test

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| GET | `/api/v1/test/admin` | Verificar rol ADMIN_SAAS | — | Mensaje de verificación |
| GET | `/api/v1/test/odontologo` | Verificar rol ODONTOLOGO o superior | — | Mensaje de verificación |

### Clínicas

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| POST | `/api/v1/clinicas` | Crear clínica (auto-genera API key) | `CreateClinicaRequest`: nombre, email | `ClinicaDto` |

### Pacientes

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| GET | `/api/v1/pacientes` | Listar pacientes (paginado, por clínica) | — | Página de `PacienteDTO` |
| GET | `/api/v1/pacientes/buscar?q=` | Buscar por nombre/apellido | query param `q` | Lista de `PacienteDTO` |
| GET | `/api/v1/pacientes/{id}` | Obtener paciente por ID | — | `PacienteDTO` |
| POST | `/api/v1/pacientes` | Crear paciente | `CreatePacienteDTO` con validaciones @NotBlank | `PacienteDTO` |
| PUT | `/api/v1/pacientes/{id}` | Actualizar paciente | `CreatePacienteDTO` | `PacienteDTO` |
| DELETE | `/api/v1/pacientes/{id}` | Desactivar paciente (soft delete) | — | `ApiResponse` |

### Especialidades

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| GET | `/api/v1/especialidades` | Listar especialidades activas | — | Lista de `EspecialidadDTO` |
| GET | `/api/v1/especialidades/{id}` | Obtener especialidad | — | `EspecialidadDTO` |
| POST | `/api/v1/especialidades` | Crear especialidad | `CreateEspecialidadRequest`: nombre, descripcion | `EspecialidadDTO` |
| PUT | `/api/v1/especialidades/{id}` | Actualizar especialidad | `CreateEspecialidadRequest` | `EspecialidadDTO` |
| DELETE | `/api/v1/especialidades/{id}` | Desactivar especialidad (soft delete) | — | `ApiResponse` |

### Horarios

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| POST | `/api/v1/horarios` | Crear horario | `CreateHorarioRequest`: odontologoId, diaSemana, horaInicio, horaFin | `HorarioDTO` |
| GET | `/api/v1/horarios/odontologo/{odontologoId}` | Horarios de un odontólogo | — | Lista de `HorarioDTO` |
| GET | `/api/v1/horarios/dia/{dia}` | Horarios por día de la semana | — | Lista de `HorarioDTO` |
| DELETE | `/api/v1/horarios/{id}` | Desactivar horario (soft delete) | — | `ApiResponse` |

### Turnos

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| GET | `/api/v1/turnos` | Listar turnos (paginado, por clínica) | — | Página de `TurnoDTO` |
| GET | `/api/v1/turnos/disponibles` | Obtener slots disponibles | parámetros de fecha | Lista de `SlotDTO` |
| POST | `/api/v1/turnos` | Crear turno (CONSULTA genera preferencia MP automáticamente) | `CreateTurnoRequest` | `TurnoDTO` (con initPoint si es CONSULTA) |
| GET | `/api/v1/turnos/{id}` | Obtener turno por ID | — | `TurnoDTO` |
| POST | `/api/v1/turnos/{id}/confirmar-pago` | Confirmar pago de turno | — | `TurnoDTO` (estado → CONFIRMADO) |
| POST | `/api/v1/turnos/{id}/cancelar` | Cancelar turno | — | `ApiResponse` (estado → CANCELADO) |
| POST | `/api/v1/turnos/{id}/completar` | Completar turno | `CompletarTurnoRequest`: diagnostico, pagoEnConsultorio | `TurnoDTO` (estado → COMPLETADO) |
| GET | `/api/v1/turnos/paciente/{pacienteId}` | Turnos de un paciente | — | Lista de `TurnoDTO` |
| POST | `/api/v1/turnos/{id}/imagenes` | Subir imagen (multipart) | archivo multipart | `TurnoImagenDTO` |
| GET | `/api/v1/turnos/imagenes/{imagenId}` | Obtener imagen | — | Bytes de la imagen |

### MercadoPago

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| POST | `/api/mercadopago/webhook` | Webhook de notificaciones MP | `WebhookNotification` | 200 OK (procesa pago) |

### n8n (Automatización WhatsApp)

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| POST | `/api/v1/n8n/turnos` | Crear turno desde n8n | `CreateTurnoN8nRequest` | `TurnoN8NResponse`: turnoId, initPoint, estado |
| GET | `/api/v1/n8n/disponibles` | Slots disponibles para n8n | parámetros de fecha | Lista de `SlotN8NResponse` |
| GET | `/api/v1/n8n/odontologos` | Listar odontólogos de la clínica | — | Lista de `UserDTO` |
| GET | `/api/v1/n8n/pacientes/buscar?dni=` | Buscar paciente por DNI | query param `dni` | `PacienteDTO` |
| POST | `/api/v1/n8n/pacientes` | Crear paciente desde n8n | `CreatePacienteDTO` | `PacienteDTO` |
| GET | `/api/v1/n8n/turnos/{id}` | Estado de un turno | — | `TurnoDTO` |

### Conversaciones

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| GET | `/api/v1/conversaciones/{telefono}` | Obtener conversación por teléfono | — | `ConversacionDto` |
| PATCH | `/api/v1/conversaciones/{telefono}` | Actualizar estado de conversación | `UpdateConversacionRequest` | `ConversacionDto` |
| DELETE | `/api/v1/conversaciones/{telefono}` | Eliminar conversación | — | `ApiResponse` |
| GET | `/api/v1/crm/conversaciones` | Listar todas las conversaciones (CRM) | — | Lista de `ConversacionDto` |
| GET | `/api/v1/crm/conversaciones/{telefono}` | Obtener conversación específica (CRM) | — | `ConversacionDto` |

### Mensajes

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| GET | `/api/v1/mensajes` | Listar todos los mensajes | — | Lista de `MensajeDTO` |
| GET | `/api/v1/mensajes/{telefono}` | Mensajes de un teléfono + marca como leídos | — | Lista de `MensajeDTO` |
| POST | `/api/v1/mensajes/{telefono}` | Enviar mensaje de salida | `EnviarMensajeRequest`: contenido, tipo | `MensajeDTO` |
| POST | `/api/v1/mensajes/guardar` | Guardar mensaje entrante (n8n) | `GuardarMensajeRequest` | `MensajeDTO` |

### Reportes

| Método | Ruta | Descripción | Body requerido | Lo que devuelve |
|---|---|---|---|---|
| GET | `/api/v1/reportes/resumen` | Resumen del dashboard (turnos, ingresos, etc.) | — | `ReportesDto` |

---

## 4. SEGURIDAD POR RUTA

### Mecanismos de Autenticación

El backend tiene **dos mecanismos** de autenticación:

1. **JWT Bearer Token** — Para el CRM web. Se envía en el header `Authorization: Bearer <token>`.
2. **API Key** — Para integraciones con n8n. Se envía en el header `x-api-key`. El filtro `ApiKeyFilter` valida la key contra la tabla `clinicas` y setea el `clinicaId` como atributo del request.

### Configuración de SecurityConfig

| Rutas | Autorización |
|---|---|
| `/api/v1/auth/**` | Público (sin auth) |
| `/api/mercadopago/webhook` | Público (sin auth) |
| `/api/v1/n8n/**` | API Key (x-api-key header) |
| `/api/v1/conversaciones/**` | API Key (x-api-key header) |
| `/api/v1/mensajes/guardar` | API Key (x-api-key header) |
| `/api/v1/crm/**` | JWT, rol ADMIN_SAAS |
| `/api/v1/reportes/**` | JWT, rol ADMIN_SAAS |
| `/api/v1/mensajes` (GET) | JWT, rol ADMIN_SAAS |
| `/api/v1/mensajes/{telefono}` | JWT, rol ADMIN_SAAS |
| `/api/v1/clinicas` | JWT, rol ADMIN_SAAS |
| `/api/v1/users/**` | JWT, rol ADMIN_SAAS |
| `/api/v1/especialidades` POST/PUT/DELETE | JWT, rol ADMIN_SAAS |
| `/api/v1/pacientes/{id}` DELETE | JWT, rol ADMIN_SAAS |
| `/api/v1/horarios/{id}` DELETE | JWT, rol ADMIN_SAAS |
| `/api/v1/turnos/{id}/confirmar-pago` | JWT, rol ADMIN_SAAS |
| Todo lo demás (`**`) | JWT, rol ADMIN_SAAS o ODONTOLOGO |

### Detalle por Endpoint

| Método | Ruta | Autenticación | Rol Requerido | Validación Token |
|---|---|---|---|---|
| POST | `/api/v1/auth/register` | Ninguna | Público | — |
| POST | `/api/v1/auth/login` | Ninguna | Público | — |
| POST | `/api/v1/auth/refresh` | Ninguna | Público | — |
| GET | `/api/v1/auth/me` | JWT | Cualquier usuario autenticado | `Authorization: Bearer <token>` |
| GET | `/api/v1/users` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| POST | `/api/v1/users` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| PUT | `/api/v1/users/{id}` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| DELETE | `/api/v1/users/{id}` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| GET | `/api/v1/test/admin` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| GET | `/api/v1/test/odontologo` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| POST | `/api/v1/clinicas` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| GET | `/api/v1/pacientes` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| GET | `/api/v1/pacientes/buscar` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| GET | `/api/v1/pacientes/{id}` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token}` |
| POST | `/api/v1/pacientes` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| PUT | `/api/v1/pacientes/{id}` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| DELETE | `/api/v1/pacientes/{id}` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| GET | `/api/v1/especialidades` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| GET | `/api/v1/especialidades/{id}` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| POST | `/api/v1/especialidades` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| PUT | `/api/v1/especialidades/{id}` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| DELETE | `/api/v1/especialidades/{id}` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| POST | `/api/v1/horarios` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| GET | `/api/v1/horarios/odontologo/{id}` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| GET | `/api/v1/horarios/dia/{dia}` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| DELETE | `/api/v1/horarios/{id}` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| GET | `/api/v1/turnos` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| GET | `/api/v1/turnos/disponibles` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| POST | `/api/v1/turnos` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| GET | `/api/v1/turnos/{id}` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| POST | `/api/v1/turnos/{id}/confirmar-pago` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| POST | `/api/v1/turnos/{id}/cancelar` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| POST | `/api/v1/turnos/{id}/completar` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| GET | `/api/v1/turnos/paciente/{id}` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| POST | `/api/v1/turnos/{id}/imagenes` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| GET | `/api/v1/turnos/imagenes/{id}` | JWT | ADMIN_SAAS u ODONTOLOGO | `Authorization: Bearer <token>` |
| POST | `/api/mercadopago/webhook` | Ninguna | Público (sin auth) | — |
| POST | `/api/v1/n8n/turnos` | API Key | Público (con API key válida) | `x-api-key: <key>` |
| GET | `/api/v1/n8n/disponibles` | API Key | Público (con API key válida) | `x-api-key: <key>` |
| GET | `/api/v1/n8n/odontologos` | API Key | Público (con API key válida) | `x-api-key: <key>` |
| GET | `/api/v1/n8n/pacientes/buscar` | API Key | Público (con API key válida) | `x-api-key: <key>` |
| POST | `/api/v1/n8n/pacientes` | API Key | Público (con API key válida) | `x-api-key: <key>` |
| GET | `/api/v1/n8n/turnos/{id}` | API Key | Público (con API key válida) | `x-api-key: <key>` |
| GET | `/api/v1/conversaciones/{telefono}` | API Key | Público (con API key válida) | `x-api-key: <key>` |
| PATCH | `/api/v1/conversaciones/{telefono}` | API Key | Público (con API key válida) | `x-api-key: <key>` |
| DELETE | `/api/v1/conversaciones/{telefono}` | API Key | Público (con API key válida) | `x-api-key: <key>` |
| GET | `/api/v1/crm/conversaciones` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| GET | `/api/v1/crm/conversaciones/{telefono}` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| GET | `/api/v1/mensajes` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| GET | `/api/v1/mensajes/{telefono}` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| POST | `/api/v1/mensajes/{telefono}` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |
| POST | `/api/v1/mensajes/guardar` | API Key | Público (con API key válida) | `x-api-key: <key>` |
| GET | `/api/v1/reportes/resumen` | JWT | ADMIN_SAAS | `Authorization: Bearer <token>` |

---

## 5. LÓGICA DE NEGOCIO RELEVANTE

### 5.1 Generación y Validación de JWT

**Implementación:** `security/jwt/JwtService.java`

| Aspecto | Detalle |
|---|---|
| **Librería** | `com.auth0:java-jwt:4.5.2` |
| **Algoritmo** | HMAC256 (simétrico) |
| **Secret** | `clinica_bot_jwt_secret_2024_super_seguro` (configurable en `jwt.secret`) |
| **Access Token** | Expira en 15 minutos (900000 ms) |
| **Refresh Token** | Expira en 7 días (604800000 ms) |
| **Payload del Access Token** | `sub` (email), `role` (ADMIN_SAAS/ODONTOLOGO), `clinicaId`, `tokenType` ("ACCESS") |
| **Payload del Refresh Token** | `sub` (email), `tokenType` ("REFRESH") |
| **Generación** | Al hacer login o register, se devuelven ambos tokens |
| **Refresh** | Se valida el refresh token y se generan nuevos tokens |
| **Validación** | `JWTAuthenticationFilter` extrae el token del header `Authorization: Bearer <token>`, verifica la firma y expiración, y setea el `SecurityContext` con los `CustomUserDetails` |

### 5.2 Multi-Tenant

El sistema es **multi-tenant por clínica**. El mecanismo es:

1. Cada usuario registrado tiene un campo `clinicaId` (excepto el ADMIN_SAAS que puede no tener uno).
2. El `clinicaId` se incluye como claim en el JWT al momento del login.
3. `SecurityUtils.obtenerClinicaId()` extrae el `clinicaId` del `SecurityContext` (desde los claims del JWT).
4. Todos los controllers de dominio (pacientes, turnos, horarios, mensajes, conversaciones, reportes) filtran por `clinicaId` al momento de consultar la BD.
5. Para las rutas de n8n (API Key), el `ApiKeyFilter` busca la clínica por su API key en la tabla `clinicas` y setea el `clinicaId` como request attribute.
6. Los turnos, pacientes, horarios, conversaciones y mensajes están scoped a una `clinicaId` específica.

**Entidades con multi-tenant:** Paciente, Turno, Horario, Mensaje, Conversacion.

**Entidad global (sin multi-tenant):** Especialidad (compartida entre clínicas), Clinica, Usuario (su clinicaId es un campo, no un filtro).

### 5.3 Integración con MercadoPago

**Archivos involucrados:**
- `mercadopago/services/MercadoPagoService.java`
- `mercadopago/services/WebhookService.java`
- `mercadopago/controller/MercadoPagoController.java`
- `mercadopago/entity/MercadoPagoConfigInit.java`

**Flujo de pago:**

1. **Al crear un turno de tipo CONSULTA:**
   - Se genera una preferencia de pago en MercadoPago.
   - Se usa `MercadoPagoService.crearPreferencia()` con: título (nombre del paciente + especialidad), monto (senia), y `turnoId` como referencia externa.
   - Se configuran `back_urls` (éxito/failure/pending) y `notification_url` (webhook).
   - El `initPoint` (link de pago) se guarda en el turno y se devuelve al frontend.
   - El estado del turno queda en `PENDIENTE_PAGO`.

2. **Webhook de notificación:**
   - MercadoPago envía POST a `/api/mercadopago/webhook`.
   - `WebhookService.procesarNotificacion()` verifica si el pago fue aprobado.
   - Si fue aprobado: cambia el estado del turno a `CONFIRMADO`, actualiza el estado de la conversación (si existe), y envía un webhook a n8n para confirmar el pago.
   - **No requiere autenticación** (webhook público de MercadoPago).

3. **Confirmación manual:** Existe también `POST /{id}/confirmar-pago` para confirmación manual (requiere JWT + ADMIN_SAAS).

4. **Liberación automática:** `LiberacionTurnosTask` corre cada 30 segundos y cancela turnos con estado `PENDIENTE_PAGO` que llevan más de 10 minutos (configurable en `app.tiempo-liberacion-minutos`).

**Configuración:**
- `mercadopago.access-token` en `application.properties` (token de producción MP).
- `app.mp.notification-url` — URL del webhook.
- `app.mp.back-urls` — URLs de redirección tras el pago.

### 5.4 Integración con WhatsApp (vía n8n)

El backend **no se conecta directamente a la API de WhatsApp**. En su lugar, actúa como backend para un flujo de **n8n** (automatización) que maneja la comunicación por WhatsApp.

**Arquitectura:**

```
WhatsApp ←→ n8n (chatbot) ←→ API Key → Backend (REST)
```

**Módulos involucrados:**
- `n8n/` — Endpoints para que n8n consulte/cree turnos, pacientes y odontólogos.
- `conversacion/` — Estado del chatbot (flujo, contexto JSON) por teléfono + clínica.
- `mensaje/` — Almacenamiento de mensajes entrantes y salientes.

**Flujo típico:**
1. Paciente envía mensaje por WhatsApp.
2. n8n recibe el mensaje, consulta el estado de la conversación (`GET /api/v1/conversaciones/{telefono}` con API key).
3. n8n busca paciente por teléfono o DNI (`GET /api/v1/n8n/pacientes/buscar`).
4. n8n crea un turno (`POST /api/v1/n8n/turnos`) y obtiene el `initPoint` (link de pago MP).
5. n8n envía el link de pago al paciente por WhatsApp.
6. n8n guarda el mensaje entrante (`POST /api/v1/mensajes/guardar`) y el saliente (`POST /api/v1/mensajes/{telefono}`).
7. Se actualiza el estado de la conversación (`PATCH /api/v1/conversaciones/{telefono}`).

### 5.5 Manejo de Errores

**Implementación:** `shared/exception/GlobalExceptionHandler.java`

Se usa `@RestControllerAdvice` con `ProblemDetail` de Spring para respuestas estandarizadas:

| Excepción | Código HTTP | Descripción |
|---|---|---|
| `MethodArgumentNotValidException` | 400 Bad Request | Error de validación (@NotBlank, @Valid, etc.) |
| `BadCredentialsException` | 401 Unauthorized | Credenciales incorrectas (login fallido) |
| `JWTVerificationException` | 401 Unauthorized | Token JWT inválido o expirado |
| `AccessDeniedException` | 403 Forbidden | Sin permisos para el recurso |
| `RuntimeException` | 400 Bad Request | Errores de negocio genéricos |
| `Exception` (catch-all) | 500 Internal Server Error | Error inesperado del servidor |

**Formato de respuesta de error (ProblemDetail):**
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "El email ya está registrado",
  "instance": "/api/v1/auth/register"
}
```

**Wrapper genérico de éxito:** `ApiResponse<T>` con campos `success` (boolean), `message` (String), `data` (T). Definido en `shared/response/ApiResponse.java` pero **no utilizado por la mayoría de los controllers** — estos devuelven directamente el DTO o entidad en `ResponseEntity`.

---

## Observaciones Adicionales

1. **Credenciales en texto plano:** `application.properties` contiene la contraseña de MySQL, el secret de JWT y el access token de MercadoPago en texto plano. Para producción, se recomienda usar variables de entorno o un gestor de secretos.

2. **Nombre de paquete inconsistente:** El paquete `Horarios` usa mayúscula inicial (`com.backend_sistema_clinico.Horarios.Controller`), lo que viola las convenciones de nomenclatura de Java.

3. **Soft deletes:** Todas las entidades de dominio usan borrado lógico (campo `activo = false`) en vez de borrado físico.

4. **Sin tests unitarios:** Solo existe el test placeholder `contextLoads()`. No hay tests de integración ni unitarios.

5. **Swagger/OpenAPI habilitado:** Disponible vía SpringDoc en la ruta `/swagger-ui.html` o `/swagger-ui/index.html`.

6. **Tareas programadas:** `LiberacionTurnosTask` ejecuta cada 30 segundos una limpieza automática de turnos no pagados, usando `@Scheduled(fixedRate = 30000)`.

7. **Archivos subidos:** Las imágenes de turnos se guardan en disco local en la carpeta `uploads/turnos/`.
