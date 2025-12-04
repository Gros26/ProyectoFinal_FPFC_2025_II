import Datos._
import common._
import scala.math.{sqrt, pow}

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

  def itinerariosTiempo(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {

    // Reutilizar función itinerarios existente
    val generarItinerarios = itinerarios(vuelos, aeropuertos)

    // Función auxiliar: calcula el tiempo total de un itinerario
    def tiempoTotal(it: Itinerario): Int = {
      if (it.isEmpty) 0
      else {
        val primero = it.head
        val ultimo  = it.last

        def aMinutos(h: Int, m: Int) = h * 60 + m
        val salida  = aMinutos(primero.HS, primero.MS)
        var llegada = aMinutos(ultimo.HL, ultimo.ML)

        // Si llega al día siguiente
        if (llegada < salida)
          llegada += 24 * 60

        llegada - salida
      }
    }

    // Retornamos la función final (cod1, cod2) => List[Itinerario]
    (cod1: String, cod2: String) => {

      // 1. Obtener todos los itinerarios posibles usando tu función ya implementada
      val todos = generarItinerarios(cod1, cod2)

      // 2. Ordenar por su tiempo total
      val ordenados = todos.sortBy(tiempoTotal)

      // // 3. Retornar los tres mejores (o menos si no hay tantos)
      ordenados
    }
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

  def itinerariosEscalas(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    //Busco todos los itinerarios 
    val buscarItinerarios = itinerarios(vuelos, aeropuertos)

    //Defino la funcion que me da el total de escalas
    def totalEscalas(itinerario: Itinerario): Int = {
      val escalasTecnicas = itinerario.map(_.Esc).sum
      val transbordos = if (itinerario.nonEmpty) itinerario.length - 1 else 0
      escalasTecnicas + transbordos
    }

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

  
  def objectivoAire(itinerario: Itinerario): Double = {
    
    val codigoApHashMap: HashMap[String, Aeropuerto] = HashMap.from(aeropuertos.map(aeropuerto => aeropuerto.Cod -> aeropuerto))

    
    def distAP(Org: Aeropuerto, Dst: Aeropuerto): Double = {

      sqrt(pow(Org.X - Dst.X, 2) + pow(Org.Y - Dst.Y, 2))
    }

    def vueloAire(vuelo: Vuelo): Double = {
      distAP(codigoApHashMap(vuelo.Org), codigoApHashMap(vuelo.Dst))
    }
    itinerario.map(vueloAire).sum
  }


  val itinerariosAire = itinerariosBase(objectivoAire)
  

}
