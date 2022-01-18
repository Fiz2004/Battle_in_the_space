package com.fiz.android.battleinthespace

class State(
    var countPlayers: Int = 4,
    var namePlayers: List<String> = listOf("Player 1", "Player 2", "Player 3", "Player 4"),
) {
    lateinit var level: Level
    var round: Int = 1
    var status: String = "playing"
    var mainPlayer: Int = 0

    var scores: MutableList<Int> = mutableListOf()

    init {
        newGame()
    }

    private fun newGame() {
        round = 0
        newRound()

        scores.clear()
        for (n in 0 until countPlayers)
            scores.add(0)
    }

    private fun newRound() {
        round += 1

        level = Level(20.0, 20.0,countPlayers, round)
    }

    fun update(controller: Array<Controller>, deltaTime: Double): Boolean {
        if (!level.update(controller,deltaTime))
            newRound()

        val tempScores=level.getScoresForUpdate()
        for (n in scores.indices)
            scores[n]+=tempScores[n]

        if (round == 11)
            return false

        return true
    }

    fun clickPause() {
        status = if (status == "playing")
            "pause"
        else
            "playing"
    }
}
