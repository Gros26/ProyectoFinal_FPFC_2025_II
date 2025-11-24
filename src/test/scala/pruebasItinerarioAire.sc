import Datos._
import Itinerarios._

val itsAireCurso = itinerariosAire(vuelosCurso,aeropuertosCurso)
//2.1 Aeropuertos incomunicados
val itsAire1 = itsAireCurso("MID", "SVCS")
val itsAire2 = itsAireCurso("CLO", "SVCS")

// 4 itinerarios CLO-SVO

val itsAire3 = itsAireCurso("CLO","SVO")

//2 itinerarios CLO-MEX

val itsAire4 = itsAireCurso("CLO", "MEX")

//2 itinerarios CTG-PTY
val itsAire5 = itsAireCurso("CTG","PTY")