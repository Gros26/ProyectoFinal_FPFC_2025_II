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

    def itinerariosEscalasPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
      val buscarItinerarios = itinerariosPar(vuelos, aeropuertos)

      def totalEscalas(itinerario: Itinerario): Int = {
        val escalasTecnicas = itinerario.map(_.Esc).sum
        val transbordos = if (itinerario.nonEmpty) itinerario.length - 1 else 0
        escalasTecnicas + transbordos
      }

      (c1: String, c2: String) => {
        val todosLosItinerarios = buscarItinerarios(c1, c2)
        
        if(todosLosItinerarios.isEmpty) {
          Nil
        } else {
          val todosLosItinerariosPar = todosLosItinerarios.par
          val minEscalas = todosLosItinerariosPar.map(totalEscalas).min
          todosLosItinerariosPar.filter(it => totalEscalas(it) == minEscalas).toList
        }
      }
    }

    (cod1: String, cod2: String) => buscar(cod1, cod2, Set(cod1), nivel = 0)
  }
}
