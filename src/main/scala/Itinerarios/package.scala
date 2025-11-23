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

  def itinerariosTiempo(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {

    // Reutilizar función itinerarios existente
    val generarItinerarios = itinerarios(vuelos, aeropuertos)

    // Función auxiliar: calcula el tiempo total de un itinerario
    def tiempoTotal(it: Itinerario): Int = {
      /*
        Un itinerario es List[Vuelo].
        El tiempo total se calcula:
        - horaLlegadaÚltimo - horaSalidaPrimero + sumatoria de tiempos de vuelo
      */

      if (it.isEmpty) 0
      else {
        val primero = it.head
        val ultimo  = it.last

        // Convertir horas a minutos
        def aMinutos(h: Int, m: Int) = h * 60 + m

        val salida = aMinutos(primero.HS, primero.MS)
        val llegada = aMinutos(ultimo.HL, ultimo.ML)

        // tiempo de viaje total en minutos
        llegada - salida
      }
    }

    // Retornamos la función final (cod1, cod2) => List[Itinerario]
    (cod1: String, cod2: String) => {

      // 1. Obtener todos los itinerarios posibles usando tu función ya implementada
      val todos = generarItinerarios(cod1, cod2)

      // 2. Ordenar por su tiempo total
      val ordenados = todos.sortBy(tiempoTotal)

      // 3. Retornar los tres mejores (o menos si no hay tantos)
      ordenados.take(3)
    }
  }

}
