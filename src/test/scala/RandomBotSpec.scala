package vindinium

object RandomBotSpec extends org.specs2.mutable.Specification {
  "Random bot" title

  "Tile" should {
    Seq(Board.Tile.AIR, Board.Tile.FREE_MINE, Board.Tile.TAVERN) foreach { t ⇒
      s"'$t' be considered free" in {
        RandomBot.freeTiles.contains(t) must beTrue
      }
    }
  }
}
