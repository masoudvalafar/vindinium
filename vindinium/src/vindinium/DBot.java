package vindinium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;

import vindinium.Board;
import vindinium.Board.Tile;
import vindinium.Bot;
import vindinium.Direction;
import vindinium.Hero;
import vindinium.State;


public class DBot implements Bot {

	private boolean hasMission = false;
	private ImmutablePair<Integer,Integer> goal;
	
	static final List<Tile> freeTiles;

    static {
        final ArrayList<Tile> ts = new ArrayList<Tile>(3);
        ts.add(Tile.AIR);
        ts.add(Tile.FREE_MINE);
        ts.add(Tile.TAVERN);
        freeTiles = Collections.unmodifiableList(ts);
    }

    
	@Override
	public Direction nextMove(State state) {
		int passedTurn = state.game.turn;
		Hero played = state.hero();
		ImmutablePair<Integer,Integer> myPosition = played.position;
		
        System.out.println("my position: " + myPosition);
        
        Board board = state.game.board;
        Direction direction = Direction.STAY;
        
        if (!hasMission) {
        	List<ImmutablePair<Integer,Integer>> targetMines = findAchievableGoldMines(state.game.board, played.id);
        	goal = findClosestMine(state.game.board, targetMines, myPosition);
        	System.out.println("new goal is the closest mine:" + goal);
			hasMission = true;
		}
        
        if (hasMission) {
        	int shortestPathLength = Integer.MAX_VALUE;
        	
        	if (myPosition.right > 0) {
				if (isGoal(goal, myPosition.left, myPosition.right - 1) || isEmpty(board.tiles[myPosition.left][myPosition.right - 1])) {
					int pathLength = findShortestPath(board, new ImmutablePair<Integer,Integer>(myPosition.left, myPosition.right - 1), goal);
					if (pathLength < shortestPathLength) {
						direction = Direction.WEST;
						shortestPathLength = pathLength;
						System.out.println("Going " + direction + " " + board.tiles[myPosition.left][myPosition.right - 1].toString() + " - path length: " + shortestPathLength);
					}
				}
	        }
        	
        	if (myPosition.right < board.size - 1) {
				if (isGoal(goal, myPosition.left, myPosition.right + 1) || isEmpty(board.tiles[myPosition.left][myPosition.right + 1])) {
					int pathLength = findShortestPath(board, new ImmutablePair<Integer,Integer>(myPosition.left, myPosition.right + 1), goal);
					if (pathLength < shortestPathLength) {
						direction = Direction.EAST;
						shortestPathLength = pathLength; 
						System.out.println("Going " + direction + " " + board.tiles[myPosition.left][myPosition.right + 1].toString() + " - path length: " + shortestPathLength);
					}
				}
	        }

        	if (myPosition.left > 0) {
				if (isGoal(goal, myPosition.left - 1, myPosition.right) || isEmpty(board.tiles[myPosition.left - 1][myPosition.right])) {
					int pathLength = findShortestPath(board, new ImmutablePair<Integer,Integer>(myPosition.left - 1, myPosition.right), goal);
					if (pathLength < shortestPathLength) {
						direction = Direction.NORTH;
						shortestPathLength = pathLength; 
						System.out.println("Going " + direction + " " + board.tiles[myPosition.left - 1][myPosition.right].toString() + " - path length: " + shortestPathLength);
					}
				}
	        }
        	
        	if (myPosition.left < board.size - 1) {
				if (isGoal(goal, myPosition.left + 1, myPosition.right) || isEmpty(board.tiles[myPosition.left + 1][myPosition.right])) {
					int pathLength = findShortestPath(board, new ImmutablePair<Integer,Integer>(myPosition.left + 1, myPosition.right), goal);
					if (pathLength < shortestPathLength) {
						direction = Direction.SOUTH;
						shortestPathLength = pathLength;
						System.out.println("Going " + direction + " " + board.tiles[myPosition.left + 1][myPosition.right].toString() + " - path length: " + shortestPathLength);
					}
				}
	        }
        	
        	if (shortestPathLength < 2) {
				hasMission = false;
			}
        }
        
        System.out.println("Going " + direction);
        return direction;
	}

	private ImmutablePair<Integer, Integer> findClosestMine(Board board,
			List<ImmutablePair<Integer, Integer>> targetMines,
			ImmutablePair<Integer, Integer> myPosition) {
		
		int shortestPathLength = Integer.MAX_VALUE;
		ImmutablePair<Integer,Integer>  targetMine = null;
		for (ImmutablePair<Integer, Integer> mine: targetMines) {
			//int pathLength = findShortestPath(board, myPosition, mine);
			int pathLength = findShortestCoordinate(myPosition, mine);
			if (pathLength < shortestPathLength) {
				targetMine = mine;
				shortestPathLength = pathLength;
			}
		}
		
		return targetMine;
	}

	private int findShortestCoordinate(
			ImmutablePair<Integer, Integer> source,
			ImmutablePair<Integer, Integer> destination) {

		return Math.abs(source.left - destination.left) + Math.abs(source.right - destination.right);
	}

	private int findShortestPath(Board board, ImmutablePair<Integer, Integer> source,
			ImmutablePair<Integer, Integer> destination) {

		Set<ImmutablePair<Integer, Integer>> visited = new HashSet<ImmutablePair<Integer, Integer>>();
		List<ImmutablePair<Integer, Integer>> exploreList = new ArrayList<ImmutablePair<Integer, Integer>>();
		visited.add(source);
		exploreList.add(source);
		
		int l = 0;
		
		while(!visited.contains(destination)) {
			
			List<ImmutablePair<Integer, Integer>> nextToExplore = new ArrayList<ImmutablePair<Integer, Integer>>();
			
			while(exploreList.size() > 0) {
				ImmutablePair<Integer, Integer> toExplore = exploreList.remove(0);
				visited.add(toExplore);
				
				if (toExplore.right > 0) {
					//if (isEmpty(board.tiles[toExplore.left][toExplore.right - 1])) {
					if (isGoal(destination, toExplore.left, toExplore.right - 1) || isEmpty(board.tiles[toExplore.left][toExplore.right - 1])) {
						ImmutablePair<Integer, Integer> neighborTile = new ImmutablePair<Integer, Integer>(toExplore.left, toExplore.right - 1);
						if (!visited.contains(neighborTile)) {
							nextToExplore.add(neighborTile);
						}
					}
		        }

				if (toExplore.right < board.size - 1) {
					if (isGoal(destination, toExplore.left, toExplore.right + 1) || isEmpty(board.tiles[toExplore.left][toExplore.right + 1])) {
						ImmutablePair<Integer, Integer> neighborTile = new ImmutablePair<Integer, Integer>(toExplore.left, toExplore.right + 1);
						if (!visited.contains(neighborTile)) {
							nextToExplore.add(neighborTile);
						}
					}
		        }

				if (toExplore.left > 0) {
					if (isGoal(destination, toExplore.left - 1, toExplore.right) || isEmpty(board.tiles[toExplore.left - 1][toExplore.right])) {
						ImmutablePair<Integer, Integer> neighborTile = new ImmutablePair<Integer, Integer>(toExplore.left - 1, toExplore.right);
						if (!visited.contains(neighborTile)) {
							nextToExplore.add(neighborTile);
						}
					}
		        }


				if (toExplore.left < board.size - 1) {
					if (isGoal(destination, toExplore.left + 1, toExplore.right) || isEmpty(board.tiles[toExplore.left + 1][toExplore.right])) {
						ImmutablePair<Integer, Integer> neighborTile = new ImmutablePair<Integer, Integer>(toExplore.left + 1, toExplore.right);
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
		
		//System.out.println("shortest path between " + source + " " + destination + " is " + l);
		return l;
	}

	private boolean isGoal(ImmutablePair<Integer, Integer> position,
			Integer l, Integer r) {
		
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

	private List<ImmutablePair<Integer,Integer>> findAchievableGoldMines(Board board, int myID) {
		
		// get all mines
		List<ImmutablePair<Integer,Integer>> mines = new ArrayList<>();
		for (int i = 0; i < board.size; i++) {
			for (int j = 0; j < board.size; j++) {
				if (isMine(board.tiles[i][j])) {
					mines.add(new ImmutablePair<Integer, Integer>(i, j));
				}
				
			}
		}
		
		// get enemy/unclaimed mines
		List<ImmutablePair<Integer,Integer>> targetMines = new ArrayList<>();
		for (ImmutablePair<Integer,Integer> mine: mines) {
			if (!isMineMine(board.tiles[mine.left][mine.right], myID)) {
				targetMines.add(mine);
			}
		}
		
		return targetMines;
	}

	private boolean isMineMine(Tile t, int myID) {
		if (t.toString().contains(String.valueOf(myID))) {
			return true;
		}
		
		return false;
	}

	private boolean isMine(Tile t) {
		if (t.toString().contains("$")) {
			return true;
		}

		return false;
	}

}
