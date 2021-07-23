package example.json

import org.json4s
import org.json4s.JsonAST.{JArray, JDouble, JObject, JValue}
import org.json4s.jackson.JsonMethods
import org.json4s.{DefaultFormats, Formats}

object MetricsParsingDemo {
  implicit val JsonFormats: Formats = DefaultFormats

  case class PRCurvePoint(recall: Double, precision: Double)

  def main(args: Array[String]): Unit = {
    val topPrecisionMetricStr = "[{\"top_precision\":0.40670028818443804}]"
    val topPrecisionJson = JsonMethods.parse(topPrecisionMetricStr)

    println("top precision json: " + topPrecisionJson)

    topPrecisionJson match {
      case JArray(List(JObject(List((_, JDouble(topPrecisionValue)))))) =>
        println("top precision: " + topPrecisionValue)
    }

    val prCurveMetricStr =
      """[
        |{"recall":0.0,"precision":0.46464646464646464},
        |{"recall":0.01184956208140134,"precision":0.46464646464646464},
        |{"recall":0.02215352910870685,"precision":0.43434343434343436},
        |{"recall":0.03374549201442555,"precision":0.4395973154362416},
        |{"recall":0.04379185986604843,"precision":0.4282115869017632},
        |{"recall":0.05486862442040186,"precision":0.42857142857142855},
        |{"recall":0.06646058732612056,"precision":0.43288590604026844},
        |{"recall":0.080370942812983,"precision":0.4489208633093525},
        |{"recall":0.09299330242143225,"precision":0.45465994962216627},
        |{"recall":0.10303967027305512,"precision":0.4479283314669653},
        |{"recall":0.11540443070582174,"precision":0.45161290322580644},
        |{"recall":0.226172076249356,"precision":0.4425403225806452},
        |{"recall":0.3258629572385368,"precision":0.42506720430107525},
        |{"recall":0.4283874291602267,"precision":0.4191028225806452},
        |{"recall":0.5252447192168985,"precision":0.411005845595646},
        |{"recall":0.6156620298815044,"precision":0.4014782462623887},
        |{"recall":0.7109737248840804,"precision":0.39740820734341253},
        |{"recall":0.8049974240082431,"precision":0.39382482671707625},
        |{"recall":0.9003091190108191,"precision":0.3914212117818345},
        |{"recall":0.9927872230808862,"precision":0.38846890434432013}]""".stripMargin

    val prCurveMetricJson = JsonMethods.parse(prCurveMetricStr)

    println("PR curve metric json: " + prCurveMetricJson)

    def mapToPoints(elements: List[JValue]): List[PRCurvePoint] = elements match {
      case JObject(List((_, JDouble(recall)), (_, JDouble(precision)))) :: tail => PRCurvePoint(recall, precision) :: mapToPoints(tail)
      case Nil => Nil
    }

    val prCurvePoints = prCurveMetricJson match {
      case JArray(elements) => mapToPoints(elements)
    }

    println("PR curve points: " + prCurvePoints)

    val prCurvePoints2 = prCurveMetricJson match {
      case JArray(elements) => elements.map {
        case JObject(List((_, JDouble(recall)), (_, JDouble(precision)))) => PRCurvePoint(recall, precision)
      }
    }

    println("PR curve points: " + prCurvePoints2)
  }
}
