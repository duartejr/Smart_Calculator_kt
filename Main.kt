package calculator
import java.util.Scanner

var op = "+"

fun main() {
    val input = Scanner(System.`in`)
    var ans = 0
    var line = ""

    while (true) {
        line = input.nextLine()
        ans = 0
        op = "+"

        if (line == "") continue
        if (line == "/exit") break
        if (line == "/help") {
            println("The program calculates the sum of numbers")
            continue
        }

        for (n in line.split(" ")){
            if (n.toIntOrNull() == null) {
                if (n.length > 1) {
                    for (m in n) {
                        op = checkOp(m.toString())
                    }
                } else {
                    op = checkOp(n)
                }
            } else {
                if (op == "-") {
                    ans -= n.toInt()
                    op = "+"
                }
                if (op == "+") {
                    ans += n.toInt()
                    op = "+"
                }
            }
        }
        println(ans)

    }

    println("Bye!")
}


fun sum(a: Int, b: Int): Int {
    return a + b
}

fun sub(a: Int, b: Int): Int {
    return a - b
}

fun checkOp(p: String): String {
    return if (op == p && p == "-") "+"
    else if (op == "+" && p == "-") "-"
    else "+"
}