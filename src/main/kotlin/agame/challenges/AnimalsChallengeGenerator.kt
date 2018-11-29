package agame.challenges

import java.util.*

class AnimalsChallengeGenerator : ChallengeGenerator {

    data class Animal(val name: String, val sound: String)

    private val smallInfoData = listOf(
            Animal("dog", "bark"),
            Animal("cat", "meow"))

    private val fullInfoData = listOf(
            Animal("dog", "bark"),
            Animal("cat", "meow"),
            Animal("bird", "chirp"),
            Animal("wolf", "howl"),
            Animal("cow", "moo"),
            Animal("parrot", "squawk"),
            Animal("grasshopper", "chirp"),
            Animal("duck", "quack"),
            Animal("chimpanzee", "scream"),
            Animal("lion", "roar"),
            Animal("bear", "growl"),
            Animal("snake", "hiss"),
            Animal("bat", "screech"),
            Animal("bee", "buzz"))

    override fun generate(difficulty: Difficulty): Challenge {
        return when (difficulty) {
            Difficulty.EASY -> generateRandomChallenge(smallInfoData)
            Difficulty.NORMAL -> generateRandomChallenge(fullInfoData)
            else -> {
                val c1 = generateRandomChallenge(fullInfoData)
                val c2 = generateRandomChallenge(fullInfoData)
                Challenge(fullInfoData, "${c1.input}+AND+${c2.input}", listOf(c1.output, c2.output).toString())
            }
        }
    }

    private fun generateRandomChallenge(infoData: List<Animal>): Challenge {
        val rand = Random().nextInt(infoData.size)
        val randAnimal = infoData[rand]
        return Challenge(infoData, randAnimal.name, randAnimal.sound)
    }
}
