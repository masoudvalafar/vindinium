package vindinium.aibot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;

import vindinium.Board;
import vindinium.Direction;
import vindinium.Board.Tile;

public class ScoreManager {

	List<ImmutablePair<Integer, Integer>> mines = null;

	/**
	 * calculating score for each direction
	 * 
	 * @param board
	 * @param id
	 * @param position
	 * @return
	 */
	public Map<Direction, Integer> getScores(Board board, int id, ImmutablePair<Integer, Integer> position) {

		int MAX_DISTANCE = board.size * board.size;

		// updating list of target mines
		List<ImmutablePair<Integer, Integer>> targetMines = getTargetableMines(board, id);

		// finding the closest mine
		ImmutablePair<Integer, Integer> targetMine = findClosestMine(targetMines, position);

		// finding the best direction to reach the mine
		BoardManager bm = BoardManager.getInstance();
		Map<Direction, Integer> distance = new HashMap<Direction, Integer>();
		if (position.right > 0
				&& (bm.isEmpty(board, position.left, position.right - 1) || bm.isTarget(
						new ImmutablePair<Integer, Integer>(position.left, position.right - 1), targetMine))) {
			distance.put(Direction.WEST, bm.findShortestPath(board, new ImmutablePair<Integer, Integer>(position.left,
					position.right - 1), targetMine));
		} else {
			distance.put(Direction.WEST, MAX_DISTANCE);
		}

		if (position.right < board.size - 1 && bm.isEmpty(board, position.left, position.right + 1) || bm.isTarget(
				new ImmutablePair<Integer, Integer>(position.left, position.right + 1), targetMine)) {
			distance.put(Direction.EAST, bm.findShortestPath(board, new ImmutablePair<Integer, Integer>(position.left,
					position.right + 1), targetMine));
		} else {
			distance.put(Direction.EAST, MAX_DISTANCE);
		}

		if (position.left > 0 && bm.isEmpty(board, position.left - 1, position.right) || bm.isTarget(
				new ImmutablePair<Integer, Integer>(position.left - 1, position.right), targetMine)) {
			distance.put(Direction.NORTH, bm.findShortestPath(board, new ImmutablePair<Integer, Integer>(
					position.left - 1, position.right), targetMine));
		} else {
			distance.put(Direction.NORTH, MAX_DISTANCE);
		}

		if (position.left < board.size - 1 && bm.isEmpty(board, position.left + 1, position.right) || bm.isTarget(
				new ImmutablePair<Integer, Integer>(position.left + 1, position.right), targetMine)) {
			distance.put(Direction.SOUTH, bm.findShortestPath(board, new ImmutablePair<Integer, Integer>(
					position.left + 1, position.right), targetMine));
		} else {
			distance.put(Direction.SOUTH, MAX_DISTANCE);
		}

		distance.put(Direction.STAY, bm.findShortestPath(board, new ImmutablePair<Integer, Integer>(position.left,
				position.right), targetMine));

		System.out.println("target mine:" + targetMine);
		System.out.println("position:" + position);
		System.out.println("distance:" + distance);

		Map<Direction, Integer> directionScores = new HashMap<Direction, Integer>();
		for (Direction d : Direction.values()) {
			directionScores.put(d, MAX_DISTANCE - distance.get(d));
		}

		return directionScores;
	}

	/**
	 * finding the closest mine based on Cartesian coordinates
	 * 
	 * @param targetMines
	 * @param myPosition
	 * @return
	 */
	private ImmutablePair<Integer, Integer> findClosestMine(List<ImmutablePair<Integer, Integer>> targetMines,
			ImmutablePair<Integer, Integer> myPosition) {
		int shortestPathLength = Integer.MAX_VALUE;
		ImmutablePair<Integer, Integer> targetMine = null;
		for (ImmutablePair<Integer, Integer> mine : targetMines) {
			// int pathLength = findShortestPath(board, myPosition, mine);
			int pathLength = findShortestCoordinate(myPosition, mine);
			if (pathLength < shortestPathLength) {
				targetMine = mine;
				shortestPathLength = pathLength;
			}
		}

		return targetMine;
	}

	private int findShortestCoordinate(ImmutablePair<Integer, Integer> source,
			ImmutablePair<Integer, Integer> destination) {

		return Math.abs(source.left - destination.left) + Math.abs(source.right - destination.right);
	}

	private List<ImmutablePair<Integer, Integer>> getTargetableMines(Board board, int myID) {

		// get enemy/unclaimed mines
		List<ImmutablePair<Integer, Integer>> targetMines = new ArrayList<>();
		for (ImmutablePair<Integer, Integer> mine : getMines(board)) {
			if (!isMineMine(board.tiles[mine.left][mine.right], myID)) {
				targetMines.add(mine);
			}
		}

		return targetMines;
	}

	private List<ImmutablePair<Integer, Integer>> getMines(Board board) {
		if (mines == null) {
			mines = new ArrayList<>();
			for (int i = 0; i < board.size; i++) {
				for (int j = 0; j < board.size; j++) {
					if (isMine(board.tiles[i][j])) {
						mines.add(new ImmutablePair<Integer, Integer>(i, j));
					}

				}
			}
		}

		return mines;
	}

	private boolean isMine(Tile t) {
		if (t.toString().contains("$")) {
			return true;
		}

		return false;
	}

	private boolean isMineMine(Tile t, int myID) {
		if (t.toString().contains(String.valueOf(myID))) {
			return true;
		}

		return false;
	}

}
