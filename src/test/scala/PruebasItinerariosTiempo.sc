import Itinerarios._
import Datos._

val itsTiempoCurso = itinerariosTiempo(vuelosCurso, aeropuertosCurso)

// prueba itinerariosTiempo

// Caso 1: Existe ruta directa — 1 itinerario
val itst1 = itsTiempoCurso("MID", "SVCS")

// Caso 2: No existe camino entre los aeropuertos — lista vacía
val itst2 = itsTiempoCurso("CLO", "SVCS")

// Caso 3: Múltiples itinerarios, se esperan los 3 de menor tiempo
val itst3 = itsTiempoCurso("CLO", "SVO")

// Caso 4: Ruta con varias escalas — 2 itinerarios mínimos
val itst4 = itsTiempoCurso("CLO", "MEX")

// Caso 5: Ruta corta con 1–2 itinerarios mínimos
val itst5 = itsTiempoCurso("CTG", "PTY")