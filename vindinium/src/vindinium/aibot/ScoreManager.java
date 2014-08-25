package vindinium.aibot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;

import vindinium.Board;
import vindinium.Direction;
import vindinium.Board.Tile;
import vindinium.Hero;

public class ScoreManager {

	static BoardManager bm = BoardManager.getInstance();

	List<ImmutablePair<Integer, Integer>> mines = null;

	/**
	 * calculating score for each direction
	 * 
	 * @param board
	 * @param hero
	 * @param position
	 * @return
	 */
	public Map<Direction, Integer> getScores(Board board, Hero hero, ImmutablePair<Integer, Integer> position) {

		int id = hero.id;
		int hp = hero.life;
		System.out.println("hp:" + hp);

		// updating list of target mines
		List<ImmutablePair<Integer, Integer>> targetMines = getTargetableMines(board, id);

		// finding the closest mine
		ImmutablePair<Integer, Integer> targetMine = findClosestMine(targetMines, position);

		// finding distance from each neighbor to target mine
		Map<Direction, Integer> distanceFromTargetMine = getDistance(board, position, targetMine);

		// find closest pub
		ImmutablePair<Integer, Integer> pub = findClosestPub(position);

		// finding distance from pub
		Map<Direction, Integer> distanceFromTargetPub = getDistance(board, position, pub);

		// calculating best direction score
		Map<Direction, Integer> directionScores = new HashMap<Direction, Integer>();
		for (Direction d : Direction.values()) {
			int mineFactor = bm.getMaxDistance() - distanceFromTargetMine.get(d);
			int pubFactor = bm.getMaxDistance() - distanceFromTargetPub.get(d);
			System.out.println(String.format("score: %d - mine: %d, pub: %d", mineFactor + pubFactor, mineFactor,
					pubFactor) + " - direction: " + d);
			directionScores.put(d, hp > 50 ? mineFactor : pubFactor);
		}

		return directionScores;
	}

	private ImmutablePair<Integer, Integer> findClosestPub(ImmutablePair<Integer, Integer> position) {

		int shortestPathLength = Integer.MAX_VALUE;
		ImmutablePair<Integer, Integer> targetPub = null;
		for (ImmutablePair<Integer, Integer> pub : bm.getTargetPubs()) {
			int pathLength = findShortestCoordinate(position, pub);
			if (pathLength < shortestPathLength) {
				targetPub = pub;
				shortestPathLength = pathLength;
			}
		}

		return targetPub;
	}

	private Map<Direction, Integer> getDistance(Board board, ImmutablePair<Integer, Integer> position,
			ImmutablePair<Integer, Integer> targetMine) {
		Map<Direction, Integer> distance = new HashMap<Direction, Integer>();
		ImmutablePair<Integer, Integer> neighbor = new ImmutablePair<Integer, Integer>(position.left,
				position.right - 1);
		if (position.right > 0 && (bm.isEmpty(position.left, position.right - 1) || bm.isTarget(neighbor, targetMine))) {
			distance.put(Direction.WEST, bm.findShortestPath(neighbor, targetMine));
		} else {
			distance.put(Direction.WEST, bm.getMaxDistance());
		}

		neighbor = new ImmutablePair<Integer, Integer>(position.left, position.right + 1);
		if (position.right < board.size - 1 && bm.isEmpty(position.left, position.right + 1)
				|| bm.isTarget(neighbor, targetMine)) {
			distance.put(Direction.EAST, bm.findShortestPath(neighbor, targetMine));
		} else {
			distance.put(Direction.EAST, bm.getMaxDistance());
		}

		neighbor = new ImmutablePair<Integer, Integer>(position.left - 1, position.right);
		if (position.left > 0 && bm.isEmpty(position.left - 1, position.right) || bm.isTarget(neighbor, targetMine)) {
			distance.put(Direction.NORTH, bm.findShortestPath(neighbor, targetMine));
		} else {
			distance.put(Direction.NORTH, bm.getMaxDistance());
		}

		neighbor = new ImmutablePair<Integer, Integer>(position.left + 1, position.right);
		if (position.left < board.size - 1 && bm.isEmpty(position.left + 1, position.right)
				|| bm.isTarget(neighbor, targetMine)) {
			distance.put(Direction.SOUTH, bm.findShortestPath(neighbor, targetMine));
		} else {
			distance.put(Direction.SOUTH, bm.getMaxDistance());
		}

		distance.put(Direction.STAY,
				bm.findShortestPath(new ImmutablePair<Integer, Integer>(position.left, position.right), targetMine));

		// System.out.println("target mine:" + targetMine);
		// System.out.println("position:" + position);
		// System.out.println("distance:" + distance);
		return distance;
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
