import Datos._
import common._

package object Itinerarios {
  def itinerarios(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    //inicialmente cojo los vuelos que salen de mi origen
    val vuelosPorOrigen = vuelos.groupBy(_.Org).withDefaultValue(Nil)

    def buscar(actual: String, destino: String, visitados: Set[String]): List[Itinerario] = {

      // si el aeropuerto actual es el mismo que el destino llegue a mi caso base
      if (actual == destino) List(Nil)
      else {
        //los vuelos que me sriven son los que desde mi aeropuerto actual me llevan a un destino que no he visitado
        /*
        Ejem si estoy en un vuelo de bogota a mexico y despues de mexico sale otro para bogota no me sirve porque ya visite bogota
         */
        val vuelosDisponibles = vuelosPorOrigen(actual).filterNot(v => visitados(v.Dst))

        // aqui para cada vuelo disponible me empizo a mover a su destino haciendo el bucle hasta llegar al caso base o ser descartado
        for {
          vuelo <- vuelosDisponibles
          itinerario <- buscar(vuelo.Dst, destino, visitados + vuelo.Dst)
        } yield vuelo :: itinerario
      }
    }

    (cod1: String, cod2: String) => buscar(cod1, cod2, Set(cod1))
  }

}
