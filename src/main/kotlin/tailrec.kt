import java.math.BigInteger

fun fibonacci(n: Int, a: BigInteger, b: BigInteger): BigInteger {
    return if (n == 0) b
    else fibonacci(n - 1, a + b, a)
}

tailrec fun fibonacciTailrec(n: Int, a: BigInteger, b: BigInteger): BigInteger {
    return if (n == 0) b
    else fibonacciTailrec(n - 1, a + b, a)
}

fun fibonacciWhileLoop(n: Int, a: BigInteger, b: BigInteger): BigInteger {
    var iteration = n
    var tempA = a
    var tempB = b
    var result = BigInteger.ZERO
    while (iteration > 1) {
        result = (tempA + tempB)
        tempB = tempA
        tempA = result
        iteration-=1
    }
    return result
}
