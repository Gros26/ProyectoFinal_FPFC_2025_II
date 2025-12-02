============================================================
PROYECTO FINAL – Programación Funcional y Paralela (Scala)
============================================================

Este archivo describe la estructura del proyecto, los archivos
incluidos, su propósito y las instrucciones para compilar y 
ejecutar las distintas partes del programa.

------------------------------------------------------------
1. Requerimientos del sistema
------------------------------------------------------------

Para ejecutar el proyecto se requiere:

- Scala 2.13.x
- SBT (Scala Build Tool)
- Java 11, 17 o 21 (⚠️ Java 24 no es compatible con Scala 2.13.10)
- Un entorno compatible como IntelliJ IDEA con plugin de Scala

------------------------------------------------------------
2. Estructura del proyecto
------------------------------------------------------------

El proyecto está organizado de la siguiente manera:

src/
 └── main/
      └── scala/
           ├── common/
           │     └── package.scala
           │
           ├── Datos/
           │     ├── package.scala
           │     ├── VuelosA.scala
           │     ├── VuelosB.scala
           │     ├── VuelosC.scala
           │     └── VuelosD.scala
           │
           ├── Itinerarios/
           │     └── package.scala
           │
           ├── ItinerariosPar/
           │     └── package.scala
           │
           ├── Benchmarks.scala
           └── Main.scala

 └── test/
      └── scala/
           ├── Pruebas.sc
           ├── PruebasItinerarios.sc
           ├── PruebaItinerariosEscalas.sc
           ├── pruebasItinerarioSalida.sc
           └── PruebasPar.sc

------------------------------------------------------------
3. Descripción de directorios y módulos
------------------------------------------------------------

● common/
  Contiene definiciones y utilidades generales compartidas
  entre distintos módulos del proyecto (tipos, alias, helpers).
  Aquí se centralizan elementos que se usan de manera transversal.

● Datos/
  Incluye los datasets provistos por el curso (A, B, C, D),
  cada uno representado como una lista de vuelos declarada en
  archivos independientes. Estos datos se utilizan en las pruebas
  del programa y en los benchmarks de rendimiento.

● Itinerarios/
  Contiene las implementaciones **secuenciales** de las funciones:
  
  - `itinerarios`: Construye todos los itinerarios posibles 
    entre dos aeropuertos sin repetir nodos (caminos simples).
    Realiza búsqueda exhaustiva DFS con recursión estructural.
  
  - `itinerariosEscalas`: Retorna los itinerarios con el menor
    número total de escalas (técnicas + transbordos).
  
  - `itinerarioSalida`: Dado un horario de llegada máximo,
    retorna el itinerario que permite salir más tarde.

● ItinerariosPar/
  Contiene las implementaciones **paralelas**:
  
  - `itinerariosPar`: Versión paralela de `itinerarios` con:
    * Umbrales mínimos de paralelización (UMBRAL_PAR = 4)
    * Control de profundidad (MAX_PROF_PAR = 2)
    * División del espacio de búsqueda en tareas independientes
  
  - `itinerariosEscalasPar`: Versión paralela de `itinerariosEscalas`
    que utiliza colecciones paralelas para filtrar resultados.

● Benchmarks.scala
  Ejecuta mediciones de rendimiento para comparar versiones
  secuenciales y paralelas usando ScalaMeter. Incluye:
  
  - Benchmarks para `itinerarios` vs `itinerariosPar`
  - Benchmarks para `itinerariosEscalas` vs `itinerariosEscalasPar`
  - Soporte para datasets: Curso, A (15), B (40), C (100), D (500)

● Main.scala
  Punto de entrada principal con dos modos de ejecución:
  
  1. Línea de comandos: sbt "run [función] [datasets...]"
  2. Menú interactivo: sbt run

------------------------------------------------------------
4. Funciones implementadas
------------------------------------------------------------

Estado actual de implementación:

  ✅ itinerarios / itinerariosPar
  ✅ itinerariosEscalas / itinerariosEscalasPar  
  ✅ itinerarioSalida (secuencial)
  ⏳ itinerariosTiempo / itinerariosTiempoPar (pendiente benchmark)
  ⏳ itinerariosAire / itinerariosAirePar (pendiente benchmark)
  ⏳ itinerarioSalidaPar (pendiente)

------------------------------------------------------------
5. Cómo compilar el proyecto
------------------------------------------------------------

Desde la raíz del proyecto, ejecutar:

    sbt compile

Esto descargará las dependencias necesarias y compilará todos los
módulos del proyecto.

------------------------------------------------------------
6. Cómo ejecutar los benchmarks
------------------------------------------------------------

MODO 1: Línea de comandos
-------------------------

  # Benchmarks de itinerarios
  sbt "run itinerarios all"
  sbt "run i curso"
  sbt "run i a b c"

  # Benchmarks de itinerariosEscalas
  sbt "run escalas all"
  sbt "run e curso"
  sbt "run e a b c"

  # Ver ayuda
  sbt "run help"

MODO 2: Menú interactivo
------------------------

  sbt run

  Luego seleccionar:
    1. itinerarios vs itinerariosPar
    3. itinerariosEscalas vs itinerariosEscalasPar

Datasets disponibles:
  - curso, c    : Ejemplos del enunciado del curso
  - a           : Dataset A (15 vuelos)
  - b           : Dataset B (40 vuelos)
  - c           : Dataset C (100 vuelos)
  - d           : Dataset D (500 vuelos) ⚠️ CUIDADO: puede agotar memoria
  - all, todos  : Todos los datasets (excepto D)

------------------------------------------------------------
7. Cómo ejecutar las pruebas
------------------------------------------------------------

Los archivos dentro de test/scala son scripts .sc.
Pueden ejecutarse desde IntelliJ usando "Scala Worksheets"
o desde consola con amm:

    amm test/scala/Pruebas.sc
    amm test/scala/PruebaItinerariosEscalas.sc

------------------------------------------------------------
8. Notas finales
------------------------------------------------------------

- El dataset D no puede ser procesado completamente por la 
  explosión combinatoria, como se discute en el informe.

- La implementación paralela mejora el rendimiento solo cuando 
  el número de itinerarios es suficientemente grande (casos C).

- Si usas Java 24, debes actualizar a Scala 2.13.12+ o usar
  Java 11/17/21 para evitar errores de compilación.

============================================================
FIN DEL ARCHIVO README
============================================================