package agame.challenges

import java.util.*


class OrderListChallengeGenerator : ChallengeGenerator {

    val list = listOf(0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 9)

    override fun generate(difficulty: Difficulty): Challenge {
        return when (difficulty) {
            Difficulty.EASY -> reverseSortChallenge()
            Difficulty.NORMAL -> if (rand(2) == 0) return reverseSortChallenge() else incChallenge()
            else -> {
                when (rand(3)) {
                    0 -> reverseSortChallenge()
                    1 -> incChallenge()
                    else -> Challenge(list, "To+set", list.toSet().toString())
                }
            }
        }
    }

    private fun reverseSortChallenge(): Challenge {
        return Challenge(list, "Reverse+sort", list.sorted().reversed().toString())
    }

    private fun incChallenge(): Challenge {
        val r = rand(100)
        return Challenge(list, "Inc($r)", list.map { n -> n + r }.toString())
    }

    private fun rand(i: Int): Int {
        return Random().nextInt(i)
    }
}
