package calculator
import java.math.BigInteger
import java.util.Scanner
import kotlin.math.absoluteValue
import kotlin.math.pow

var op = "+"
val variables = mutableMapOf<String, BigInteger>()

fun main() {
    val input = Scanner(System.`in`)
    var cmd = ""

    while (true) {
        cmd = input.nextLine().replace(" ", "")

        if (cmd == "") continue

        if (cmd.startsWith("/")) {
            if (cmd == "/exit") break
            if (cmd == "/help") {
                println("The program calculates the sum of numbers")
                continue
            }
            println("Unknown command")
            continue
        }

        if (cmd.contains("=")) {
            assertVar(cmd)
            continue
        }

        cmd = clearRepeated(cmd)
        eval(infixToPostFix(cmd))
    }
    println("Bye!")
}

fun infixToPostFix(exp: String): MutableList<String>{
    //initializing empty String for result
    var result = mutableListOf<String>()
    var error = mutableListOf<String>("Invalid expression")

    //initializing empty stack
    val stack = mutableListOf<Char>()
    var n = 0

    for (i in exp.indices) {
        val c = exp[i]

        //if the scanned character is an operand, add it to output
        if (c.isLetterOrDigit()) {
            if (n == 0) {
                result.add(c.toString())
                n += 1
            } else {
                val id = result.last()
                result.removeAt(result.size - 1)
                result.add( id + c.toString())
            }
        }

        //if the scanned character is an '(', push it to the stack
        else if (c == '(') {
            stack.add(c)
            n = 0
        }

        //if the scanned character is an ')', pop and output from the stack
        //until an '(' is encountered
        else if (c == ')') {
            n = 0
            while (stack.isNotEmpty() && stack.last() != '(') {
                result.add(stack.last().toString())
                stack.removeAt(stack.size - 1)
            }

            if (stack.isNotEmpty() && stack.last() != '(')
                return error //invalid expression
            else if (stack.isEmpty()) return  error
            else stack.removeAt(stack.size - 1)
        }

        else if (c != ' '){
            n = 0
            while(stack.isNotEmpty() && prec(c) <= prec(stack.last())) {
                if (stack.last() == '(') return error
                result.add(stack.last().toString())
                stack.removeAt(stack.size - 1)
            }
            stack.add(c)
        }

        if (c == ' ') n = 0
    }

    //pop the operators from the stack
    while (stack.isNotEmpty()) {
        if (stack.last() == '(') return error
        result.add(stack.last().toString())
        stack.removeAt(stack.size - 1)
    }

    return result
}

fun prec(ch: Char): Int {
    when (ch) {
        '+'-> return 1
        '-'-> return 1
        '*'-> return 2
        '/'-> return 2
        '^'-> return 3
    }
    return -1
}

fun eval(exp: MutableList<String>) {
    var ans = ""
    val stack = mutableListOf<String>()
    while (exp.size >= 1) {
        stack.add(exp.first())
        exp.removeAt(0)

        if (stack.last() in variables) {
            ans = variables[stack.last()].toString()
            stack.removeAt(stack.size - 1)
            stack.add(ans)
        } else if (stack.last().contains("[-+*/^]".toRegex())) {
            if (stack.size == 2) {
                ans = makeOp(stack.last(), stack[0], "0")
                stack.removeAt(stack.size - 1)
                stack.removeAt(stack.size - 1)
            } else {
                val stackSize = stack.size
                ans = makeOp(stack.last(), stack[stackSize - 2], stack[stackSize - 3])
                stack.removeAt(stack.size - 1)
                stack.removeAt(stack.size - 1)
                stack.removeAt(stack.size - 1)
            }
            stack.add(ans)
        }
    }

    println(stack[0])
}

fun makeOp(op: String, b: String, a: String): String {
    var ans: BigInteger = BigInteger.ZERO

    when (op) {
        "+" -> ans = BigInteger(a) + BigInteger(b)
        "-" -> ans = BigInteger(a) - BigInteger(b)
        "*" -> ans = BigInteger(a) * BigInteger(b)
        "/" -> ans = BigInteger(a) / BigInteger(b)
        "^" -> ans = BigInteger(a).pow(b.toInt())
    }

    return  ans.toString()
}

fun clearRepeated(exp: String): String{
    val stack = mutableListOf<Char>()

    for (ch in exp) {
        if (stack.size >= 1) {
            if (ch == stack.last()) {
                if (stack.last() == '-') {
                    stack.removeAt(stack.size - 1)
                    stack.add('+')
                } else if (!stack.last().toString().contains("[+()-]".toRegex())) {
                    if (ch.toString().contains("[0-9]".toRegex())) stack.add(ch)
                    else return "Invalid expression"
                } else stack.add(ch)
            } else if (stack.last().toString().contains("[*/^]".toRegex()) && ch.toString()
                            .contains("[*/^]".toRegex())) {
                return "Invalid expression"
            }

            else stack.add(ch)
        } else stack.add(ch)
    }

    return stack.joinToString(separator = "")
}

fun assertVar(line: String){
    val defVar = line.split("=")

    if (defVar.size > 2) {
        println("Invalid assignment")
        return
    }

    if (checkVarName(defVar[0], "identifier")){
        if (defVar[1].contains("[a-zA-Z]".toRegex())) {
            if (checkVarName(defVar[1], "assignment")){
                if (variables.contains(defVar[1])) {
                    variables[defVar[0]] = variables.getValue(defVar[1])
                } else println("Unknown variable")
            }
        } else {
            variables[defVar[0]] = BigInteger(defVar[1])
        }
    }
}

fun checkVarName(varName: String, id: String): Boolean{
    if (varName.contains("[0-9]".toRegex())) {
        println("Invalid $id")
        return false
    }

    return true 
}