import Datos._
import helpers._
import common._

import scala.collection.mutable.HashMap
import math._
import scala.annotation.tailrec

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
  
  
  def itinerariosBase(objective_function: Itinerario => Double, top_k: Int = 0): (List[Vuelo], List[Aeropuerto]) => (String, String) => List[Itinerario] = {
    def inner(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {

      val todosItsFunc = itinerarios(vuelos, aeropuertos)


      def buscar(actual: String, destino: String): List[Itinerario] = {

        def findMins(l: List[(Double, Itinerario)], min_candidate: Double, its_candidate: List[Itinerario]): List[Itinerario] = {
          if (l.isEmpty) {
            its_candidate
          } else if (l.head._1 < min_candidate) {
            findMins(l.tail, l.head._1, List(l.head._2))
          } else if (l.head._1 == min_candidate) {
            findMins(l.tail, min_candidate, its_candidate :+ l.head._2)
          } else {
            findMins(l.tail, min_candidate, its_candidate)
          }
        }

        val todosIts = todosItsFunc(actual, destino)

        val todosObjIts = todosIts.map(it => (objective_function(it), it))
        findMins(todosObjIts, Double.MaxValue, List())

      }


      buscar
    }
    inner
  }



  val itinerarioTiempo = itinerariosBase(objectivoTiempo, 3)
  val itinerariosEscalas = itinerariosBase(objectivoEscalas)
  val itinerariosAire = itinerariosBase(objectivoAire)
  val itinerariosSalida =
    (vuelos: List[Vuelo], aeropuertos: List[Aeropuerto], HS: Int, MS: Int)
    => itinerariosBase(objectivoSalida(HS, MS))(vuelos, aeropuertos)



}


