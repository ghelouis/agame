package agame.challenges

import java.util.*

class OperationChallengeGenerator : ChallengeGenerator {

    data class Data(val name: String, val value: Int)

    override fun generate(difficulty: Difficulty): Challenge {
        val infoData = listOf(Data("x", rand(1000)), Data("y", rand(1000)))
        return when (difficulty) {
            Difficulty.EASY -> generateChallenge(infoData, 2)
            Difficulty.NORMAL -> generateChallenge(infoData, 4)
            else -> {
                generateChallenge(infoData, 5)
            }
        }
    }

    private fun rand(i: Int): Int {
        return Random().nextInt(i)
    }

    private fun generateChallenge(infoData: List<Data>, i: Int): Challenge {
        val randData = infoData[rand(infoData.size)]
        val randData2 = infoData[rand(infoData.size)]
        if (rand(i) == 0) {
            val input = "${randData.name}+PLUS+${randData2.name}"
            val output = randData.value + randData2.value
            return Challenge(infoData, input, output.toString())
        }
        if (rand(i) == 1) {
            val input = "${randData.name}+MINUS+${randData2.name}"
            val output = randData.value - randData2.value
            return Challenge(infoData, input, output.toString())
        }
        if (rand(i) == 2 || randData2.value == 0) { // prevent division by 0
            val input = "${randData.name}+TIMES+${randData2.name}"
            val output = randData.value * randData2.value
            return Challenge(infoData, input, output.toString())
        }
        if (rand(i) == 3) {
            val input = "${randData.name}+DIVIDE+${randData2.name}"
            val output = randData.value / randData2.value
            return Challenge(infoData, input, output.toString())
        }
        val input = "${randData.name}+MODULO+${randData2.name}"
        val output = randData.value % randData2.value
        return Challenge(infoData, input, output.toString())
    }
}