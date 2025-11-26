import Datos._
import common._
import Itinerarios._
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.ParSeq

package object ItinerariosPar {

    def itinerariosPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    val vuelosPorOrigen = vuelos.groupBy(_.Org).withDefaultValue(Nil)

    // Umbral mínimo para paralelizar y máxima profundidad de paralelismo
    val UMBRAL_PAR     = 4       // solo paralelizar si hay al menos 4 vuelos alternativos
    val MAX_PROF_PAR   = 2       // solo paralelizar en los 2 primeros niveles

    def buscar(actual: String, destino: String, visitados: Set[String], nivel: Int): List[Itinerario] = {
      if (actual == destino) List(Nil)
      else {
        val vuelosDisponibles = vuelosPorOrigen(actual).filterNot(v => visitados(v.Dst))

        // Si hay pocos vuelos o ya estamos muy profundo, mejor hacerlo secuencial
        if (vuelosDisponibles.size <= 1 || nivel >= MAX_PROF_PAR) {
          // equivalente a la versión secuencial
          for {
            vuelo <- vuelosDisponibles
            itinerario <- buscar(vuelo.Dst, destino, visitados + vuelo.Dst, nivel + 1)
          } yield vuelo :: itinerario
        } else {
          // aquí sí vale la pena repartir el trabajo
          buscarParalelo(vuelosDisponibles, destino, visitados, nivel)
        }
      }
    }

    def buscarParalelo(vuelos: List[Vuelo], destino: String, visitados: Set[String], nivel: Int): List[Itinerario] = {
      if (vuelos.size <= UMBRAL_PAR) {
        // si ya está pequeño, volvemos a secuencial (misma lógica que arriba)
        vuelos.flatMap { vuelo =>
          buscar(vuelo.Dst, destino, visitados + vuelo.Dst, nivel + 1).map(vuelo :: _)
        }
      } else {
        val (mitad1, mitad2) = vuelos.splitAt(vuelos.length / 2)
        val (izq, der) = parallel(
          buscarParalelo(mitad1, destino, visitados, nivel),
          buscarParalelo(mitad2, destino, visitados, nivel)
        )
        izq ::: der
      }
    }

    (cod1: String, cod2: String) => buscar(cod1, cod2, Set(cod1), nivel = 0)
  }


  def itinerarioSalidaPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String, Int, Int) => Itinerario = {

    def aMinutos(h: Int, m: Int): Int = h * 60 + m

    def horaSalida(itinerario: Itinerario): Int = itinerario match {
        case Nil => 0
        case vuelo :: _ => aMinutos(vuelo.HS, vuelo.MS)
    }

    def horaLlegada(itinerario: Itinerario): Int = itinerario match {
        case Nil => 0
        case vuelos => aMinutos(vuelos.last.HL, vuelos.last.ML)
    }

    // Usar la versión paralela de búsqueda de itinerarios
    val buscarItinerariosPar = itinerariosPar(vuelos, aeropuertos)

    def buscar(cod1: String, cod2: String, HL: Int, ML: Int): Itinerario = {
        val todosItinerarios = buscarItinerariosPar(cod1, cod2)
        val horaCita = aMinutos(HL, ML)

        // Si no hay itinerarios, retornar vacío
        if (todosItinerarios.isEmpty) return Nil

        // Clasificar itinerarios en paralelo
        val itinerariosClasificados = todosItinerarios.par.map { itinerario =>
        val salida = horaSalida(itinerario)
        val llegada = horaLlegada(itinerario)
        
        // Calcular en qué "día relativo" llega respecto a la cita
        val diasAntes = if (llegada <= horaCita) {
            // Llega el mismo día a tiempo
            0
        } else {
            // Llega después de la hora de cita el mismo día
            // Pero si lo tomamos el día anterior, llegaríamos a tiempo
            1
        }
        
        (itinerario, salida, diasAntes)
        }

        // Ordenar por:
        // 1. Menor número de días antes (prioridad a llegar el mismo día)
        // 2. Mayor hora de salida (salir lo más tarde posible)
        val mejorItinerario = itinerariosClasificados
        .seq  // Convertir a secuencial para sortBy
        .sortBy { case (_, salida, dias) => (dias, -salida) }
        .head

        mejorItinerario._1
    }

    (cod1: String, cod2: String, HL: Int, ML: Int) => buscar(cod1, cod2, HL, ML)
    }
}
