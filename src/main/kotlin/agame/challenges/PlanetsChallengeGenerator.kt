package agame.challenges

import java.util.*

class PlanetsChallengeGenerator : ChallengeGenerator {

    private val planets = listOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")

    override fun generate(difficulty: Difficulty): Challenge {
        val copy = planets.toMutableList()
        val missing = copy.removeAt(rand(copy.size))
        return when (difficulty) {
            Difficulty.EASY -> Challenge(copy, "Who+is+missing%3F", missing)
            Difficulty.NORMAL -> {
                val missing2 = copy.removeAt(rand(copy.size))
                Challenge(copy, "Who+is+missing+%28sorted%29%3F", listOf(missing, missing2).sorted().toString())
            }
            else -> {
                val missing2 = copy.removeAt(rand(copy.size))
                val missing3 = copy.removeAt(rand(copy.size))
                val missing4 = copy.removeAt(rand(copy.size))
                val missing5 = copy.removeAt(rand(copy.size))
                Challenge(copy, "Who+is+missing+%28sorted%29%3F", listOf(missing, missing2, missing3, missing4, missing5).sorted().toString())
            }
        }
    }

    private fun rand(i: Int): Int {
        return Random().nextInt(i)
    }
}