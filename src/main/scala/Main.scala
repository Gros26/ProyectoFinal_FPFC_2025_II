import org.scalameter._
import Datos._
import Itinerarios._
import ItinerariosPar._
import VuelosA._
import VuelosB._
import VuelosC._
import VuelosD._

object Main extends App {

  println("═══════════════════════════════════════════════════════════════")
  println("                ANÁLISIS COMPARATIVO - ITINERARIOS")
  println("═══════════════════════════════════════════════════════════════\n")

  // ============================================
  // CONFIGURACIÓN DE MEDICIÓN (más ligera)
  // ============================================

  val standardConfig = config(
    Key.exec.minWarmupRuns := 5,
    Key.exec.maxWarmupRuns := 10,
    Key.exec.benchRuns     := 10,
    Key.verbose            := false
  ) withWarmer(new Warmer.Default)

  // ============================================
  // FUNCIÓN PARA COMPARAR RENDIMIENTO
  // ============================================

  def compararRendimiento(dataset: String, origen: String, destino: String,
                          vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): Unit = {

    if (vuelos.isEmpty) {
      println(f"$dataset%-12s | $origen→$destino%-8s | ${0}%-6d | ${0}%-12d | ${0.0}%.3f | ${0.0}%.3f | N/A")
      return
    }

    val its    = itinerarios(vuelos, aeropuertos)
    val itsPar = itinerariosPar(vuelos, aeropuertos)

    // Guardar resultados UNA sola vez
    var resultSeq: List[Itinerario] = Nil
    var resultPar: List[Itinerario] = Nil

    val timeSeq = standardConfig measure {
      resultSeq = its(origen, destino)
    }

    val timePar = standardConfig measure {
      resultPar = itsPar(origen, destino)
    }

    // Pasar a milisegundos
    val timeSeqMs = timeSeq.value * 1000.0
    val timeParMs = timePar.value * 1000.0

    val speedup =
      if (timeParMs == 0.0) Double.PositiveInfinity
      else timeSeqMs / timeParMs

    // Verificar correctitud (mismo conjunto de itinerarios)
    val correct = resultSeq.toSet == resultPar.toSet

    // Imprimir en formato tabla
    println(f"$dataset%-12s | $origen→$destino%-8s | ${vuelos.length}%-6d | ${resultSeq.length}%-12d | $timeSeqMs%12.3f | $timeParMs%10.3f | $speedup%6.2fx")

    if (!correct) {
      println("⚠️  ERROR: Los resultados secuencial y paralelo no coinciden!")
    }
  }

  // ============================================
  // ENCABEZADO TABLA
  // ============================================

  println("Dataset      | Ruta     | Vuelos | Itinerarios  | T.secuencial | T.paralelo | Speedup")
  println("             | (cod1–cod2) |        | encontrados  |     (ms)     |    (ms)    |")
  println("─────────────┼──────────┼────────┼──────────────┼──────────────┼────────────┼────────")

  // ============================================
  // DATOS CURSO (ejemplos del enunciado)
  // ============================================


  compararRendimiento("Curso-1", "CTG", "PTY", vuelosCurso, aeropuertosCurso)
  compararRendimiento("Curso-2", "CLO", "MEX", vuelosCurso, aeropuertosCurso)
  compararRendimiento("Curso-3", "CLO", "SVO", vuelosCurso, aeropuertosCurso)
  compararRendimiento("Curso-4", "MID", "SVCS", vuelosCurso, aeropuertosCurso)

  // ============================================
  // DATASETS A (15 vuelos) – misma ruta HOU→BNA
  // ============================================

  compararRendimiento("A1", "HOU", "BNA", VuelosA.vuelosA1, aeropuertos)
  compararRendimiento("A2", "HOU", "BNA", VuelosA.vuelosA2, aeropuertos)
  compararRendimiento("A3", "HOU", "BNA", VuelosA.vuelosA3, aeropuertos)

  // ============================================
  // DATASETS B (40 vuelos) – misma ruta DFW→ORD
  // ============================================

  compararRendimiento("B1", "DFW", "ORD", VuelosB.vuelosB1, aeropuertos)
  compararRendimiento("B2", "DFW", "ORD", VuelosB.vuelosB2, aeropuertos)
  compararRendimiento("B3", "DFW", "ORD", VuelosB.vuelosB3, aeropuertos)

  // ============================================
  // DATASETS C (100 vuelos) – misma ruta ORD→TPA
  // ============================================

  compararRendimiento("C1", "ORD", "TPA", VuelosC.vuelosC1, aeropuertos)
  compararRendimiento("C2", "ORD", "TPA", VuelosC.vuelosC2, aeropuertos)
  compararRendimiento("C3", "ORD", "TPA", VuelosC.vuelosC3, aeropuertos)

  // ============================================
  // DATASETS D (500 vuelos) – misma ruta ORD→LAX
  // ============================================

  /// aqui llegamos al limite
  /*
  compararRendimiento("D1", "ORD", "LAX", VuelosD.vuelosD1, aeropuertos)
  compararRendimiento("D2", "ORD", "LAX", VuelosD.vuelosD2, aeropuertos)
  compararRendimiento("D3", "ORD", "LAX", VuelosD.vuelosD3, aeropuertos)

   */

  println("─────────────┴──────────┴────────┴──────────────┴──────────────┴────────────┴────────")
  println("\n📊 Análisis comparativo terminado.")
}
