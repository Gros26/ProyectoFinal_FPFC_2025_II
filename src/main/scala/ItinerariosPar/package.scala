import Datos._
import common._
import scala.collection.parallel.CollectionConverters._

package object ItinerariosPar {

  def itinerariosPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    val vuelosPorOrigen = vuelos.groupBy(_.Org).withDefaultValue(Nil)

    def buscar(actual: String, destino: String, visitados: Set[String]): List[Itinerario] = {
      if (actual == destino) List(Nil)
      else {
        val vuelosDisponibles = vuelosPorOrigen(actual).filterNot(v => visitados(v.Dst))

        buscarParalelo(vuelosDisponibles, destino, visitados)
      }
    }

    //aqui procesamos en paralelo
    def buscarParalelo(vuelos: List[Vuelo], destino: String, visitados: Set[String]): List[Itinerario] = {
      vuelos match {
        case Nil => Nil
        case vuelo :: Nil =>
          buscar(vuelo.Dst, destino, visitados + vuelo.Dst).map(vuelo :: _)
        case _ =>
          val (mitad1, mitad2) = vuelos.splitAt(vuelos.length / 2)
          val (izq, der) = parallel(
            buscarParalelo(mitad1, destino, visitados),
            buscarParalelo(mitad2, destino, visitados)
          )
          izq ::: der
      }
    }

    (cod1: String, cod2: String) => buscar(cod1, cod2, Set(cod1))
  }

  def itinerariosTiempoPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {

    val generarItinerariosPar = itinerariosPar(vuelos, aeropuertos)

    def tiempoTotal(it: Itinerario): Int = {
      if (it.isEmpty) 0
      else {
        val primero = it.head
        val ultimo  = it.last
        def aMinutos(h: Int, m: Int) = h * 60 + m
        val salida  = aMinutos(primero.HS, primero.MS)
        val llegada = aMinutos(ultimo.HL, ultimo.ML)
        llegada - salida
      }
    }

    (cod1: String, cod2: String) => {
      val todos = generarItinerariosPar(cod1, cod2)

      val ordenados =
        todos
          .par
          .map(it => (tiempoTotal(it), it)) // paraleliza cálculo
          .toList
          .sortBy(_._1)   // ordena secuencial
          .map(_._2)

      ordenados.take(3)
    }
  }

}
