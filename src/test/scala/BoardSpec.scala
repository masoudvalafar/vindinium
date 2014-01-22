package vindinium

object BoardSpec extends org.specs2.mutable.Specification {
  "Board" title

  "Tile" should {
    "refuse negative hero ID" in {
      Board.Hero(-1) must throwA[IllegalArgumentException]
    }

    "refuse negative hero ID for mine" in {
      Board.Mine(-1) must throwA[IllegalArgumentException]
    }

    "support equality test" in {
      val mine = Board.Mine(1)

      (Board.Tile.AIR mustEqual Board.Tile.AIR).
        and(mine mustEqual mine).and(mine.hashCode mustEqual mine.hashCode)
    }
  }

  "Tiles list" should {
    "not be null" in {
      new Board(null, 0).aka("constructor") must throwA[IllegalArgumentException]
    }

    "not be empty" in {
      new Board(Array.ofDim[Board.Tile](0, 0), 0).
        aka("constructor") must throwA[IllegalArgumentException]
    }
  }
}
