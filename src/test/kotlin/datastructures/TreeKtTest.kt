package datastructures

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import utils.nameOfNumber

internal class TreeKtTest {

    @Test
    fun `Should return 1 when tree has only 1 leaf`() {
        assertEquals(1, Leaf(1).size())

        assertEquals(1, Leaf(1).sizeFold())
    }

    @Test
    fun `Should return correct size of tree`() {
        val tree = Branch(
            Branch(Leaf(1), Leaf(1)),
            Leaf(1)
        )
        assertEquals(5, tree.size())
        assertEquals(5, tree.sizeFold())

        val biggerTree = Branch(tree, Branch(Leaf(1), Leaf(1)))

        assertEquals(9, biggerTree.size())
        assertEquals(9, biggerTree.sizeFold())

    }

    @Test
    fun `Should return maximum number from tree`() {
        val tree = Branch(
            Branch(Leaf(3), Leaf(4)),
            Leaf(2)
        )
        assertEquals(4, tree.maximum())
        assertEquals(4, tree.maximumFold())

        assertEquals(1, Leaf(1).maximum())
        assertEquals(4, tree.maximumFold())

    }

    @Test
    fun `Should return depth of tree`() {

        assertEquals(0, Leaf(1).depth())

        val tree = Branch(
            Leaf(4),
            Branch(
                Leaf(3), Branch(
                    Leaf(2),
                    Branch(
                        Leaf(1),
                        Branch(
                            Leaf(0),
                            Leaf(0)
                        )
                    )
                )
            )

        )

        assertEquals(5, tree.depth())
        assertEquals(5, tree.depthFold())
    }

    @Test
    fun `Should convert tree of numbers to tree of names of numbers`() {
        val tree = Branch(
            Leaf(4),
            Branch(
                Leaf(3), Branch(
                    Leaf(2),
                    Branch(
                        Leaf(1),
                        Branch(
                            Leaf(0),
                            Leaf(8)
                        )
                    )
                )
            )
        )

        val expectedTree = Branch(
            Leaf("four"),
            Branch(
                Leaf("three"), Branch(
                    Leaf("two"),
                    Branch(
                        Leaf("one"),
                        Branch(
                            Leaf("zero"),
                            Leaf("eight")
                        )
                    )
                )
            )
        )

        assertEquals(expectedTree, tree.map(nameOfNumber))
        assertEquals(expectedTree, tree.mapFold(nameOfNumber))
    }

}