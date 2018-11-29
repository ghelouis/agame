package agame

import agame.challenges.*
import khttp.get
import java.lang.Thread.sleep
import java.net.ConnectException
import kotlin.concurrent.thread

class GameEngine {

    data class User(var name: String, val port: String, var score: Int, var currentChallenges: MutableList<Challenge>, var maxChallengeAchieved: Int, var finishedAllChallenges: Boolean)
    data class UserDto(val name: String, var score: Int, var maxChallengeAchieved: Int, var finishedAllChallenges: Boolean)
    data class GameStateDto(val players: List<UserDto>, val isRunning: Boolean)

    private fun User.toUserDto() = UserDto(
            name = name,
            score = score,
            maxChallengeAchieved = maxChallengeAchieved,
            finishedAllChallenges = finishedAllChallenges
    )

    private val roundInterval = 3000L // 3 seconds
    private val normalDifficultyThreshold = 100 // 100 * 3 = 5 minutes
    private val hardDifficultyThreshold = 140 // 140 * 3 = 7 minutes

    private val challenge0Generator = DummyChallengeGenerator()
    private val startChallenge = challenge0Generator.generate()
    private val startMaxChallenge = 0

    private var isPreGameRunning = true
    private var isGameRunning = false

    private val ports = GameEngine::class.java.getResource("/users.txt").readText().lines().asSequence().filter { l -> l.isNotEmpty() }

    private var users: MutableMap<String, User> = HashMap()

    private val challenges: List<ChallengeGenerator> = listOf(
            AnimalsChallengeGenerator(),
            OperationChallengeGenerator(),
            PlanetsChallengeGenerator(),
            OrderListChallengeGenerator(),
            PrimeChallengeGenerator(),
            RotChallengeGenerator())

    private var userChallengeGoodAnswers: MutableMap<String, MutableMap<Int, Int>> = HashMap()

    fun startPreGame() {
        thread {
            while (isPreGameRunning) {
                sleep(roundInterval)
                ports.forEach { port -> runPreGame(port) }
            }
        }
    }

    fun startGame() {
        if (isGameRunning)
            return
        println("Starting game with players " + users.keys.joinToString())
        isPreGameRunning = false
        isGameRunning = true
        users.values.forEach { u -> u.currentChallenges.add(challenges[0].generate(Difficulty.EASY)) }
        userChallengeGoodAnswers = users.values.associateBy({ u -> u.name },
                { _ ->
                    challenges.asSequence().mapIndexed { index, _ -> index }.associate { it to 0 } as MutableMap

                }) as MutableMap
        thread {
            while (isGameRunning) {
                sleep(roundInterval)
                users.values.forEach { user -> submitChallenges(user) }
            }
        }
    }

    fun pauseGame() {
        println("Pausing game")
        isGameRunning = false
    }

    // pre-game: register user name and play challenge 0
    private fun runPreGame(port: String) {
        try {
            val res = get("http://localhost:$port/name")
            if (res.statusCode == 200) {
                val name = res.text
                if (users.values.none { u -> u.name == name }) {
                    println("Welcome $name!")
                    users.values.removeIf { u -> u.port == port }
                    users.put(name, User(name, port, 0, listOf(startChallenge).toMutableList(), startMaxChallenge, false))
                }
            }
            users.values.forEach { user -> submitDummyChallenges(user) }
        } catch (e: ConnectException) {
            // ignore unreachable url
        }
    }

    private fun submitDummyChallenges(user: User) {
        val success = userSucceededChallenge(user, 0)
        if (success) {
            // update challenge
            user.currentChallenges[0] = challenge0Generator.generate()
        }
    }

    // recursive function: play all challenges until a fail or finished all
    private fun submitChallenges(user: User, challengeNumber: Int = 0) {
        //println("submit: user: ${user.name}, challenge: $challengeNumber")
        //println("${user.score}")
        if (challengeNumber == challenges.size) {
            if (!user.finishedAllChallenges) {
                println("[${user.name}] finished all challenges!")
                user.finishedAllChallenges = true
            }
            return
        }
        val success = userSucceededChallenge(user, challengeNumber + 1)
        if (success) {
            userChallengeGoodAnswers[user.name]?.get(challengeNumber)?.let { goodAnswers ->
                userChallengeGoodAnswers[user.name]?.put(challengeNumber, goodAnswers + 1)
            }

            user.score++
            user.maxChallengeAchieved = challengeNumber + 1

            // update current challenge
            updateChallenge(user, challengeNumber)

            // unlock and create next challenge
            if (challengeNumber < challenges.size - 1 && user.currentChallenges.size == challengeNumber + 2) {
                user.currentChallenges.add(challenges[challengeNumber + 1].generate(Difficulty.EASY))
            }

            submitChallenges(user, challengeNumber + 1)
        }
    }

    private fun userSucceededChallenge(user: User, i: Int): Boolean {
        val challenge = user.currentChallenges[i]
        try {
            val res = get("http://localhost:" + user.port + "/challenges/" + i + "?input=" + challenge.input)
            val answer = if (res.statusCode == 200) res.text else "Error ${res.statusCode}"
            return if (challenge.output != answer) {
                println("[${user.name}][Challenge $i] $answer : wrong answer! (expecting ${challenge.output} given ${challenge.input} and ${challenge.info})")
                false
            } else {
                println("[${user.name}][Challenge $i] $answer : good answer!")
                true
            }
        } catch (e: ConnectException) {
            // ignore unreachable url
        }
        return false
    }

    private fun updateChallenge(user: User, challengeNumber: Int) {
        userChallengeGoodAnswers[user.name]?.get(challengeNumber)?.let { goodAnswers ->
            when {
                goodAnswers < normalDifficultyThreshold -> user.currentChallenges[challengeNumber + 1] = challenges[challengeNumber].generate(Difficulty.EASY)
                goodAnswers < hardDifficultyThreshold -> user.currentChallenges[challengeNumber + 1] = challenges[challengeNumber].generate(Difficulty.NORMAL)
                else -> user.currentChallenges[challengeNumber + 1] = challenges[challengeNumber].generate(Difficulty.HARD)
            }
        }
    }

    fun getGameState(): GameStateDto {
        val players = users.values.map { u -> u.toUserDto() }.sortedByDescending { u -> u.score }
        return GameStateDto(players, isGameRunning)
    }

    fun getChallenge(user: String, challengeNumber: Int): Any? {
        return users[user]?.currentChallenges?.let { currentChallenges ->
            if (challengeNumber < currentChallenges.size) {
                return currentChallenges[challengeNumber].info
            }
            return null
        }
    }
}
