# Sistema de Análisis de Itinerarios - Proyecto FPFC

Este proyecto implementa y compara algoritmos para la búsqueda de itinerarios de vuelos, utilizando tanto enfoques secuenciales como paralelos en Scala. El objetivo es analizar el rendimiento (speedup) obtenido al paralelizar la búsqueda de rutas en diferentes datasets de vuelos.

## Descripción de los Archivos Entregados

El código fuente se encuentra en el directorio `src/main/scala` y está organizado de la siguiente manera:

### Archivos Principales
*   **`Main.scala`**: Punto de entrada de la aplicación. Contiene la lógica para el menú interactivo y el procesamiento de argumentos de línea de comandos. Permite seleccionar qué función y qué dataset ejecutar.
*   **`Benchmarks.scala`**: Contiene la lógica de medición de tiempos y comparación de resultados entre las versiones secuenciales y paralelas. Utiliza `ScalaMeter` para las mediciones.

### Paquete `Datos`
*   **`package.scala`**: Define las estructuras de datos básicas (`Aeropuerto`, `Vuelo`, `Itinerario`) y contiene los datos del ejemplo del curso (`aeropuertosCurso`, `vuelosCurso`).
*   **`VuelosA.scala`**: Dataset A (15 vuelos).
*   **`VuelosB.scala`**: Dataset B (40 vuelos).
*   **`VuelosC.scala`**: Dataset C (100 vuelos).
*   **`VuelosD.scala`**: Dataset D (500 vuelos).

### Paquete `Itinerarios`
*   **`package.scala`** (o archivo correspondiente): Implementación secuencial de las funciones de búsqueda de itinerarios (`itinerarios`, `itinerariosTiempo`, `itinerariosEscalas`, etc.).

### Paquete `ItinerariosPar`
*   **`package.scala`** (o archivo correspondiente): Implementación paralela de las funciones de búsqueda de itinerarios (`itinerariosPar`, `itinerariosTiempoPar`, `itinerariosEscalasPar`, etc.), utilizando colecciones paralelas de Scala.

## Instrucciones de Ejecución

El proyecto utiliza `sbt` para la compilación y ejecución.

### Requisitos
*   Tener instalado `sbt` y `Java` (JDK 8 o superior).

### Ejecución Interactiva
Para iniciar el menú interactivo, simplemente ejecute:

```bash
sbt run
```

Esto mostrará un menú donde podrá seleccionar la función a probar (por ejemplo, "itinerarios vs itinerariosPar") y los datasets deseados.

### Ejecución con Argumentos
También puede ejecutar comandos directamente sin pasar por el menú interactivo:

```bash
sbt "run [funcion] [datasets...]"
```

**Argumentos:**
*   `funcion`: La función a probar.
    *   `itinerarios` o `i`: Prueba `itinerarios` vs `itinerariosPar`.
    *   `escalas` o `e`: Prueba `itinerariosEscalas` vs `itinerariosEscalasPar`.
    *   `tiempo` o `t`: (TODO) Prueba `itinerariosTiempo`.
    *   `aire` o `a`: (TODO) Prueba `itinerariosAire`.
    *   `salida` o `s`: (TODO) Prueba `itinerarioSalida`.
*   `datasets`: Lista de datasets a probar (separados por espacio).
    *   `curso` o `c`: Ejemplos del curso.
    *   `a`, `b`, `c`, `d`: Datasets A, B, C, D respectivamente.
    *   `all` o `todos`: Todos los datasets (excepto D por precaución de memoria).

**Ejemplos:**

1.  Ejecutar benchmarks de itinerarios básicos con el dataset del curso:
    ```bash
    sbt "run itinerarios curso"
    ```

2.  Ejecutar benchmarks de itinerarios con escalas para los datasets A, B y C:
    ```bash
    sbt "run escalas a b c"
    ```

3.  Ver la ayuda:
    ```bash
    sbt "run help"
    ```