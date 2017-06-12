package tenniskata

enum class Player { A, B }

enum class PlayerScore { Zero, Fifteen, Thirty, Forty }

sealed class Score {
  data class Points private constructor(val playerA: PlayerScore, val playerB: PlayerScore) : Score() {
    companion object { 
      operator fun invoke(playerA: PlayerScore, playerB: PlayerScore) : Score {
        return if (playerA === PlayerScore.Forty && playerB === PlayerScore.Forty) 
          Score.Deuce else Score.Points(playerA, playerB)
      }
    }
  }
  data class Advantage(val player : Player) : Score()
  object Deuce : Score()
  data class Game(val player : Player) : Score()
}

fun nextPointScore(pointScore : PlayerScore) : PlayerScore = when (pointScore) {
  PlayerScore.Zero -> PlayerScore.Fifteen
  PlayerScore.Fifteen -> PlayerScore.Thirty
  PlayerScore.Thirty -> PlayerScore.Forty
  PlayerScore.Forty -> throw Exception("Unexpected")
}

fun scorePoint(currentScore : Score, point : Player) : Score {
  return when (currentScore) {
    is Score.Points -> 
      if (currentScore.playerA === PlayerScore.Forty && point === Player.A) { Score.Game(Player.A) }
      else if (currentScore.playerB === PlayerScore.Forty && point === Player.B) { Score.Game(Player.B) }
      else 
          if (point === Player.A) Score.Points(nextPointScore(currentScore.playerA), currentScore.playerB)
          else Score.Points(currentScore.playerA, nextPointScore(currentScore.playerB))
    is Score.Advantage -> when (currentScore.player) {
      Player.A -> if(point === Player.A) Score.Game(Player.A) else Score.Deuce
      Player.B -> if(point === Player.B) Score.Game(Player.B) else Score.Deuce
    }
    is Score.Deuce -> Score.Advantage(point)
    is Score.Game -> throw Exception("Game already over")
  }
}

fun runGame(points : Sequence<Player>) : Score {
  return points.fold(Score.Points(PlayerScore.Zero, PlayerScore.Zero), fun(acc, point) : Score {
    val newScore = scorePoint(acc, point) 
    println("Point for player $point ... New score: $newScore")
    return newScore
  })
}

fun main(args : Array<String>) { 

  println("Scenario: Player A get all the points")
  val playerAisGreat = generateSequence({Player.A }).take(4)
  println(runGame(playerAisGreat))

  println("Scenario: Player B get all the points")
  val playerBisGreat = generateSequence({Player.B }).take(4)
  println(runGame(playerBisGreat))

  println("Sceario: Tough game. A wins")
  val gamePoints = listOf(Player.A, Player.B, Player.A, Player.B, Player.A, Player.A).asSequence()
  println(runGame(gamePoints))

  println("Sceario: Tough game, back from advantage. B wins")
  val gamePoints2 = listOf(Player.A, Player.B, Player.A, Player.B, Player.A, Player.B, Player.A, Player.B, Player.B, Player.B).asSequence()
  println(runGame(gamePoints2))
}
