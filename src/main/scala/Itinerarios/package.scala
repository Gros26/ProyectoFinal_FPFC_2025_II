import Datos._

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
}
