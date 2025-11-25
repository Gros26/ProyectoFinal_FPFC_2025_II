import Datos._

import scala.collection.mutable.HashMap
import scala.math.{pow, sqrt}

package object helpers {



  val codigoApHashMap: HashMap[String, Aeropuerto] = HashMap.from(aeropuertos.map(aeropuerto => aeropuerto.Cod -> aeropuerto))

  


  def aMinutos(h: Int, m: Int): Int = h * 60 + m

  

  def objectivoTiempo(it: Itinerario): Int = {

    if (it.isEmpty) 0
    else {
      val primero = it.head
      val ultimo = it.last

      def aMinutos(h: Int, m: Int) = h * 60 + m

      val salida = aMinutos(primero.HS, primero.MS)
      val llegada = aMinutos(ultimo.HL, ultimo.ML)

      llegada - salida
    }
  }

  def objectivoEscalas(itinerario: Itinerario): Int = {
    val escalasTecnicas = itinerario.map(_.Esc).sum
    val transbordos = if (itinerario.nonEmpty) itinerario.length - 1 else 0
    escalasTecnicas + transbordos
  }

  def objectivoAire(itinerario: Itinerario): Double = {
    
    def distAP(Org: Aeropuerto, Dst: Aeropuerto): Double = {

      sqrt(pow(Org.X - Dst.X, 2) + pow(Org.Y - Dst.Y, 2))
    }

    def vueloAire(vuelo: Vuelo): Double = {
      distAP(codigoApHashMap(vuelo.Org), codigoApHashMap(vuelo.Dst))
    }
    itinerario.map(vueloAire).sum
  }

  def objectivoSalida(HL: Int, ML: Int): Itinerario => Double = {

    def horaSalida(itinerario: Itinerario): Int = itinerario match {
      case Nil => 0
      case vuelo :: _ => aMinutos(vuelo.HS, vuelo.MS)
    }

    def horaLlegada(itinerario: Itinerario): Int = itinerario match {
      case Nil => 0
      case vuelos => aMinutos(vuelos.last.HL, vuelos.last.ML)
    }
    val horaCita = aMinutos(HL, ML)

    def inner(itinerario: Itinerario): Double = {
      if (horaLlegada(itinerario) <= horaCita) {
        Double.MaxValue
      } else {
        -horaSalida(itinerario)
      }
    }

    inner
  }

}
