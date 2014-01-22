package vindinium

object JsonSpec extends org.specs2.mutable.Specification
    with JsonTest with JsonFixtures {

  "JSON parsing" title

  "Board" should {
    "be parsed as expected" in withReader(boardJson) { r ⇒
      Json.next(r, Json.boardReader) mustEqual board
    }
  }

  "Hero" should {
    heroesJson zip heroes foreach { d ⇒
      val (json, hero) = d

      "be parsed as expected" in withReader(json) { r ⇒
        Json.next(r, Json.heroReader) mustEqual hero
      }
    }
  }

  "Game" should {
    "be parsed as expected" in withReader(gameJson) { r ⇒
      Json.next(r, Json.gameReader) mustEqual game
    }
  }

  "State" should {
    "be parsed as expected" in withReader(stateJson) { r ⇒
      Json.next(r, Json.stateReader) mustEqual state
    }
  }
}

sealed trait JsonTest {
  import java.io.Reader

  def withReader[A](json: String)(f: Reader ⇒ A): A = {
    val r = new java.io.StringReader(json)
    try f(r) finally r.close()
  }
}

sealed trait JsonFixtures {
  import java.net.URL
  import org.apache.commons.lang3.tuple.ImmutablePair.{ of ⇒ Pair }
  import scala.collection.JavaConverters.seqAsJavaListConverter
  import Board.Tile

  val boardJson =
    """{ "size": 3, "tiles": [ "  ", "##", "[]", "$-", "$1", "@2", "  ", "@1", "  " ] }"""

  val tiles = {
    val l = Array.ofDim[Board.Tile](3, 3);
    l.update(0, Array(Tile.AIR, Tile.WALL, Tile.TAVERN))
    l.update(1, Array(Tile.FREE_MINE, Board.Mine(1), Board.Hero(2)))
    l.update(2, Array(Tile.AIR, Board.Hero(1), Tile.AIR))
    l
  }

  val board = new Board(tiles, 3)

  val heroesJson = Seq(
    """{
      "name": "Hero #1",
      "id": 1,
      "pos": { "x": 1, "y": 2 },
      "gold": 0,
      "life": 1,
      "crashed": false
    }""",
    """{
      "id": 2,
      "name": "Hero #2",
      "pos": { "x": 2, "y": 3 },
      "life": 2,
      "gold": 1,
      "crashed": true
    }""")

  val heroes = Seq(
    new Hero(1, "Hero #1", Pair(1, 2), 1, 0, false),
    new Hero(2, "Hero #2", Pair(2, 3), 2, 1, true))

  val gameJson = """{
    "id": 1,
    "turn": 2,
    "maxTurns": 10,
    "finished": false,
    "heroes": [
        {
            "name": "Hero #1",
            "id": 1,
            "pos": { "x": 1, "y": 2 },
            "gold": 0,
            "life": 1,
            "crashed": false
        },
        {
            "id": 2,
            "name": "Hero #2",
            "pos": { "x": 2, "y": 3 },
            "life": 2,
            "gold": 1,
            "crashed": true
        }
    ],
    "board": {
        "size": 3,
        "tiles": [ "  ", "##", "[]", "$-", "$1", "@2", "  ", "@1", "  " ]
    }
  }"""

  lazy val game = new Game("abcd", 2, 10, false, heroes.asJava, board)

  lazy val stateJson = scala.io.Source.
    fromURL(getClass.getResource("/input1.json")).getLines.mkString

  lazy val state = new State(game, 1, "tok",
    new URL("http://view/url"), new URL("http://play/url"))

}
