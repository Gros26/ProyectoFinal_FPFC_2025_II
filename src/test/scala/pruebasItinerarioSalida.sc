import Datos._
import Itinerarios._
import ItinerariosPar._


val itsSalidaCurso = itinerarioSalida(vuelosCurso, aeropuertosCurso)
val itsal1 = itsSalidaCurso("CTG", "PTY", 11, 40)
val itsal2 = itsSalidaCurso("CTG", "PTY", 11, 55)
val itsal3 = itsSalidaCurso("CTG", "PTY", 10, 30)
val itsal4 = itsSalidaCurso("MAD", "SVO", 1, 10)

// En tu worksheet o Main
val itSalida = itinerarioSalida(vuelosCurso, aeropuertosCurso)
val itSalidaPar = itinerarioSalidaPar(vuelosCurso, aeropuertosCurso)

val resultado1 = itSalida("CTG", "PTY", 11, 40)
val resultado2 = itSalidaPar("CTG", "PTY", 11, 40)
val resultado3 = itSalidaPar("CTG", "PTY", 10, 30)
val resultado4 = itSalidaPar("MAD", "SVO", 1, 10)

// Deben ser iguales
println(resultado1 == resultado2)