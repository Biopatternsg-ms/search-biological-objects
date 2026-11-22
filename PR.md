## Descripción
Se ha implementado una nueva funcionalidad que permite buscar objetos biológicos en la base de datos basándose de forma global en sus sinónimos. Esto habilita a los clientes a encontrar coincidencias de un objeto a través de nombres alternativos, retornando todos los objetos biológicos relevantes independientemente del usuario al que pertenezcan.

## Cambios Técnicos Realizados
Siguiendo la Arquitectura Hexagonal del proyecto, se introdujeron los siguientes componentes:

- **Capa de Infraestructura (Entrada):** Se añadió el endpoint `GET /biological-object/search-by-synonym/{synonym}` en `BiologicalObjectController.java` y se documentó con OpenAPI/Swagger.
- **Capa de Aplicación/Dominio:** Se crearon el puerto de entrada `FindBiologicalObjectsBySynonym.java` y su implementación en el caso de uso `FindBiologicalObjectsBySynonymUseCase.java`.
- **Capa de Infraestructura (Salida):** Se actualizó el puerto `BiologicalObjectRepository.java` y su adaptador `BiologicalObjectRepositoryAdapter.java` para realizar la consulta en MongoDB. La consulta `{'synonyms': :synonym}` evalúa el array de sinónimos directamente, sin filtrar por `userId`.
- **DTOs:** Se ajustaron detalles en `BiologicalObjectDTO` para soportar la respuesta de este nuevo endpoint.

## Tipo de Cambio
- [x] Nueva característica (funcionalidad nueva que no rompe código existente - *feat*)
- [ ] Corrección de errores (*fix*)
- [ ] Refactorización (*refactor*)
- [ ] Actualización de documentación (*docs*)

## Checklist
- [x] El código compila correctamente.
- [x] Se siguió el patrón de Arquitectura Hexagonal del proyecto.
- [x] Se aplicó la documentación de OpenAPI para el nuevo endpoint.
