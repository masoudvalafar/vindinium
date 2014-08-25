package vindinium.aibot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;

import vindinium.Board;
import vindinium.Board.Tile;

public class BoardManager {

	static BoardManager instance = null;
	private Board board;
	private ArrayList<ImmutablePair<Integer, Integer>> pubs = null;

	private BoardManager() {
	}

	public static BoardManager getInstance() {
		if (instance == null) {
			instance = new BoardManager();
		}

		return instance;
	}

	public int findShortestPath(ImmutablePair<Integer, Integer> source, ImmutablePair<Integer, Integer> destination) {

		Set<ImmutablePair<Integer, Integer>> visited = new HashSet<ImmutablePair<Integer, Integer>>();
		List<ImmutablePair<Integer, Integer>> exploreList = new ArrayList<ImmutablePair<Integer, Integer>>();
		visited.add(source);
		exploreList.add(source);

		int l = 0;

		while (!visited.contains(destination)) {

			List<ImmutablePair<Integer, Integer>> nextToExplore = new ArrayList<ImmutablePair<Integer, Integer>>();

			while (exploreList.size() > 0) {
				ImmutablePair<Integer, Integer> toExplore = exploreList.remove(0);
				visited.add(toExplore);

				if (toExplore.right > 0) {
					if (isGoal(destination, toExplore.left, toExplore.right - 1)
							|| isEmpty(toExplore.left, toExplore.right - 1)) {
						ImmutablePair<Integer, Integer> neighborTile = new ImmutablePair<Integer, Integer>(
								toExplore.left, toExplore.right - 1);
						if (!visited.contains(neighborTile)) {
							nextToExplore.add(neighborTile);
						}
					}
				}

				if (toExplore.right < board.size - 1) {
					if (isGoal(destination, toExplore.left, toExplore.right + 1)
							|| isEmpty(toExplore.left, toExplore.right + 1)) {
						ImmutablePair<Integer, Integer> neighborTile = new ImmutablePair<Integer, Integer>(
								toExplore.left, toExplore.right + 1);
						if (!visited.contains(neighborTile)) {
							nextToExplore.add(neighborTile);
						}
					}
				}

				if (toExplore.left > 0) {
					if (isGoal(destination, toExplore.left - 1, toExplore.right)
							|| isEmpty(toExplore.left - 1, toExplore.right)) {
						ImmutablePair<Integer, Integer> neighborTile = new ImmutablePair<Integer, Integer>(
								toExplore.left - 1, toExplore.right);
						if (!visited.contains(neighborTile)) {
							nextToExplore.add(neighborTile);
						}
					}
				}

				if (toExplore.left < board.size - 1) {
					if (isGoal(destination, toExplore.left + 1, toExplore.right)
							|| isEmpty(toExplore.left + 1, toExplore.right)) {
						ImmutablePair<Integer, Integer> neighborTile = new ImmutablePair<Integer, Integer>(
								toExplore.left + 1, toExplore.right);
						if (!visited.contains(neighborTile)) {
							nextToExplore.add(neighborTile);
						}
					}
				}

			}

			exploreList.addAll(nextToExplore);
			nextToExplore.clear();
			l++;
		}

		return l;
	}

	private boolean isGoal(ImmutablePair<Integer, Integer> position, Integer l, Integer r) {

		if (position.left == l && position.right == r) {
			return true;
		}

		return false;
	}

	private boolean isEmpty(Tile tile) {
		if (tile.toString().equals("##") || tile.toString().contains("$") || tile.toString().contains("[")) {
			return false;
		}

		return true;
	}

	public boolean isEmpty(int i, int j) {
		return isEmpty(board.tiles[i][j]);
	}

	public boolean isTarget(ImmutablePair<Integer, Integer> p1, ImmutablePair<Integer, Integer> p2) {
		return p1.equals(p2);
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public ArrayList<ImmutablePair<Integer, Integer>> getTargetPubs() {

		if (pubs == null) {
			pubs = new ArrayList<>();
			for (int i = 0; i < board.size; i++) {
				for (int j = 0; j < board.size; j++) {
					if (isPub(board.tiles[i][j])) {
						pubs.add(new ImmutablePair<Integer, Integer>(i, j));
					}

				}
			}
		}

		return pubs;
	}

	private boolean isPub(Tile tile) {
		if (tile.toString().equals("[]")) {
			return true;
		}

		return false;
	}

	public int getMaxDistance() {
		return board.size * 2;
	}

}
