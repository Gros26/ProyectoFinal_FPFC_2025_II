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
    compararItinerarios("Curso-1", "CLO", "SVO", vuelosCurso, aeropuertosCurso)
    compararItinerarios("Curso-2", "CLO", "SVO", vuelosCurso, aeropuertosCurso)
    compararItinerarios("Curso-3", "CLO", "SVO", vuelosCurso, aeropuertosCurso)
    compararItinerarios("Curso-4", "CLO", "SVO", vuelosCurso, aeropuertosCurso)
  }

  // En Benchmarks.scala, modifica:

  def benchmarkItinerariosA(): Unit = {
    println("\n// Dataset A1 (15 vuelos):")
    compararItinerarios("A1-1", "HOU", "MSY", VuelosA.vuelosA1, aeropuertos)
    compararItinerarios("A1-2", "MSY", "BNA", VuelosA.vuelosA1, aeropuertos)
    compararItinerarios("A1-3", "DFW", "ORD", VuelosA.vuelosA1, aeropuertos)
    
    println("\n// Dataset A2 (15 vuelos):")
    compararItinerarios("A2-1", "DFW", "ORD", VuelosA.vuelosA2, aeropuertos)
    compararItinerarios("A2-2", "SFO", "BNA", VuelosA.vuelosA2, aeropuertos)
    compararItinerarios("A2-3", "PHX", "LAX", VuelosA.vuelosA2, aeropuertos)
    
    println("\n// Dataset A3 (15 vuelos):")
    compararItinerarios("A3-1", "MIA", "HOU", VuelosA.vuelosA3, aeropuertos)
    compararItinerarios("A3-2", "LAX", "MIA", VuelosA.vuelosA3, aeropuertos)
    compararItinerarios("A3-3", "DFW", "SFO", VuelosA.vuelosA3, aeropuertos)
  }


  def benchmarkItinerariosB(): Unit = {
    println("\n// Dataset B1 (40 vuelos):")
    compararItinerarios("B1-1", "DFW", "ORD", VuelosB.vuelosB1, aeropuertos)
    compararItinerarios("B1-2", "DFW", "DCA", VuelosB.vuelosB1, aeropuertos)
    compararItinerarios("B1-3", "ORD", "LAX", VuelosB.vuelosB1, aeropuertos)
    
    println("\n// Dataset B2 (40 vuelos):")
    compararItinerarios("B2-1", "DFW", "ORD", VuelosB.vuelosB2, aeropuertos)
    compararItinerarios("B2-2", "DFW", "DCA", VuelosB.vuelosB2, aeropuertos)
    compararItinerarios("B2-3", "ATL", "SEA", VuelosB.vuelosB2, aeropuertos)
    
    println("\n// Dataset B3 (40 vuelos):")
    compararItinerarios("B3-1", "DFW", "ORD", VuelosB.vuelosB3, aeropuertos)
    compararItinerarios("B3-2", "DFW", "DCA", VuelosB.vuelosB3, aeropuertos)
    compararItinerarios("B3-3", "ORD", "MIA", VuelosB.vuelosB3, aeropuertos)
  }

  def benchmarkItinerariosC(): Unit = {
    println("\n// Dataset C1 (100 vuelos):")
    compararItinerarios("C1-1", "ORD", "TPA", VuelosC.vuelosC1, aeropuertos)
    compararItinerarios("C1-2", "DFW", "MIA", VuelosC.vuelosC1, aeropuertos)
    compararItinerarios("C1-3", "ATL", "LAX", VuelosC.vuelosC1, aeropuertos)
    
    println("\n// Dataset C2 (100 vuelos):")
    compararItinerarios("C2-1", "ORD", "TPA", VuelosC.vuelosC2, aeropuertos)
    compararItinerarios("C2-2", "LAX", "JFK", VuelosC.vuelosC2, aeropuertos)
    compararItinerarios("C2-3", "SEA", "MIA", VuelosC.vuelosC2, aeropuertos)
    
    println("\n// Dataset C3 (100 vuelos):")
    compararItinerarios("C3-1", "ORD", "TPA", VuelosC.vuelosC3, aeropuertos)
    compararItinerarios("C3-2", "DFW", "SEA", VuelosC.vuelosC3, aeropuertos)
    compararItinerarios("C3-3", "ATL", "SFO", VuelosC.vuelosC3, aeropuertos)
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

  def imprimirEncabezadoSalida(titulo: String): Unit = {
    println("\n═══════════════════════════════════════════════════════════════")
    println(s"           $titulo")
    println("═══════════════════════════════════════════════════════════════\n")
    println("Dataset      | Ruta      | Cita   | Vuelos | Resultado        | T.secuencial | T.paralelo | Speedup")
    println("             | (cod1–cod2)| (H:M)  |        | (sale H:M)       |     (ms)     |    (ms)    |")
    println("─────────────┼───────────┼────────┼────────┼──────────────────┼──────────────┼────────────┼────────")
  }

  def compararSalida(dataset: String, origen: String, destino: String, hora: Int, minuto: Int,
                     vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): Unit = {
    if (vuelos.isEmpty) {
      println(f"$dataset%-12s | $origen→$destino%-10s | $hora%02d:$minuto%02d | ${0}%-6d | Vacío            | ${0.0}%.3f | ${0.0}%.3f | N/A")
      return
    }

    val itSal    = itinerarioSalida(vuelos, aeropuertos)
    val itSalPar = itinerarioSalidaPar(vuelos, aeropuertos)

    var resultSeq: Itinerario = Nil
    var resultPar: Itinerario = Nil

    val timeSeq = standardConfig measure {
      resultSeq = itSal(origen, destino, hora, minuto)
    }

    val timePar = standardConfig measure {
      resultPar = itSalPar(origen, destino, hora, minuto)
    }

    val timeSeqMs = timeSeq.value * 1000.0
    val timeParMs = timePar.value * 1000.0
    val speedup = if (timeParMs == 0.0) Double.PositiveInfinity else timeSeqMs / timeParMs
    val correct = resultSeq == resultPar

    // Formatear resultado
    val resultadoStr = if (resultSeq.isEmpty) {
      "Vacío"
    } else {
      val primerVuelo = resultSeq.head
      f"${primerVuelo.HS}%02d:${primerVuelo.MS}%02d (${resultSeq.length} vuelos)"
    }

    println(f"$dataset%-12s | $origen→$destino%-10s | $hora%02d:$minuto%02d | ${vuelos.length}%-6d | $resultadoStr%-16s | $timeSeqMs%12.3f | $timeParMs%10.3f | $speedup%6.2fx")

    if (!correct) {
      println("⚠️  ERROR: Los resultados secuencial y paralelo no coinciden!")
      println(s"   Secuencial: $resultSeq")
      println(s"   Paralelo:   $resultPar")
    }
  }

  def benchmarkSalidaCurso(): Unit = {
    println("\n// Ejemplos del curso:")
    compararSalida("Curso-1", "CTG", "PTY", 11, 40, vuelosCurso, aeropuertosCurso)
    compararSalida("Curso-2", "CTG", "PTY", 11, 55, vuelosCurso, aeropuertosCurso)
    compararSalida("Curso-3", "CTG", "PTY", 10, 30, vuelosCurso, aeropuertosCurso)
    compararSalida("Curso-4", "MAD", "SVO", 1, 10, vuelosCurso, aeropuertosCurso)
  }

  def benchmarkSalidaA(): Unit = {
  println("\n// Dataset A1 (15 vuelos):")
  compararSalida("A1-1", "HOU", "MSY", 18, 30, VuelosA.vuelosA1, aeropuertos)
  compararSalida("A1-2", "MSY", "BNA", 15, 0, VuelosA.vuelosA1, aeropuertos)
  compararSalida("A1-3", "DFW", "ORD", 20, 0, VuelosA.vuelosA1, aeropuertos)
  
  println("\n// Dataset A2 (15 vuelos):")
  compararSalida("A2-1", "DFW", "ORD", 18, 30, VuelosA.vuelosA2, aeropuertos)
  compararSalida("A2-2", "SFO", "BNA", 16, 0, VuelosA.vuelosA2, aeropuertos)
  compararSalida("A2-3", "PHX", "LAX", 14, 30, VuelosA.vuelosA2, aeropuertos)
  
  println("\n// Dataset A3 (15 vuelos):")
  compararSalida("A3-1", "MIA", "HOU", 19, 0, VuelosA.vuelosA3, aeropuertos)
  compararSalida("A3-2", "LAX", "MIA", 17, 30, VuelosA.vuelosA3, aeropuertos)
  compararSalida("A3-3", "DFW", "SFO", 21, 0, VuelosA.vuelosA3, aeropuertos)
  }

  def benchmarkSalidaB(): Unit = {
  println("\n// Dataset B1 (40 vuelos):")
  compararSalida("B1-1", "DFW", "ORD", 18, 30, VuelosB.vuelosB1, aeropuertos)
  compararSalida("B1-2", "DFW", "DCA", 16, 0, VuelosB.vuelosB1, aeropuertos)
  compararSalida("B1-3", "ORD", "LAX", 20, 0, VuelosB.vuelosB1, aeropuertos)
  
  println("\n// Dataset B2 (40 vuelos):")
  compararSalida("B2-1", "DFW", "ORD", 17, 30, VuelosB.vuelosB2, aeropuertos)
  compararSalida("B2-2", "DFW", "DCA", 15, 30, VuelosB.vuelosB2, aeropuertos)
  compararSalida("B2-3", "ATL", "SEA", 19, 0, VuelosB.vuelosB2, aeropuertos)
  
  println("\n// Dataset B3 (40 vuelos):")
  compararSalida("B3-1", "DFW", "ORD", 18, 0, VuelosB.vuelosB3, aeropuertos)
  compararSalida("B3-2", "DFW", "DCA", 16, 30, VuelosB.vuelosB3, aeropuertos)
  compararSalida("B3-3", "ORD", "MIA", 21, 30, VuelosB.vuelosB3, aeropuertos)
  }

  def benchmarkSalidaC(): Unit = {
  println("\n// Dataset C1 (100 vuelos):")
  compararSalida("C1-1", "ORD", "TPA", 18, 30, VuelosC.vuelosC1, aeropuertos)
  compararSalida("C1-2", "DFW", "MIA", 16, 0, VuelosC.vuelosC1, aeropuertos)
  compararSalida("C1-3", "ATL", "LAX", 20, 0, VuelosC.vuelosC1, aeropuertos)
  
  println("\n// Dataset C2 (100 vuelos):")
  compararSalida("C2-1", "ORD", "TPA", 17, 30, VuelosC.vuelosC2, aeropuertos)
  compararSalida("C2-2", "LAX", "JFK", 15, 30, VuelosC.vuelosC2, aeropuertos)
  compararSalida("C2-3", "SEA", "MIA", 19, 0, VuelosC.vuelosC2, aeropuertos)
  
  println("\n// Dataset C3 (100 vuelos):")
  compararSalida("C3-1", "ORD", "TPA", 18, 0, VuelosC.vuelosC3, aeropuertos)
  compararSalida("C3-2", "DFW", "SEA", 16, 30, VuelosC.vuelosC3, aeropuertos)
  compararSalida("C3-3", "ATL", "SFO", 21, 30, VuelosC.vuelosC3, aeropuertos)
  }
}