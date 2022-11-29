package challenges


//396. Rotate Function
//You are given an integer array nums of length n.
//
//Assume arrk to be an array obtained by rotating nums by k positions clock-wise.
// We define the rotation function F on nums as follow:
//
//F(k) = 0 * arrk[0] + 1 * arrk[1] + ... + (n - 1) * arrk[n - 1].
//Return the maximum value of F(0), F(1), ..., F(n-1).

fun maxRotateFunction(nums: List<Int>): Int =
    List(nums.size) { index ->
        listOf(
            nums.subList(index, nums.size),
            nums.subList(0, index)
        ).flatten()
    }.maxOf { it.foldIndexed(0) { index, acc, i -> acc + (index * i) } }
