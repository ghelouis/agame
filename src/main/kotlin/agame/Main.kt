package agame

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post

fun main(args: Array<String>) {

    val gameEngine = GameEngine()
    gameEngine.startPreGame();

    val app = Javalin.create().apply {
        exception(Exception::class.java) { e, _ -> e.printStackTrace() }
    }.enableStaticFiles("leaderboard")
            .start(7000)

    app.routes {
        post("/start") { gameEngine.startGame() }
        post("/pause") { gameEngine.pauseGame() }
        get("/game") { ctx -> ctx.json(gameEngine.getGameState()) }
        get("/challenges/:n/info") { ctx ->
            ctx.queryParam("user")?.let { user ->
                val challengeNumber = ctx.pathParam("n").toInt()
                gameEngine.getChallenge(user, challengeNumber)?.let { challengeInfo ->
                    ctx.json(challengeInfo)
                } ?: ctx.status(404)
            } ?: ctx.status(404)
        }
    }
}
