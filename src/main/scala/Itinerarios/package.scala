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
}
