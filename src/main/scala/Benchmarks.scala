import org.scalameter._
import Datos._
import Itinerarios._
import ItinerariosPar._
import VuelosA._
import VuelosB._
import VuelosC._
import VuelosD._

object Benchmarks {

  // ============================================
  // CONFIGURACIÓN COMÚN
  // ============================================

  val standardConfig = config(
    Key.exec.minWarmupRuns := 5,
    Key.exec.maxWarmupRuns := 10,
    Key.exec.benchRuns     := 10,
    Key.verbose            := false
  ) withWarmer(new Warmer.Default)

  def imprimirEncabezado(titulo: String): Unit = {
    println("\n═══════════════════════════════════════════════════════════════")
    println(s"           $titulo")
    println("═══════════════════════════════════════════════════════════════\n")
    println("Dataset      | Ruta     | Vuelos | Itinerarios  | T.secuencial | T.paralelo | Speedup")
    println("             | (cod1–cod2) |        | encontrados  |     (ms)     |    (ms)    |")
    println("─────────────┼──────────┼────────┼──────────────┼──────────────┼────────────┼────────")
  }

  def imprimirPie(): Unit = {
    println("─────────────┴──────────┴────────┴──────────────┴──────────────┴────────────┴────────")
  }

  // ============================================
  // BENCHMARKS PARA: itinerarios
  // ============================================

  def compararItinerarios(dataset: String, origen: String, destino: String,
                          vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): Unit = {
    if (vuelos.isEmpty) {
      println(f"$dataset%-12s | $origen→$destino%-8s | ${0}%-6d | ${0}%-12d | ${0.0}%.3f | ${0.0}%.3f | N/A")
      return
    }

    val its    = itinerarios(vuelos, aeropuertos)
    val itsPar = itinerariosPar(vuelos, aeropuertos)

    var resultSeq: List[Itinerario] = Nil
    var resultPar: List[Itinerario] = Nil

    val timeSeq = standardConfig measure {
      resultSeq = its(origen, destino)
    }

    val timePar = standardConfig measure {
      resultPar = itsPar(origen, destino)
    }

    val timeSeqMs = timeSeq.value * 1000.0
    val timeParMs = timePar.value * 1000.0
    val speedup = if (timeParMs == 0.0) Double.PositiveInfinity else timeSeqMs / timeParMs
    val correct = resultSeq.toSet == resultPar.toSet

    println(f"$dataset%-12s | $origen→$destino%-8s | ${vuelos.length}%-6d | ${resultSeq.length}%-12d | $timeSeqMs%12.3f | $timeParMs%10.3f | $speedup%6.2fx")

    if (!correct) {
      println("⚠️  ERROR: Los resultados secuencial y paralelo no coinciden!")
    }
  }

  def benchmarkItinerariosCurso(): Unit = {
    println("\n// Ejemplos del curso:")
    compararItinerarios("Curso-1", "CTG", "PTY", vuelosCurso, aeropuertosCurso)
    compararItinerarios("Curso-2", "CLO", "MEX", vuelosCurso, aeropuertosCurso)
    compararItinerarios("Curso-3", "CLO", "SVO", vuelosCurso, aeropuertosCurso)
    compararItinerarios("Curso-4", "MID", "SVCS", vuelosCurso, aeropuertosCurso)
  }

  def benchmarkItinerariosA(): Unit = {
    println("\n// Dataset A (15 vuelos):")
    compararItinerarios("A1", "HOU", "MSY", VuelosA.vuelosA1, aeropuertos)
    compararItinerarios("A2", "MSY", "BNA", VuelosA.vuelosA2, aeropuertos)
    compararItinerarios("A3", "DFW", "ORD", VuelosA.vuelosA3, aeropuertos)
  }

  def benchmarkItinerariosB(): Unit = {
    println("\n// Dataset B (40 vuelos):")
    compararItinerarios("B1", "DFW", "ORD", VuelosB.vuelosB1, aeropuertos)
    compararItinerarios("B2", "DFW", "ORD", VuelosB.vuelosB2, aeropuertos)
    compararItinerarios("B3", "DFW", "ORD", VuelosB.vuelosB3, aeropuertos)
  }

  def benchmarkItinerariosC(): Unit = {
    println("\n// Dataset C (100 vuelos):")
    compararItinerarios("C1", "ORD", "TPA", VuelosC.vuelosC1, aeropuertos)
    compararItinerarios("C2", "ORD", "TPA", VuelosC.vuelosC2, aeropuertos)
    compararItinerarios("C3", "ORD", "TPA", VuelosC.vuelosC3, aeropuertos)
  }

  def benchmarkItinerariosD(): Unit = {
    println("\n// Dataset D (500 vuelos) - ⚠️ PUEDE AGOTAR MEMORIA:")
    compararItinerarios("D1", "ORD", "LAX", VuelosD.vuelosD1, aeropuertos)
    compararItinerarios("D2", "ORD", "LAX", VuelosD.vuelosD2, aeropuertos)
    compararItinerarios("D3", "ORD", "LAX", VuelosD.vuelosD3, aeropuertos)
  }

  // ============================================
  // BENCHMARKS PARA: itinerariosTiempo
  // ============================================

  def compararTiempo(dataset: String, origen: String, destino: String,
                     vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): Unit = {

    if (vuelos.isEmpty) {
      println(f"$dataset%-12s | $origen→$destino%-8s | ${0}%-6d | ${0}%-12d | ${0.0}%.3f | ${0.0}%.3f | N/A")
      return
    }

    val itsTiempo    = itinerariosTiempo(vuelos, aeropuertos)
    val itsTiempoPar = itinerariosTiempoPar(vuelos, aeropuertos)

    var resultSeq: List[Itinerario] = Nil
    var resultPar: List[Itinerario] = Nil

    val timeSeq = standardConfig measure {
      resultSeq = itsTiempo(origen, destino)
    }

    val timePar = standardConfig measure {
      resultPar = itsTiempoPar(origen, destino)
    }

    val timeSeqMs = timeSeq.value * 1000.0
    val timeParMs = timePar.value * 1000.0
    val speedup = if (timeParMs == 0.0) Double.PositiveInfinity else timeSeqMs / timeParMs
    val correct = resultSeq.toSet == resultPar.toSet

    println(f"$dataset%-12s | $origen→$destino%-8s | ${vuelos.length}%-6d | ${resultSeq.length}%-12d | $timeSeqMs%12.3f | $timeParMs%10.3f | $speedup%6.2fx")

    if (!correct) {
      println("⚠️  ERROR: Los resultados secuencial y paralelo no coinciden!")
    }
  }

  def benchmarkTiempoCurso(): Unit = {
    println("\n// Ejemplos del curso (Tiempo):")
    compararTiempo("Curso-T1", "CTG", "PTY", vuelosCurso, aeropuertosCurso)
    compararTiempo("Curso-T2", "CLO", "MEX", vuelosCurso, aeropuertosCurso)
    compararTiempo("Curso-T3", "CLO", "SVO", vuelosCurso, aeropuertosCurso)
    compararTiempo("Curso-T4", "MID", "SVCS", vuelosCurso, aeropuertosCurso)
  }

  def benchmarkTiempoA(): Unit = {
    println("\n// Dataset A (15 vuelos) — Tiempo:")
    compararTiempo("A1-T", "HOU", "BNA", VuelosA.vuelosA1, aeropuertos)
    compararTiempo("A2-T", "HOU", "BNA", VuelosA.vuelosA2, aeropuertos)
    compararTiempo("A3-T", "HOU", "BNA", VuelosA.vuelosA3, aeropuertos)
  }

  def benchmarkTiempoB(): Unit = {
    println("\n// Dataset B (40 vuelos) — Tiempo:")
    compararTiempo("B1-T", "DFW", "ORD", VuelosB.vuelosB1, aeropuertos)
    compararTiempo("B2-T", "DFW", "ORD", VuelosB.vuelosB2, aeropuertos)
    compararTiempo("B3-T", "DFW", "ORD", VuelosB.vuelosB3, aeropuertos)
  }

  def benchmarkTiempoC(): Unit = {
    println("\n// Dataset C (100 vuelos) — Tiempo:")
    compararTiempo("C1-T", "ORD", "TPA", VuelosC.vuelosC1, aeropuertos)
    compararTiempo("C2-T", "ORD", "TPA", VuelosC.vuelosC2, aeropuertos)
    compararTiempo("C3-T", "ORD", "TPA", VuelosC.vuelosC3, aeropuertos)
  }

  def benchmarkTiempoD(): Unit = {
  println("\n// Dataset D (500 vuelos) — Tiempo:")
  compararTiempo("D1-T", "ORD", "LAX", VuelosD.vuelosD1, aeropuertos)
  compararTiempo("D2-T", "ORD", "LAX", VuelosD.vuelosD2, aeropuertos)
  compararTiempo("D3-T", "ORD", "LAX", VuelosD.vuelosD3, aeropuertos)
}

  // ============================================
  // BENCHMARKS PARA: itinerariosEscalas
  // ============================================

  def compararEscalas(dataset: String, origen: String, destino: String,
                      vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): Unit = {
    // TODO: Implementar cuando tengas itinerariosEscalas e itinerariosEscalasPar
    println(f"$dataset%-12s | $origen→$destino%-8s | TODO")
  }

  def benchmarkEscalasCurso(): Unit = {
    println("\n// Ejemplos del curso:")
    compararEscalas("Curso-1", "CTG", "PTY", vuelosCurso, aeropuertosCurso)
    // ... etc
  }

  // ============================================
  // BENCHMARKS PARA: itinerariosAire
  // ============================================

  def compararAire(dataset: String, origen: String, destino: String,
                   vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): Unit = {
    // TODO: Implementar cuando tengas itinerariosAire e itinerariosAirePar
    println(f"$dataset%-12s | $origen→$destino%-8s | TODO")
  }

  def benchmarkAireCurso(): Unit = {
    println("\n// Ejemplos del curso:")
    compararAire("Curso-1", "CTG", "PTY", vuelosCurso, aeropuertosCurso)
    // ... etc
  }

  // ============================================
  // BENCHMARKS PARA: itinerarioSalida
  // ============================================

  def compararSalida(dataset: String, origen: String, destino: String, hora: Int, minuto: Int,
                     vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): Unit = {
    // TODO: Implementar cuando tengas itinerarioSalida e itinerarioSalidaPar
    println(f"$dataset%-12s | $origen→$destino%-8s | TODO")
  }

  def benchmarkSalidaCurso(): Unit = {
    println("\n// Ejemplos del curso:")
    compararSalida("Curso-1", "CTG", "PTY", 11, 40, vuelosCurso, aeropuertosCurso)
    // ... etc
  }
}