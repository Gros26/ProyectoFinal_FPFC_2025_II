import Datos._
import helpers._
import common._
import scala.collection.mutable.HashMap
import math._

package object Itinerarios {
  def itinerarios(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    val vuelosPorOrigen = vuelos.groupBy(_.Org).withDefaultValue(Nil)

    def buscar(actual: String, destino: String, visitados: Set[String]): List[Itinerario] = {
      if (actual == destino) List(Nil)
      else {
        val vuelosDisponibles = vuelosPorOrigen(actual).filterNot(v => visitados(v.Dst))
        for {
          vuelo <- vuelosDisponibles
          itinerario <- buscar(vuelo.Dst, destino, visitados + vuelo.Dst)
        } yield vuelo :: itinerario
      }
    }

    (cod1: String, cod2: String) => buscar(cod1, cod2, Set(cod1))
  }

  def itinerarioSalida(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String, Int, Int) => Itinerario = {



    val buscarItinerarios = itinerarios(vuelos, aeropuertos)

    def buscar(cod1: String, cod2: String, HL: Int, ML: Int): Itinerario = {
      val todosItinerarios = buscarItinerarios(cod1, cod2)
      val horaCita = aMinutos(HL, ML)

      //filtro itinerarios que llegan a tiempo
      val itinerariosValidos = todosItinerarios.filter { itinerario =>
        horaLlegada(itinerario) <= horaCita
      }

      //si no hay retorno vacio
      if (itinerariosValidos.isEmpty) Nil
      else {
        //me quedo con el que sale mas tarde
        itinerariosValidos.maxBy(horaSalida)
      }
    }

    (cod1: String, cod2: String, HL: Int, ML: Int) => buscar(cod1, cod2, HL, ML)
  }

  def itinerariosEscalas(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    //Busco todos los itinerarios 
    val buscarItinerarios = itinerarios(vuelos, aeropuertos)


  (c1: String, c2: String) => {
     //Obtengo todos los itinerarios
     val todosLosItinerarios = buscarItinerarios(c1, c2)

     if (todosLosItinerarios.isEmpty) {
      Nil
     } else {
      //Me quedo con el itinerario con menos escalas
      val minEscalas = todosLosItinerarios.map(totalEscalas).min
      //Filtro los itinerarios con menos escalas
      todosLosItinerarios.filter(it => totalEscalas(it) == minEscalas)
     }
    }
  }



  def itinerariosAire(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {

    val todosItsFunc = itinerarios(vuelos, aeropuertos)


    def buscar(actual: String, destino: String): List[Itinerario] = {

      val todosIts = todosItsFunc(actual, destino)

      val todosItsAire = todosIts.map(totalAire)

      var its_candidatos : List[Itinerario] = List()
      var aire_candidato : Double = Double.MaxValue

      for ((itAire, idx) <- todosItsAire.zipWithIndex) {
        if (itAire < aire_candidato) {
          its_candidatos = List(todosIts(idx))
          aire_candidato = itAire
        } else if (itAire == aire_candidato) {
          its_candidatos = its_candidatos :+ todosIts(idx)
        }
      }
      its_candidatos
    }
    buscar
  }

}


