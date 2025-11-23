import Datos._
import Itinerarios._

object PruebasItinerariosTiempo {

  def main(args: Array[String]): Unit = {

    // Crear función de itinerarios tiempo con los datos del curso
    val itsTiempoCurso = itinerariosTiempo(vuelosCurso, aeropuertosCurso)

    // prueba itinerariosTiempo
    val itst1 = itsTiempoCurso("MID", "SVCS")
    println("Prueba 1 MID → SVCS:")
    println(itst1)
    println("----------------------------------")

    val itst2 = itsTiempoCurso("CLO", "SVCS")
    println("Prueba 2 CLO → SVCS:")
    println(itst2)
    println("----------------------------------")

    // 4 itinerarios CLO–SVO
    val itst3 = itsTiempoCurso("CLO", "SVO")
    println("Prueba 3 CLO → SVO (debería generar 4 itinerarios):")
    println(itst3)
    println("----------------------------------")

    // 2 itinerarios CLO–MEX
    val itst4 = itsTiempoCurso("CLO", "MEX")
    println("Prueba 4 CLO → MEX (debería generar 2 itinerarios):")
    println(itst4)
    println("----------------------------------")

    // 2 itinerarios CTG–PTY
    val itst5 = itsTiempoCurso("CTG", "PTY")
    println("Prueba 5 CTG → PTY (debería generar 2 itinerarios):")
    println(itst5)
    println("----------------------------------")
  }
}
