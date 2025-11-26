import Datos._
import common._

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

  def aMinutos(h: Int, m: Int): Int = h * 60 + m

  def horaSalida(itinerario: Itinerario): Int = itinerario match {
    case Nil => 0
    case vuelo :: _ => aMinutos(vuelo.HS, vuelo.MS)
  }

  def horaLlegada(itinerario: Itinerario): Int = itinerario match {
    case Nil => 0
    case vuelos => aMinutos(vuelos.last.HL, vuelos.last.ML)
  }

  val buscarItinerarios = itinerarios(vuelos, aeropuertos)

  def buscar(cod1: String, cod2: String, HL: Int, ML: Int): Itinerario = {
    val todosItinerarios = buscarItinerarios(cod1, cod2)
    val horaCita = aMinutos(HL, ML)

    // Si no hay itinerarios, retornar vacío
    if (todosItinerarios.isEmpty) return Nil

    // Clasificar itinerarios según cuándo llegan
    val itinerariosClasificados = todosItinerarios.map { itinerario =>
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
      .sortBy { case (_, salida, dias) => (dias, -salida) }
      .head

    mejorItinerario._1
  }

  (cod1: String, cod2: String, HL: Int, ML: Int) => buscar(cod1, cod2, HL, ML)
}
}
