import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt
//用面向对象的思维设计相关类，从而实现直线与直线、直线与圆、直线与矩形的交点（二维坐标系），并判断给定点是否在矩形或者圆内部（含边界上的点）
//数据类
data class Point(val x : Double,val y : Double)
data class Line(val point1 : Point,val point2 : Point)
data class Circle(val radius : Double,val  originalPoint : Point)
data class Rectangle(val pointLeftTop : Point, val pointRightBottom : Point)
//判断
fun isLine(line : Line) : Boolean {
    return line.point1 != line.point2
}

fun isCircle(circle : Circle) : Boolean {
    return circle.radius != 0.0
}

fun isRectangle(rectangle : Rectangle) : Boolean {
    val point1 = rectangle.pointLeftTop
    val point2 = rectangle.pointRightBottom
    return point1.x != point2.x && point1.y != point2.y
}
//计算直线斜率 k
fun lineSlope(line: Line) : Double {
    //判断是否存在斜率
    val x1 = line.point1.x
    val y1 = line.point1.y
    val x2 = line.point2.x
    val y2 = line.point2.y
    return if(x1 == x2) {
        //正无穷
        Double.POSITIVE_INFINITY
    } else {
        (y1-y2)/(x1-x2)
    }
}
//计算直线截距 b
fun lineIntercept(line: Line): Double {
    val k = lineSlope(line)
    return if (k == Double.POSITIVE_INFINITY) {
        Double.NaN // 垂直线没有b
    } else {
        line.point1.y - k * line.point1.x
    }
}
//直线重合
fun isLineCoincident(line1: Line,line2: Line) : Boolean {
    return lineSlope(line1) == lineSlope(line2) && lineIntercept(line1) == lineIntercept(line2)
}
//直线平行但不重合
fun isLineParallel(line1: Line,line2: Line) : Boolean {
    return lineSlope(line1) == lineSlope(line2) && lineIntercept(line1) != lineIntercept(line2)
}
//直线与直线的交点
fun linesIntersect(line1 : Line,line2 : Line) : Point? {
    if(!isLine(line1) || !isLine(line2)) {
        return null
    }
    //情况1：重合线
    if (isLineCoincident(line1, line2)) {
        println("直线重合,有无数交点")
        return null
    }
    //情况2：平行线
    if (isLineParallel(line1, line2)) {
        return null
    }
    val x1 = line1.point1.x
    val x2 = line1.point2.x

    val k1 = lineSlope(line1)
    val b1 = lineIntercept(line1)
    val k2 = lineSlope(line2)
    val b2 = lineIntercept(line2)

    //情况3：只有一条斜率不存在，即垂线，那么必定有一个交点且交点x值已知
    if(k1 == Double.POSITIVE_INFINITY) {
        return Point(x1,x1*k2+b2)
    } else if(k2 == Double.POSITIVE_INFINITY) {
        return Point(x2,x2*k1+b1)
    }
    //两条斜率都不存在就是平行或者重合前面作了判断
    //情况4：斜率都存在 //y = k1*x+b1 = k2*x+b2
    val resultX = (b2-b1)/(k1-k2)
    val resultY = resultX * k1 + b1
    return Point(resultX,resultY)
}
//直线与圆的交点
fun lineWithCircleIntersect(line: Line,circle : Circle) : List<Point> {
    if(!isLine(line) || !isCircle(circle)) {
        return emptyList()
    }
    val originalPointX = circle.originalPoint.x
    val originalPointY = circle.originalPoint.y
    val x1 = line.point1.x
    val y1 = line.point1.y
    val x2 = line.point2.x
    val y2 = line.point2.y
    val dx = x2-x1
    val dy = y2-y1
    val dr = sqrt(dx*dx+dy*dy)
    val D = x1*y2-x2*y1
    val discriminant = circle.radius*circle.radius*dr*dr - D*D
    //无交点
    if(discriminant < 0) {
        return emptyList()
    }
    val sqrtDiscriminant = sqrt(discriminant)
    val signDy = if (dy < 0) -1 else 1

    val intersection1 = Point(
        (D * dy + signDy * dx * sqrt(discriminant)) / (dr * dr) + originalPointX,
        (-D * dx + Math.abs(dy) * sqrtDiscriminant) / (dr * dr) + originalPointY
    )
    // 一个交点
    if (discriminant == 0.0) {
        return listOf(intersection1)
    }

    val intersection2 = Point(
        (D * dy - signDy * dx * sqrtDiscriminant) / (dr * dr) + originalPointX,
        (-D * dx - abs(dy) * sqrtDiscriminant) / (dr * dr) + originalPointY
    )
    // 两个交点
    return listOf(intersection1, intersection2)
}
//直线与矩形的交点
fun lineWithRectangleIntersect(line: Line,rectangle : Rectangle) : List<Point> {
    if(!isLine(line) || !isRectangle(rectangle)) {
        return emptyList()
    }
    val pointLeftTop = rectangle.pointLeftTop
    val pintRightBottom = rectangle.pointRightBottom
    val pointRightTop = Point(pintRightBottom.x, pointLeftTop.y)
    val pointLeftBottom = Point(pointLeftTop.x, pintRightBottom.y)
    //边集
    val edges = listOf(
        Line(pointLeftTop, pointRightTop),
        Line(pointRightTop, pintRightBottom),
        Line(pintRightBottom, pointLeftBottom),
        Line(pointLeftBottom, pointLeftTop)
    )
    //转化为判断直线相交问题
    val intersections = mutableListOf<Point>()
    for (edge in edges) {
        val intersection = linesIntersect(line, edge)
        if (intersection != null) {
            intersections.add(intersection)
        }
    }
    return intersections
}
//给定点是否在矩形内部
fun isInRectangle(point: Point,rectangle : Rectangle) : Boolean {
    if(!isRectangle(rectangle)) {
        return false
    }
    //坐标系的判定
    return point.y <= rectangle.pointLeftTop.y && point.x <= rectangle.pointRightBottom.x && point.y > rectangle.pointRightBottom.y && point.y > rectangle.pointLeftTop.x
}
//给定点是否在圆内部
fun isInCircle(point: Point,circle : Circle) : Boolean {
    if(!isCircle(circle)) {
        return false
    }
    val dx = point.x - circle.originalPoint.x
    val dy = point.y - circle.originalPoint.y
    return dx * dx + dy * dy <= circle.radius * circle.radius
}



fun main() {
    val point1 = Point(0.0,0.0) //点(0,0)
    val point2 = Point(1.0,1.0) //点(1,1)
    val point3 = Point(0.0,1.0) //点(0,1)
    val point4 = Point(1.0,0.0) //点(1,0)
    val point5 = Point(0.5,0.5) //点(0.5,0.5)
    val line1 = Line(point1,point2) //点(0,0)与(1,1)连接的直线
    val line2 = Line(point3,point4) //点(0,1)与(1,0)连接的直线
    //测试1
    val f = linesIntersect(line1, line2) //直线y=x与y=-x+1交点
    if (f != null) {
        println("(${f.x},${f.y})")
    }
    //测试2
    val c = Circle(0.5,point5)
    val f2 = lineWithCircleIntersect(line1,c)
    f2.forEach { item ->
        if(!item.x.isNaN() || !item.y.isNaN())
            println("(${item.x},${item.y})")
    }
    //测试3
    val r = Rectangle(point3,point4)
    val f3 = lineWithRectangleIntersect(line1,r)
    f3.forEach { item ->
        if(!item.x.isNaN() || !item.y.isNaN())
            println("(${item.x},${item.y})")
    }
    //测试4
    val f4 = isInRectangle(point5,r) //点(0.5,0.5)是否在由(0,1)(1,0)围成的矩形中
    println(if(f4) "YES" else "NO")
    //测试5
    val f5 = isInCircle(point1,c) //点(0,0)是否在半径0.5,圆心(0.5,0.5)的圆中
    println(if(f5) "YES" else "NO")
}