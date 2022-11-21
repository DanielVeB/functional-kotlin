package utils

val sumIntegers = { a: Int, b: Int -> a + b }


fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
