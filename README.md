# Insurance Policy Management API

API REST para la gestión de pólizas de seguros, desarrollada con Java y Spring Boot. Permite administrar pólizas individuales y colectivas, gestionar sus riesgos asociados, realizar renovaciones y controlar cancelaciones.

El proyecto está diseñado bajo una arquitectura backend basada en servicios REST, persistencia relacional y separación entre entidades, DTO y lógica de acceso a datos.

## Tecnologías utilizadas

- **Java** – Lenguaje de programación.
- **Spring Boot** – Framework para el desarrollo de la API REST.
- **Spring Data JPA / Hibernate** – Persistencia y gestión de entidades.
- **PostgreSQL** – Base de datos relacional.
- **Maven** – Gestión de dependencias y construcción del proyecto.
- **Lombok** – Reducción de código repetitivo.
- **MapStruct** – Conversión entre entidades y DTO.
- **Jakarta Validation** – Validación de datos de entrada, si está configurada.

## Funcionalidades

### Gestión de pólizas

- Registro y consulta de pólizas.
- Administración de pólizas individuales y colectivas.
- Consulta de información y riesgos asociados.
- Renovación de pólizas.
- Actualización de fechas de vigencia.
- Cancelación de pólizas.

### Gestión de riesgos

- Asociación de riesgos a pólizas.
- Registro de información y valores asegurados.
- Consulta de riesgos asociados a una póliza.
- Cancelación lógica de riesgos mediante su estado de actividad.
- Validación para permitir agregar riesgos únicamente a pólizas colectivas.

### Renovación e IPC

La renovación contempla la actualización de la vigencia y los valores económicos de la póliza.

El porcentaje de incremento debe corresponder al IPC aplicable y a las condiciones contractuales correspondientes. En el ejemplo de implementación se utiliza un porcentaje de referencia configurable.

## Modelo de datos

El modelo principal está compuesto por las siguientes entidades:

| Entidad | Descripción |
|---|---|
| `Policy` | Información general de la póliza. |
| `PolicyType` | Catálogo de tipos de póliza. |
| `PolicyStatus` | Catálogo de estados de póliza. |
| `Risk` | Riesgos asociados a una póliza. |

### Relaciones

- Una póliza tiene un tipo y un estado.
- Una póliza puede tener múltiples riesgos.
- Cada riesgo pertenece a una única póliza.
- Los tipos y estados se administran mediante tablas de catálogo.

### Campos principales de Policy

- `policyNumber`: número único de póliza.
- `type`: tipo de póliza (INDIVIDUAL o COLLECTIVE).
- `status`: estado actual.
- `premium`: prima del seguro.
- `coverageAmount`: valor de cobertura.
- `rentalAmount`: canon de arrendamiento, cuando aplique.
- `startDate`: fecha de inicio de vigencia.
- `endDate`: fecha de finalización de vigencia.
- `description`: descripción de la póliza.

## Endpoints principales

Base URL:

```text
http://localhost:8080/api
```

### Pólizas

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/polizas` | Consultar pólizas. |
| GET | `/polizas/{id}` | Consultar una póliza por ID. |
| POST | `/polizas` | Registrar una póliza. |
| PUT | `/polizas/{id}` | Actualizar una póliza. |
| POST | `/polizas/{id}/renovar` | Renovar una póliza. |
| POST | `/polizas/{id}/cancelar` | Cancelar una póliza. |
| GET | `/polizas/{id}/riesgos` | Consultar riesgos de una póliza. |
| POST | `/polizas/{id}/riesgos` | Agregar un riesgo a una póliza colectiva. |

### Riesgos

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/riesgos/{id}/cancelar` | Cancelar un riesgo. |

> Los endpoints de consulta, creación y actualización deben coincidir con las rutas efectivamente implementadas en el proyecto.

## Reglas de negocio

1. Cada póliza debe tener un número único.
2. Una póliza debe tener un tipo y un estado válidos.
3. Los riesgos deben estar asociados a una póliza existente.
4. Solo las pólizas de tipo `COLLECTIVE` pueden recibir nuevos riesgos.
5. La cancelación de un riesgo no implica la cancelación de toda la póliza.
6. La cancelación de una póliza modifica su estado, sin eliminar el registro.
7. La renovación actualiza el periodo de vigencia y los valores económicos de acuerdo con las reglas definidas.
8. Los valores monetarios se manejan utilizando `BigDecimal` para evitar imprecisiones de punto flotante.

## Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- JDK compatible con la versión configurada en el proyecto.
- PostgreSQL.
- Maven o el Maven Wrapper incluido en el repositorio.
- Un cliente para probar APIs REST, como Postman o Insomnia.

## Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/TU-USUARIO/insurance-policy-management.git

cd insurance-policy-management
```

### 2. Crear la base de datos

Desde PostgreSQL, crea una base de datos:

```sql
CREATE DATABASE insurance_management;
```

### 3. Configurar la conexión

Edita el archivo:

```text
src/main/resources/application.properties
```

Configura las credenciales de PostgreSQL:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/insurance_management}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:your_password}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Reemplaza las credenciales según tu configuración local.

### 4. Ejecutar el proyecto

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

La API estará disponible en:

```text
http://localhost:8080
```

## Ejemplos de solicitudes

### Agregar un riesgo a una póliza colectiva

**POST**

```http
/api/polizas/2/riesgos
```

Body:

```json
{
  "name": "Daños por agua",
  "description": "Daños ocasionados por fugas o inundaciones",
  "insuredValue": 25000000
}
```

### Cancelar una póliza

**POST**

```http
/api/polizas/1/cancelar
```

### Cancelar un riesgo

**POST**

```http
/api/riesgos/1/cancelar
```

## Manejo de respuestas HTTP

La API utiliza códigos HTTP para representar el resultado de las operaciones:

| Código | Significado |
|---|---|
| `200 OK` | Operación realizada correctamente. |
| `201 Created` | Recurso creado correctamente. |
| `400 Bad Request` | Solicitud inválida o regla de negocio incumplida. |
| `404 Not Found` | Recurso inexistente. |
| `409 Conflict` | Conflicto con el estado actual del recurso. |
| `500 Internal Server Error` | Error interno del servidor. |

## Estructura del proyecto

```text
src/
└── main/
    ├── java/
    │   └── com/policy/store/
    │       ├── controllers/
    │       ├── dtos/
    │       ├── entities/
    │       ├── mappers/
    │       ├── repositories/
    │       └── services/
    │
    └── resources/
        └── application.properties/
```