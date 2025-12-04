import Datos._
import Itinerarios._
import ItinerariosPar._
import VuelosA._
import VuelosB._
import VuelosC._

// ============================================
// PRUEBAS: itinerariosEscalas vs itinerariosEscalasPar
// ============================================

println("Probando itinerariosEscalas (Secuencial vs Paralelo)...")

// 1. Curso
val itsEscalasCurso = itinerariosEscalas(vuelosCurso, aeropuertosCurso)
val itsEscalasParCurso = itinerariosEscalasPar(vuelosCurso, aeropuertosCurso)

val resCurso1 = itsEscalasCurso("MID", "SVCS")
val resParCurso1 = itsEscalasParCurso("MID", "SVCS")
println(s"Curso MID-SVCS: ${resCurso1 == resParCurso1}")

val resCurso2 = itsEscalasCurso("CLO", "SVCS")
val resParCurso2 = itsEscalasParCurso("CLO", "SVCS")
println(s"Curso CLO-SVCS: ${resCurso2 == resParCurso2}")

// 2. Dataset A
val itsEscalasA = itinerariosEscalas(vuelosA1, aeropuertos)
val itsEscalasParA = itinerariosEscalasPar(vuelosA1, aeropuertos)

val resA1 = itsEscalasA("HOU", "BNA")
val resParA1 = itsEscalasParA("HOU", "BNA")
println(s"Dataset A HOU-BNA: ${resA1.toSet == resParA1.toSet}")

// 3. Dataset B
val itsEscalasB = itinerariosEscalas(vuelosB1, aeropuertos)
val itsEscalasParB = itinerariosEscalasPar(vuelosB1, aeropuertos)

val resB1 = itsEscalasB("DFW", "ORD")
val resParB1 = itsEscalasParB("DFW", "ORD")
println(s"Dataset B DFW-ORD: ${resB1.toSet == resParB1.toSet}")

// 4. Dataset C
val itsEscalasC = itinerariosEscalas(vuelosC1, aeropuertos)
val itsEscalasParC = itinerariosEscalasPar(vuelosC1, aeropuertos)

val resC1 = itsEscalasC("ORD", "TPA")
val resParC1 = itsEscalasParC("ORD", "TPA")
println(s"Dataset C ORD-TPA: ${resC1.toSet == resParC1.toSet}")
