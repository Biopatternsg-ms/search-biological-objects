# Exception Mapping Contract

## Overview

The `ApiExceptionMapper` sits at the infrastructure boundary (`infrastructure.exceptions`) and translates domain exceptions (`ApiException`) into client-facing HTTP responses.

## Boundary Contract Specification

### HTTP Status Code Resolution

| Domain Error (`GeneralError`) | Domain Code | HTTP Status Code | JAX-RS Resolution |
|-------------------------------|-------------|------------------|-------------------|
| `BAD_REQUEST` | `400` | `400 Bad Request` | `Response.status(400)` |
| `UNAUTHORIZED` | `401` | `401 Unauthorized` | `Response.status(401)` |
| `FORBIDDEN` | `403` | `403 Forbidden` | `Response.status(403)` |
| `NOT_FOUND` | `404` | `404 Not Found` | `Response.status(404)` |
| `UNPROCESSABLE_ENTITY` | `422` | `422 Unprocessable Entity` | `Response.status(422)` |
| `INTERNAL_SERVER_ERROR` | `500` | `500 Internal Server Error` | `Response.status(500)` |

### Error Response Payload Schema

- **Content-Type**: `application/json`

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "ApiResponseError",
  "type": "object",
  "properties": {
    "code": {
      "type": "string",
      "description": "Custom error code or GeneralError code"
    },
    "message": {
      "type": "string",
      "description": "Custom error message or GeneralError default message"
    }
  },
  "required": ["code", "message"]
}
```

### Example: Unprocessable Entity (422)

```http
HTTP/1.1 422 Unprocessable Entity
Content-Type: application/json

{
  "code": "422",
  "message": "Unprocessable Entity"
}
```
