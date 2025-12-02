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
    compararItinerarios("A1", "HOU", "BNA", VuelosA.vuelosA1, aeropuertos)
    compararItinerarios("A2", "HOU", "BNA", VuelosA.vuelosA2, aeropuertos)
    compararItinerarios("A3", "HOU", "BNA", VuelosA.vuelosA3, aeropuertos)
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
    // TODO: Implementar cuando tengas itinerariosTiempo e itinerariosTiempoPar
    println(f"$dataset%-12s | $origen→$destino%-8s | TODO")
  }

  def benchmarkTiempoCurso(): Unit = {
    println("\n// Ejemplos del curso:")
    compararTiempo("Curso-1", "CTG", "PTY", vuelosCurso, aeropuertosCurso)
    // ... etc
  }

  // ============================================
  // BENCHMARKS PARA: itinerariosEscalas
  // ============================================

  // Configuración más ligera para escalas (evita desbordamiento de memoria)
  val lightConfig = config(
    Key.exec.minWarmupRuns := 2,
    Key.exec.maxWarmupRuns := 3,
    Key.exec.benchRuns     := 3,
    Key.verbose            := false
  ) withWarmer(new Warmer.Default)

  def compararEscalas(dataset: String, origen: String, destino: String,
                      vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): Unit = {
    if (vuelos.isEmpty) {
      println(f"$dataset%-12s | $origen→$destino%-8s | ${0}%-6d | ${0}%-12d | ${0.0}%.3f | ${0.0}%.3f | N/A")
      return
    }

    // Forzar GC antes de cada benchmark para liberar memoria
    System.gc()

    // Medir tiempo secuencial - crear función dentro del measure para evitar retener referencias
    var countSeq = 0
    val timeSeq = lightConfig measure {
      val its = itinerariosEscalas(vuelos, aeropuertos)
      val result = its(origen, destino)
      countSeq = result.length
      result // se descarta después de medir
    }

    System.gc()

    // Medir tiempo paralelo
    var countPar = 0
    val timePar = lightConfig measure {
      val itsPar = itinerariosEscalasPar(vuelos, aeropuertos)
      val result = itsPar(origen, destino)
      countPar = result.length
      result
    }

    // Verificar correctitud con una sola ejecución (fuera del benchmark)
    System.gc()
    val resultSeq = itinerariosEscalas(vuelos, aeropuertos)(origen, destino)
    val resultPar = itinerariosEscalasPar(vuelos, aeropuertos)(origen, destino)
    val correct = resultSeq.toSet == resultPar.toSet

    val timeSeqMs = timeSeq.value * 1000.0
    val timeParMs = timePar.value * 1000.0
    val speedup = if (timeParMs == 0.0) Double.PositiveInfinity else timeSeqMs / timeParMs

    println(f"$dataset%-12s | $origen→$destino%-8s | ${vuelos.length}%-6d | ${countSeq}%-12d | $timeSeqMs%12.3f | $timeParMs%10.3f | $speedup%6.2fx")

    if (!correct) {
      println("⚠️  ERROR: Los resultados secuencial y paralelo no coinciden!")
      println(s"   Seq: ${resultSeq.length} itinerarios, Par: ${resultPar.length} itinerarios")
    }

    // Liberar referencias explícitamente
    System.gc()
  }

  def benchmarkEscalasCurso(): Unit = {
    println("\n// Ejemplos del curso:")
    compararEscalas("Curso-1", "CTG", "PTY", vuelosCurso, aeropuertosCurso)
    compararEscalas("Curso-2", "CTG", "PTY", vuelosCurso, aeropuertosCurso)
    compararEscalas("Curso-3", "CTG", "PTY", vuelosCurso, aeropuertosCurso)
    compararEscalas("Curso-4", "MAD", "SVO", vuelosCurso, aeropuertosCurso)
  }

  def benchmarkEscalasA(): Unit = {
    println("\n// Dataset A (15 vuelos):")
    // A1
    compararEscalas("A1-1", "HOU", "MSY", VuelosA.vuelosA1, aeropuertos)
    compararEscalas("A1-2", "MSY", "BNA", VuelosA.vuelosA1, aeropuertos)
    compararEscalas("A1-3", "DFW", "ORD", VuelosA.vuelosA1, aeropuertos)
    // A2
    compararEscalas("A2-1", "DFW", "ORD", VuelosA.vuelosA2, aeropuertos)
    compararEscalas("A2-2", "SFO", "BNA", VuelosA.vuelosA2, aeropuertos)
    compararEscalas("A2-3", "PHX", "LAX", VuelosA.vuelosA2, aeropuertos)
    // A3
    compararEscalas("A3-1", "MIA", "HOU", VuelosA.vuelosA3, aeropuertos)
    compararEscalas("A3-2", "LAX", "MIA", VuelosA.vuelosA3, aeropuertos)
    compararEscalas("A3-3", "DFW", "SFO", VuelosA.vuelosA3, aeropuertos)
  }

  def benchmarkEscalasB(): Unit = {
    println("\n// Dataset B (40 vuelos):")
    // B1
    compararEscalas("B1-1", "DFW", "ORD", VuelosB.vuelosB1, aeropuertos)
    compararEscalas("B1-2", "DFW", "DCA", VuelosB.vuelosB1, aeropuertos)
    compararEscalas("B1-3", "ORD", "LAX", VuelosB.vuelosB1, aeropuertos)
    // B2
    compararEscalas("B2-1", "DFW", "ORD", VuelosB.vuelosB2, aeropuertos)
    compararEscalas("B2-2", "DFW", "DCA", VuelosB.vuelosB2, aeropuertos)
    compararEscalas("B2-3", "ATL", "SEA", VuelosB.vuelosB2, aeropuertos)
    // B3
    compararEscalas("B3-1", "DFW", "ORD", VuelosB.vuelosB3, aeropuertos)
    compararEscalas("B3-2", "DFW", "DCA", VuelosB.vuelosB3, aeropuertos)
    compararEscalas("B3-3", "ORD", "MIA", VuelosB.vuelosB3, aeropuertos)
  }

  def benchmarkEscalasC(): Unit = {
    println("\n// Dataset C (100 vuelos):")
    // C1
    compararEscalas("C1-1", "ORD", "TPA", VuelosC.vuelosC1, aeropuertos)
    compararEscalas("C1-2", "DFW", "MIA", VuelosC.vuelosC1, aeropuertos)
    compararEscalas("C1-3", "ATL", "LAX", VuelosC.vuelosC1, aeropuertos)
    // C2
    compararEscalas("C2-1", "ORD", "TPA", VuelosC.vuelosC2, aeropuertos)
    compararEscalas("C2-2", "LAX", "JFK", VuelosC.vuelosC2, aeropuertos)
    compararEscalas("C2-3", "SEA", "MIA", VuelosC.vuelosC2, aeropuertos)
    // C3
    compararEscalas("C3-1", "ORD", "TPA", VuelosC.vuelosC3, aeropuertos)
    compararEscalas("C3-2", "DFW", "SEA", VuelosC.vuelosC3, aeropuertos)
    compararEscalas("C3-3", "ATL", "SFO", VuelosC.vuelosC3, aeropuertos)
  }

  def benchmarkEscalasD(): Unit = {
    println("\n// Dataset D (500 vuelos) - ⚠️ PUEDE AGOTAR MEMORIA:")
    compararEscalas("D1", "ORD", "LAX", VuelosD.vuelosD1, aeropuertos)
    compararEscalas("D2", "ORD", "LAX", VuelosD.vuelosD2, aeropuertos)
    compararEscalas("D3", "ORD", "LAX", VuelosD.vuelosD3, aeropuertos)
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