import kotlin.math.*

data class Point(val x: Double, val y: Double)
//点集
val position = mapOf(
    "C" to Point(0.0, 0.0),
    "P1" to Point(-52.5, -32.0),
    "P2" to Point(-52.5, 32.0),
    "P3" to Point(52.5, 32.0),
    "P4" to Point(52.5, -32.0),
    "P5" to Point(0.0, -32.0),
    "P6" to Point(0.0, 32.0),
    "P7" to Point(-30.0, -7.0),
    "P8" to Point(-30.0, 7.0),
    "P9" to Point(30.0, 7.0),
    "P10" to Point(30.0, -7.0),
    "G1" to Point(-52.5, 0.0),
    "G2" to Point(52.5, 0.0)
)

fun getPosition(
    p1 : String, r1 : Double,theta1 : Double,
    p2 : String,r2 : Double,theta2 : Double) : Point {

    val point1 = position[p1] ?: error("P1数据有误")
    val point2 = position[p2] ?: error("P2数据有误")

    val angle = theta2 - theta1
    //点1 2之间的距离
    val dis = (point1.x - point2.x).pow(2) + (point1.y - point2.y).pow(2)

    val a = (r1.pow(2) - r2.pow(2) + dis) / (2 * sqrt(dis))
    val cosa = (point2.x - point1.x) / sqrt(dis)
    val sina = (point2.y - point1.y) / sqrt(dis)

    val pxPie = point1.x + a * cosa
    val pyPie = point1.y + a * sina

    val h = sqrt(r1.pow(2) - a.pow(2))
    val sign = if (angle >= 0) 1 else -1

    val px = pxPie - h * sign * sina
    val py = pyPie + h * sign * cosa

    return Point(round(px * 100) / 100, round(py * 100) / 100)
}

fun main() {
    var result = getPosition("P8", 22.0, 0.0, "P7", 27.7, 30.0)
    println("px=${result.x}, py=${result.y};")
    //result = getPosition("P8", 22.0, 0.0, "P7", 10.4, 30.0)
    //println("px=${result.x}, py=${result.y};")
    //result = getPosition("P8", 14.0, -30.0, "P7", 14.0, 30.0)
    //println("px=${result.x}, py=${result.y};")
}
