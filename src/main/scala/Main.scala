object Main extends App {

  // ============================================
  // MODO 1: Con argumentos de línea de comandos
  // ============================================
  
  if (args.length > 0) {
    ejecutarConArgumentos(args)
  } else {
    // Si no hay argumentos, mostrar menú interactivo
    ejecutarMenuInteractivo()
  }

  def ejecutarConArgumentos(args: Array[String]): Unit = {
    val funcion = args(0).toLowerCase
    val datasets = if (args.length > 1) args.tail.toSet else Set("curso")
    
    funcion match {
      case "itinerarios" | "i" =>
        Benchmarks.imprimirEncabezado("ITINERARIOS vs ITINERARIOS_PAR")
        ejecutarDatasets(datasets, ejecutarItinerarios)
        Benchmarks.imprimirPie()
        
      case "tiempo" | "t" =>
        Benchmarks.imprimirEncabezado("ITINERARIOSTIEMPO vs ITINERARIOSTIEMPO_PAR")
        ejecutarDatasets(datasets, ejecutarItinerariosTiempo)
        Benchmarks.imprimirPie()
        
      case "escalas" | "e" =>
        Benchmarks.imprimirEncabezado("ITINERARIOS_ESCALAS vs ITINERARIOS_ESCALAS_PAR")
        ejecutarDatasets(datasets, ejecutarEscalas)
        Benchmarks.imprimirPie()
        

      case "aire" | "a" =>
        Benchmarks.imprimirEncabezado("ITINERARIOS_AIRE vs ITINERARIOS_AIRE_PAR")
        ejecutarDatasets(datasets, ejecutarAire)
        Benchmarks.imprimirPie()


      case "salida" | "s" =>
        println("⚠️  Función no implementada aún")
        
      case "help" | "h" | "--help" =>
        mostrarAyuda()
        
      case _ =>
        println(s"❌ Función desconocida: $funcion")
        mostrarAyuda()
    }
  }

  def ejecutarDatasets(datasets: Set[String], ejecutor: String => Unit): Unit = {
    datasets.foreach(ejecutor)
  }

  def ejecutarItinerarios(dataset: String): Unit = {
    dataset.toLowerCase match {
      case "curso" | "c" => Benchmarks.benchmarkItinerariosCurso()
      case "a" | "a1" | "a2" | "a3" => Benchmarks.benchmarkItinerariosA()
      case "b" | "b1" | "b2" | "b3" => Benchmarks.benchmarkItinerariosB()
      case "c" | "c1" | "c2" | "c3" => Benchmarks.benchmarkItinerariosC()
      case "all" | "todos" =>
        Benchmarks.benchmarkItinerariosCurso()
        Benchmarks.benchmarkItinerariosA()
        Benchmarks.benchmarkItinerariosB()
        Benchmarks.benchmarkItinerariosC()
      case _ => println(s"⚠️  Dataset desconocido: $dataset")
    }
  }

  def ejecutarItinerariosTiempo(dataset: String): Unit = {
    dataset.toLowerCase match {
      case "curso" | "c" => Benchmarks.benchmarkTiempoCurso()
      case "a" | "a1" | "a2" | "a3" => Benchmarks.benchmarkTiempoA()
      case "b" | "b1" | "b2" | "b3" => Benchmarks.benchmarkTiempoB()
      case "c" | "c1" | "c2" | "c3" => Benchmarks.benchmarkTiempoC()
      case "d" | "d1" | "d2" | "d3" => Benchmarks.benchmarkTiempoD()
      case "all" | "todos" =>
        Benchmarks.benchmarkTiempoCurso()
        Benchmarks.benchmarkTiempoA()
        Benchmarks.benchmarkTiempoB()
        Benchmarks.benchmarkTiempoC()
      case _ => println(s"⚠️  Dataset desconocido: $dataset")
    }
  }

  def ejecutarEscalas(dataset: String): Unit = {
    dataset.toLowerCase match {
      case "curso" | "c" => Benchmarks.benchmarkEscalasCurso()
      case "a" | "a1" | "a2" | "a3" => Benchmarks.benchmarkEscalasA()
      case "b" | "b1" | "b2" | "b3" => Benchmarks.benchmarkEscalasB()
      case "c" | "c1" | "c2" | "c3" => Benchmarks.benchmarkEscalasC()
      case "d" | "d1" | "d2" | "d3" => Benchmarks.benchmarkEscalasD()
      case "all" | "todos" =>
        Benchmarks.benchmarkEscalasCurso()
        Benchmarks.benchmarkEscalasA()
        Benchmarks.benchmarkEscalasB()
        Benchmarks.benchmarkEscalasC()
      case _ => println(s"⚠️  Dataset desconocido: $dataset")
    }
  }

  def ejecutarAire(dataset: String): Unit = {
    dataset.toLowerCase match {
      case "curso" | "c" => Benchmarks.benchmarkAireCurso()
      case "a" | "a1" | "a2" | "a3" => Benchmarks.benchmarkAireA()
      case "b" | "b1" | "b2" | "b3" => Benchmarks.benchmarkAireB()
      case "c" | "c1" | "c2" | "c3" => Benchmarks.benchmarkAireC()
      case "d" | "d1" | "d2" | "d3" => Benchmarks.benchmarkAireD()
      case "all" | "todos" =>
        Benchmarks.benchmarkAireCurso()
        Benchmarks.benchmarkAireA()
        Benchmarks.benchmarkAireB()
        Benchmarks.benchmarkAireC()
        Benchmarks.benchmarkAireD()
      case _ =>
        println(s"⚠️  Dataset desconocido: $dataset")
    }
  }


  def mostrarAyuda(): Unit = {
    println("""
      |Uso: sbt "run [función] [datasets...]"
      |
      |Funciones disponibles:
      |  itinerarios, i    - Probar itinerarios vs itinerariosPar
      |  tiempo, t         - Probar itinerariosTiempo (TODO)
      |  escalas, e        - Probar itinerariosEscalas (TODO)
      |  aire, a           - Probar itinerariosAire (TODO)
      |  salida, s         - Probar itinerarioSalida (TODO)
      |  help, h           - Mostrar esta ayuda
      |
      |Datasets disponibles:
      |  curso, c          - Ejemplos del enunciado
      |  a                 - Dataset A (15 vuelos)
      |  b                 - Dataset B (40 vuelos)
      |  c                 - Dataset C (100 vuelos)
      |  all, todos        - Todos los datasets
      |
      |Ejemplos:
      |  sbt "run itinerarios curso"
      |  sbt "run i a b c"
      |  sbt "run itinerarios all"
      |  sbt run                    (sin argumentos = menú interactivo)
      |""".stripMargin)
  }

  // ============================================
  // MODO 2: Menú interactivo (igual que Opción 1)
  // ============================================
  
  def ejecutarMenuInteractivo(): Unit = {
    import scala.io.StdIn._
    
    var continuar = true
    
    while (continuar) {
      println("\n" + "═" * 70)
      println("  SISTEMA DE ANÁLISIS DE ITINERARIOS - Proyecto FPFC")
      println("═" * 70)
      println("\n¿Qué función desea probar?")
      println("  1. itinerarios vs itinerariosPar")
      println("  2. itinerariosTiempo vs itinerariosTiempoPar")
      println("  3. itinerariosEscalas vs itinerariosEscalasPar")
      println("  4. itinerariosAire vs itinerariosAirePar")
      println("  5. itinerarioSalida vs itinerarioSalidaPar")
      println("  0. Salir")
      print("\nSeleccione una opción: ")
      
      val opcion = readLine().trim
      
      opcion match {
        case "0" =>
          println("\n👋 ¡Hasta luego!")
          continuar = false
          
        case "1" =>
          val datasets = seleccionarDatasets()
          if (datasets.nonEmpty) {
            Benchmarks.imprimirEncabezado("ITINERARIOS vs ITINERARIOS_PAR")
            if (datasets.contains(1)) Benchmarks.benchmarkItinerariosCurso()
            if (datasets.contains(2)) Benchmarks.benchmarkItinerariosA()
            if (datasets.contains(3)) Benchmarks.benchmarkItinerariosB()
            if (datasets.contains(4)) Benchmarks.benchmarkItinerariosC()
            Benchmarks.imprimirPie()
          }

        case "2" =>
          val datasets = seleccionarDatasets()
          if (datasets.nonEmpty) {
            Benchmarks.imprimirEncabezado("ITINERARIOSTIEMPO vs ITINERARIOSTIEMPO_PAR")
            if (datasets.contains(1)) Benchmarks.benchmarkTiempoCurso()
            if (datasets.contains(2)) Benchmarks.benchmarkTiempoA()
            if (datasets.contains(3)) Benchmarks.benchmarkTiempoB()
            if (datasets.contains(4)) Benchmarks.benchmarkTiempoC()
            if (datasets.contains(5)) Benchmarks.benchmarkTiempoD()
            Benchmarks.imprimirPie()
          }
        
        case "5" =>
          val datasets = seleccionarDatasets()
          if (datasets.nonEmpty) {
            Benchmarks.imprimirEncabezadoSalida("ITINERARIO_SALIDA vs ITINERARIO_SALIDA_PAR")
            if (datasets.contains(1)) Benchmarks.benchmarkSalidaCurso()
            if (datasets.contains(2)) Benchmarks.benchmarkSalidaA()
            if (datasets.contains(3)) Benchmarks.benchmarkSalidaB()
            if (datasets.contains(4)) Benchmarks.benchmarkSalidaC()
            Benchmarks.imprimirPie()
          }
          
        case "3" =>
          val datasets = seleccionarDatasets()
          if (datasets.nonEmpty) {
            Benchmarks.imprimirEncabezado("ITINERARIOS_ESCALAS vs ITINERARIOS_ESCALAS_PAR")
            if (datasets.contains(1)) Benchmarks.benchmarkEscalasCurso()
            if (datasets.contains(2)) Benchmarks.benchmarkEscalasA()
            if (datasets.contains(3)) Benchmarks.benchmarkEscalasB()
            if (datasets.contains(4)) Benchmarks.benchmarkEscalasC()
            if (datasets.contains(5)) Benchmarks.benchmarkEscalasD()
            Benchmarks.imprimirPie()
          }
          
        case "4" =>
          val datasets = seleccionarDatasets()
          if (datasets.nonEmpty) {
            Benchmarks.imprimirEncabezado("ITINERARIOS_AIRE vs ITINERARIOS_AIRE_PAR")
            if (datasets.contains(1)) Benchmarks.benchmarkAireCurso()
            if (datasets.contains(2)) Benchmarks.benchmarkAireA()
            if (datasets.contains(3)) Benchmarks.benchmarkAireB()
            if (datasets.contains(4)) Benchmarks.benchmarkAireC()
            if (datasets.contains(5)) Benchmarks.benchmarkAireD()
            Benchmarks.imprimirPie()
          }


        case _ =>
          println("❌ Opción inválida. Intente de nuevo.")
      }
      
      if (continuar) {
        print("\nPresione ENTER para continuar...")
        readLine()
      }
    }
  }

  def seleccionarDatasets(): Set[Int] = {
    import scala.io.StdIn._
    
    println("\n¿Qué datasets desea probar?")
    println("  1. Curso (ejemplos del enunciado)")
    println("  2. Dataset A (15 vuelos)")
    println("  3. Dataset B (40 vuelos)")
    println("  4. Dataset C (100 vuelos)")
    println("  6. Todos")
    println("  0. Volver")
    print("\nSeleccione una opción: ")
    
    readLine().trim match {
      case "0" => Set.empty
      case "1" => Set(1)
      case "2" => Set(2)
      case "3" => Set(3)
      case "4" => Set(4)
      case "5" => Set(5)
      case "6" => Set(1, 2, 3, 4, 5)
      case _ =>
        println("❌ Opción inválida.")
        seleccionarDatasets()
    }
  }
}