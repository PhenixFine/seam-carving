import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util.Stack
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

var INPUT_IMAGE = ""
var OUTPUT_IMAGE = ""

fun main(args: Array<String>) {
    if (commandsOkay(args)) saveImage(seamHorizontal())
}

fun commandsOkay(args: Array<String>): Boolean {
    for (i in args.indices) {
        val last = i == args.lastIndex

        when (args[i]) {
            "-in" -> if (last || !File(args[i + 1]).isFile) {
                println("${args[i + 1]} is not a file.")
                return false
            } else INPUT_IMAGE = args[i + 1]
            "-out" -> if (last) {
                println("A filename was not given.")
                return false
            } else try {
                File(args[i + 1]).writeText("")
                OUTPUT_IMAGE = args[i + 1]
            } catch (e: Exception) {
                println(
                    "There was an error in writing to your file. Please ensure it is not set to read only or open" +
                            "in another program."
                )
            }
        }
    }
    if (INPUT_IMAGE == "") println("Missing -in image filename to invert color of.")
    if (OUTPUT_IMAGE == "") println("Missing -out filename to save the negative image.")
    return !(INPUT_IMAGE == "" || OUTPUT_IMAGE == "")
}

fun seamHorizontal() = rotateImage(seam(rotateImage(ImageIO.read(File(INPUT_IMAGE)))))

fun seam(bImage: BufferedImage = ImageIO.read(File(INPUT_IMAGE))): BufferedImage {
    val energy = energy(bImage)
    var lowestBottom = Double.MAX_VALUE
    var seamTotal: Double
    var oldSeamTotal = Double.MAX_VALUE
    val stack = Stack<Int>()
    val oldStack = Stack<Int>()
    val jLast = energy[0].lastIndex
    val iLast = energy.lastIndex
    val setRed = { i: Int, j: Int -> bImage.setRGB(i, j, Color(255, 0, 0).rgb) }

    for (j in 1..jLast) {
        for (i in 0..iLast) {
            var add = energy[i][j - 1]
            if (i != 0 && energy[i - 1][j - 1] < add) add = energy[i - 1][j - 1]
            if (i != iLast && energy[i + 1][j - 1] < add) add = energy[i + 1][j - 1]
            energy[i][j] += add
            if (j == jLast && energy[i][j] < lowestBottom) lowestBottom = energy[i][j]
        }
    }

    for (i in 0..energy.lastIndex) {
        if (energy[i][jLast] == lowestBottom) {
            stack.push(i)
            seamTotal = energy[i][jLast]
            var index = i
            for (j in jLast - 1 downTo 0) {
                var lowIndex = index
                if (index != 0 && energy[index - 1][j] < energy[index][j]) lowIndex -= 1
                if (index != iLast && energy[index + 1][j] < energy[lowIndex][j]) lowIndex = index + 1
                index = lowIndex
                stack.push(index)
                seamTotal += energy[index][j]
            }
            if (seamTotal < oldSeamTotal) {
                if (oldStack.isNotEmpty()) oldStack.clear()
                oldSeamTotal = seamTotal
                oldStack.addAll(stack)
            }
            stack.clear()
        }
    }

    for (j in 0..jLast) setRed(oldStack.pop(), j)

    return bImage
}

fun energy(bImage: BufferedImage): Array<Array<Double>> {
    val energy = Array(bImage.width) { Array(bImage.height) { 0.0 } }

    for (i in 0 until bImage.width) {
        for (j in 0 until bImage.height) {
            val x = if (i == 0) 1 else if (i == bImage.width - 1) bImage.width - 2 else i
            val y = if (j == 0) 1 else if (j == bImage.height - 1) bImage.height - 2 else j
            val left = Color(bImage.getRGB(x - 1, j))
            val right = Color(bImage.getRGB(x + 1, j))
            val up = Color(bImage.getRGB(i, y - 1))
            val down = Color(bImage.getRGB(i, y + 1))
            val xDif = (left.red.toDouble() - right.red).pow(2) +
                    (left.green.toDouble() - right.green).pow(2) + (left.blue.toDouble() - right.blue).pow(2)
            val yDif = (up.red.toDouble() - down.red).pow(2) +
                    (up.green.toDouble() - down.green).pow(2) + (up.blue.toDouble() - down.blue).pow(2)
            val energyNum = sqrt(xDif + yDif)
            energy[i][j] = energyNum
        }
    }

    return energy
}

fun rotateImage(bImage: BufferedImage): BufferedImage {
    val bImage2 = BufferedImage(bImage.height, bImage.width, BufferedImage.TYPE_INT_RGB)

    for (i in 0 until bImage.width) {
        for (j in 0 until bImage.height) bImage2.setRGB(j, i, Color(bImage.getRGB(i, j)).rgb)
    }

    return bImage2
}

fun saveImage(image: BufferedImage) = ImageIO.write(image, "png", File(OUTPUT_IMAGE))