import Datos._
import common._
import Itinerarios._
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.ParSeq
import scala.math.{sqrt, pow}
import scala.collection.immutable.HashMap 

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

    (cod1: String, cod2: String) => buscar(cod1, cod2, Set(cod1), nivel = 0)
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
        var llegada = aMinutos(ultimo.HL, ultimo.ML)

        if (llegada < salida) llegada += 24 * 60

        llegada - salida
      }
    }

    (cod1: String, cod2: String) => {
      val todos = generarItinerariosPar(cod1, cod2)

      val ordenados =
        todos
          .par
          .map(it => (tiempoTotal(it), it))
          .toList
          .sortBy(_._1)
          .map(_._2)

      ordenados
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

  def itinerarioSalidaPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String, Int, Int) => Itinerario = {

    def aMinutos(h: Int, m: Int): Int = h * 60 + m

    def horaSalida(itinerario: Itinerario): Int = itinerario match {
        case Nil => 0
        case vuelo :: _ => aMinutos(vuelo.HS, vuelo.MS)
    }

    def horaLlegada(itinerario: Itinerario): Int = itinerario match {
        case Nil => 0
        case vuelos => aMinutos(vuelos.last.HL, vuelos.last.ML)
    }

    // Usar la versión paralela de búsqueda de itinerarios
    val buscarItinerariosPar = itinerariosPar(vuelos, aeropuertos)

    def buscar(cod1: String, cod2: String, HL: Int, ML: Int): Itinerario = {
        val todosItinerarios = buscarItinerariosPar(cod1, cod2)
        val horaCita = aMinutos(HL, ML)

        // Si no hay itinerarios, retornar vacío
        if (todosItinerarios.isEmpty) return Nil

        // Clasificar itinerarios en paralelo
        val itinerariosClasificados = todosItinerarios.par.map { itinerario =>
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
        .seq  // Convertir a secuencial para sortBy
        .sortBy { case (_, salida, dias) => (dias, -salida) }
        .head

        mejorItinerario._1
    }

    (cod1: String, cod2: String, HL: Int, ML: Int) => buscar(cod1, cod2, HL, ML)
    }

    
    def itinerariosParBase(objective_function: Itinerario => Double, top_k: Int = 0): (List[Vuelo], List[Aeropuerto]) => (String, String) => List[Itinerario] = {
      def inner(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
      
        val buscarItinerarios = itinerariosPar(vuelos, aeropuertos)

        (c1: String, c2: String) => {
          val todosLosItinerarios = buscarItinerarios(c1, c2)

          if (todosLosItinerarios.isEmpty) {
            Nil
          } else if (top_k == 0) {
            val todosLosItinerariosPar = todosLosItinerarios.par
            val valor_optimo = todosLosItinerariosPar.map(objective_function).min
            todosLosItinerariosPar.filter(it => objective_function(it) == valor_optimo).toList
          } else {
            todosLosItinerarios.sortBy(objective_function).take(top_k)
          }

        }
      }
      inner
    }

  val codigoApHashMap: HashMap[String, Aeropuerto] = HashMap.from((aeropuertos ++ aeropuertosCurso).map(aeropuerto => aeropuerto.Cod -> aeropuerto))


  def objectivoAire(itinerario: Itinerario): Double = {
    

    def distAP(Org: Aeropuerto, Dst: Aeropuerto): Double = {

      sqrt(pow(Org.X - Dst.X, 2) + pow(Org.Y - Dst.Y, 2))
    }

    def vueloAire(vuelo: Vuelo): Double = {
      distAP(codigoApHashMap(vuelo.Org), codigoApHashMap(vuelo.Dst))
    }
    itinerario.map(vueloAire).sum
  }
    
    def itinerariosAirePar: (List[Vuelo], List[Aeropuerto]) => (String, String) => List[Itinerario] = itinerariosParBase(objectivoAire)
  
}
