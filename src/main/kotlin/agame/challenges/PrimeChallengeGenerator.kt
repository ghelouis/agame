package agame.challenges

import java.util.*
import kotlin.math.sqrt

class PrimeChallengeGenerator : ChallengeGenerator {

    override fun generate(difficulty: Difficulty): Challenge {
        return when (difficulty) {
            Difficulty.EASY -> {
                val n = Random().nextInt(100)
                val isPrime = isPrime(n)
                Challenge(n, "Am+I+prime%3F", isPrime.toString())
            }
            Difficulty.NORMAL -> {
                val n = Random().nextInt(100000)
                val isPrime = isPrime(n)
                Challenge(n, "Am+I+prime%3F", isPrime.toString())
            }
            else -> {
                val n = Random().nextInt(100000)
                val isPrime = isPrime(n)
                if (Random().nextInt(2) == 0) {
                    Challenge(n, "Am+I+prime%3F", isPrime.toString())
                } else {
                    Challenge(n, "Am+I+not+prime%3F", (!isPrime).toString())
                }
            }
        }
    }

    private fun isPrime(n: Int): Boolean {
        if (n < 2) {
            return false
        }
        if (n == 2) {
            return true
        }
        if (n % 2 == 0) {
            return false
        }
        for (i in 3..sqrt(n.toDouble()).toInt()) {
            if (n % i == 0) {
                return false
            }
        }
        return true
    }
}