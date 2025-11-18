import Datos._
import Itinerarios._


val itsSalidaCurso = itinerarioSalida(vuelosCurso, aeropuertosCurso)
val itsal1 = itsSalidaCurso("CTG", "PTY", 11, 40)
val itsal2 = itsSalidaCurso("CTG", "PTY", 11, 55)
val itsal3 = itsSalidaCurso("CTG", "PTY", 11, 30)
val itsal4 = itsSalidaCurso("MAD", "SVO", 1, 10) // esto creo que esta fallando, porque deberia devolver un vuelo y retorna vacio

