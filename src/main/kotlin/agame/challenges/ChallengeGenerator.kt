package agame.challenges

interface ChallengeGenerator {

    fun generate(difficulty: Difficulty): Challenge
}
