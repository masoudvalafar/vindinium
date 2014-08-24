package vindinium.aibot;

import java.util.Map;

import vindinium.Bot;
import vindinium.Direction;
import vindinium.State;

public class AIBot implements Bot {

	private ScoreManager scoreManager = new ScoreManager();

	@Override
	public Direction nextMove(State state) {

		BoardManager bm = BoardManager.getInstance();
		bm.setBoard(state.game.board);
		
		Map<Direction, Integer> directionScores = scoreManager.getScores(state.game.board, state.hero(),
				state.hero().position);

		// returning the best direction
		Direction direction = null;
		int bestScore = Integer.MIN_VALUE;

		for (Direction d : Direction.values()) {
			if (bestScore < directionScores.get(d)) {
				direction = d;
				bestScore = directionScores.get(d);
			}
		}
		
		System.out.println("scores:" + directionScores);
		return direction;
	}

}
