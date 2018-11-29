# Agame

REST API based game. Players must develop a server and solve challenges as fast
as possible.

This is the game server, it includes the core game engine as well as a live
leaderboard.

A player server template can be found
[here](https://github.com/ghelouis/agame-player-template) (Java/Spring). Note
that the game is techno agnostic, players may develop their server using any
programming language.

## Server
* Run: `./gradlew`
* Build jar: `./gradlew jar`

## Game
* Players can follow their progress with tail -f SERVER_LOGS | grep USERNAME

### Pre-Game
* Players can register by developing a /name endpoint
* Players can train by completing challenge 0

### Live Game
* Players must succeed a challenge to gain access to the next one
* Challenges may evolve over time... :)
