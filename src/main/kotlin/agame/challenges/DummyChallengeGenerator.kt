package agame.challenges

import java.util.*

/*
 * Challenge for training before the game starts. No difficulty evolution.
 */
class DummyChallengeGenerator {

    data class Code(val letter: String, val word: String)

    private val info = listOf(
            Code("A", "alpha"),
            Code("B", "bravo"),
            Code("C", "charlie"))

    fun generate(): Challenge {
        val rand = Random().nextInt(info.size)
        val randItem = info[rand]
        val input = randItem.letter
        val output = randItem.word
        return Challenge(info, input, output)
    }
}