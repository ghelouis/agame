package agame.challenges

import java.util.*


class RotChallengeGenerator : ChallengeGenerator {

    private val loremEasy = "lorem ipsum"

    private val lorem = listOf(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            "Pellentesque suscipit, quam vitae sodales eleifend, ante magna blandit purus, ullamcorper aliquam lectus eros eget ligula.",
            "Suspendisse laoreet sodales ligula, in iaculis nulla condimentum ac.",
            "Quisque dolor libero, condimentum in lacus sed, euismod aliquet odio.",
            "Maecenas vehicula neque ut arcu rhoncus malesuada.",
            "Proin egestas tincidunt malesuada.",
            "Nullam scelerisque at neque ac aliquam.",
            "Donec eu porttitor velit, sed lobortis nibh.")


    override fun generate(difficulty: Difficulty): Challenge {
        return when (difficulty) {
            Difficulty.EASY -> {
                Challenge(loremEasy, "rot13", rot13(loremEasy))
            }
            Difficulty.NORMAL -> {
                val s = lorem.get(rand(lorem.size))
                Challenge(s, "rot13", rot13(s))
            }
            else -> {
                val n = rand(25) + 1
                val s = lorem.get(rand(lorem.size))
                Challenge(s, "rot$n", rot(n, s))
            }
        }
    }

    private fun rot13(s: String): String {
        return rot(13, s)
    }

    private fun rot(n: Int, s: String) : String {
        return s.map { char -> if (char.isLetter()) rotChar(char, n) else char }
                .joinToString("")
    }

    private fun rotChar(char: Char, n: Int): Char {
        val shift = if (char.isUpperCase()) 65 else 97
        return ((char.toInt() - shift + n).rem(26) + shift).toChar()
    }

    private fun rand(i: Int): Int {
        return Random().nextInt(i)
    }
}
