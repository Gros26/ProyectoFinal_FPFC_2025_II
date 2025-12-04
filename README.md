# Proyecto Final - Programación Funcional y Concurrente en Scala

## Información del Curso
- **Curso:** Programación Funcional y Concurrente
- **Período:** 2025-II
- **Universidad:** Universidad del Valle

---

## Descripción del Proyecto

Este proyecto consiste en el desarrollo de un **sistema de búsqueda de itinerarios de vuelos** implementado en el lenguaje de programación funcional **Scala**. El sistema permite encontrar rutas óptimas entre aeropuertos considerando diferentes criterios de optimización.

### Contexto del Problema

El sistema trabaja con la información de vuelos comerciales, donde cada vuelo se representa mediante:
- **Aerolínea (Aln):** Código de dos letras de la aerolínea
- **Número de vuelo (Num):** Número identificador del vuelo
- **Origen (Org):** Código del aeropuerto de salida
- **Hora de salida (HS, MS):** Hora y minutos de salida
- **Destino (Dst):** Código del aeropuerto de llegada
- **Hora de llegada (HL, ML):** Hora y minutos de llegada
- **Escala (Esc):** Número de escalas técnicas del vuelo

Un **itinerario** es una secuencia de vuelos que permite viajar desde un aeropuerto origen hasta un aeropuerto destino, donde el destino de cada vuelo coincide con el origen del siguiente.

---

## Funciones Implementadas

### Versiones Secuenciales

1. **`itinerarios(vuelos, aeropuertos)(a1, a2)`**
   - Genera todos los itinerarios posibles entre dos aeropuertos
   - Retorna una lista de itinerarios sin repetir aeropuertos intermedios

2. **`itinerariosTiempo(vuelos, aeropuertos)(a1, a2)`**
   - Retorna los 3 itinerarios con menor tiempo total de viaje
   - El tiempo incluye vuelo y tiempo de espera en conexiones

3. **`itinerariosEscalas(vuelos, aeropuertos)(a1, a2)`**
   - Retorna los 3 itinerarios con menor número de escalas
   - Considera tanto escalas técnicas como cambios de avión

4. **`itinerarioSalida(vuelos, aeropuertos)(a1, a2, h, m)`**
   - Dado un horario límite de llegada (h:m), encuentra el itinerario que permite salir lo más tarde posible

5. **`itinerariosAire(vuelos, aeropuertos)(a1, a2)`** *(Pendiente)*
   - Retorna los 3 itinerarios con menor tiempo total en el aire

### Versiones Paralelas

Para cada función secuencial se implementó su versión paralela correspondiente:
- `itinerariosPar`
- `itinerariosTiempoPar`
- `itinerariosEscalasPar`
- `itinerarioSalidaPar`
- `itinerariosAirePar` *(Pendiente)*

Las versiones paralelas utilizan el framework de tareas de Scala (ForkJoinPool) para distribuir el trabajo de búsqueda en múltiples hilos.

---

## Estructura del Proyecto

```
src/main/scala/
├── common/          # Utilidades para paralelismo (task, parallel)
├── Datos/           # Datasets de prueba (A, B, C, D)
├── Itinerarios/     # Implementaciones secuenciales
├── ItinerariosPar/  # Implementaciones paralelas
├── Benchmarks.scala # Medición de rendimiento
└── Main.scala       # Punto de entrada

src/test/scala/      # Scripts de prueba (.sc worksheets)
```

---

## Entregables

1. **Código fuente** en Scala con todas las funciones implementadas
2. **Pruebas de corrección** que verifican el funcionamiento de cada función
3. **Benchmarks de rendimiento** comparando versiones secuenciales vs paralelas
4. **Informe técnico** (main.tex) documentando:
   - Diseño e implementación de las funciones
   - Análisis de complejidad
   - Resultados de las pruebas de rendimiento
   - Conclusiones sobre la paralelización

---

## Tecnologías Utilizadas

- **Scala 2.13** - Lenguaje de programación funcional
- **SBT** - Herramienta de construcción
- **ForkJoinPool** - Framework para paralelismo
- **LaTeX** - Documentación del informe

---

## Estado de Implementación

| Función | Secuencial | Paralela |
|---------|:----------:|:--------:|
| itinerarios | ✅ | ✅ |
| itinerariosTiempo | ✅ | ✅ |
| itinerariosEscalas | ✅ | ✅ |
| itinerarioSalida | ✅ | ✅ |
| itinerariosAire | ⏳ | ⏳ |
