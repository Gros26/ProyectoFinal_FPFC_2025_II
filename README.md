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
- Java 8 o superior
- Un entorno compatible como IntelliJ IDEA con plugin de Scala

------------------------------------------------------------
2. Estructura del proyecto
------------------------------------------------------------

El proyecto está organizado de la siguiente manera:

main/
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
      ├── itinerarios/
      │     ├── package.scala
      │     └── Itinerarios.scala   (si aplica)
      │
      ├── itinerariosPar/
      │     ├── package.scala
      │     └── ItinerariosPar.scala (si aplica)
      │
      ├── Benchmarks.scala
      └── Main.scala

test/
 └── scala/
      ├── Pruebas.sc
      ├── PruebasItinerarios.sc
      ├── pruebasItinerarioSalida.sc
      ├── PruebasItinerariosTiempo.sc
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

● itinerarios/
  Contiene la implementación **secuencial** de la función 
  `itinerarios`, la cual construye todos los itinerarios posibles 
  entre dos aeropuertos sin repetir nodos (caminos simples).  

  La función realiza una búsqueda exhaustiva DFS pura y está 
  estructurada mediante funciones auxiliares y recursión estructural. 
  Se garantiza corrección mediante una demostración por inducción 
  estructural incluida en el informe PDF del proyecto.

  Esta versión representa la línea base del comportamiento del 
  algoritmo, siendo la referencia para validar la versión paralela.

● itinerariosPar/
  Contiene la implementación **paralela** `itinerariosPar`, 
  que reproduce exactamente la misma lógica de búsqueda del módulo 
  secuencial, pero incorpora paralelismo controlado mediante:
  
  - Umbrales mínimos de paralelización
  - Control de profundidad para evitar sobrecostos
  - División del espacio de búsqueda en tareas independientes
    cuando el conjunto de vuelos disponibles es suficientemente grande
  
  La equivalencia entre `itinerarios` e `itinerariosPar` está 
  demostrada formalmente en el informe, mediante inducción sobre 
  la estructura del árbol de búsqueda generado por la recursión.

  Esta versión se usa como base para las funciones extendidas que 
  calculan itinerarios mínimos por tiempo.

● Benchmarks.scala
  Ejecuta mediciones de rendimiento para comparar la versión
  secuencial y la paralela (incluyendo las variantes por tiempo),
  generando resultados como los presentados en el informe.

● Main.scala
  Punto de entrada principal del proyecto. Permite ejecutar un caso
  de prueba o invocar manualmente las funciones desarrolladas.

● test/scala/
  Conjunto de scripts de prueba (archivos .sc) que validan distintas 
  funcionalidades:

  - Pruebas.sc → pruebas generales del sistema
  - PruebasItinerarios.sc → verificación de itinerarios secuenciales
  - pruebasItinerarioSalida.sc → análisis de salidas específicas
  - PruebasItinerariosTiempo.sc → pruebas de itinerarios ordenados 
                                  por tiempo
  - PruebasPar.sc → verificación de comportamiento paralelo

------------------------------------------------------------
4. Cómo compilar el proyecto
------------------------------------------------------------

Desde la raíz del proyecto, ejecutar:

    sbt compile

Esto descargará las dependencias necesarias y compilará todos los
módulos del proyecto.

------------------------------------------------------------
5. Cómo ejecutar el proyecto
------------------------------------------------------------

Para ejecutar el archivo Main.scala:

    sbt run

Para correr los benchmarks:

    sbt "runMain Benchmarks"

------------------------------------------------------------
6. Cómo ejecutar las pruebas
------------------------------------------------------------

Los archivos dentro de test/scala son scripts .sc.
Pueden ejecutarse desde IntelliJ usando "Scala Worksheets"
o desde consola con amm:

    amm test/scala/Pruebas.sc

------------------------------------------------------------
7. Notas finales
------------------------------------------------------------
- El dataset D no puede ser procesado completamente por la 
  explosión combinatoria, como se discute en el informe.
- La implementación paralela mejora el rendimiento solo cuando 
  el número de itinerarios es suficientemente grande (casos grupo C).

============================================================
FIN DEL ARCHIVO README.txt
============================================================