package bricker.brick_strategies;

import bricker.gameobjects.Brick;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.Sound;

/**
 * Collision strategy that causes a brick to explode and trigger neighboring bricks.
 * <p>
 * When a brick with this strategy is hit, it explodes and causes its adjacent
 * bricks (up, down, left, right) to also explode, creating a chain reaction.
 * Each brick can only explode once to prevent infinite loops.
 *
 * @author Omri Markovich, Shalev Barda
 * @see CollisionStrategy
 */
public class ExplodingBrickStrategy implements CollisionStrategy {
	private final CollisionStrategy behavior;
	private final GameObjectCollection gameObjects;
	private final Sound explosionSound;

	/**
	 * Creates an exploding brick strategy.
	 *
	 * @param behavior       The base collision strategy to wrap
	 * @param gameObjects    The collection of game objects to search for neighbors
	 * @param explosionSound The sound to play when a brick explodes
	 */
	public ExplodingBrickStrategy(CollisionStrategy behavior,
			GameObjectCollection gameObjects,
			Sound explosionSound) {
		this.behavior = behavior;
		this.gameObjects = gameObjects;
		this.explosionSound = explosionSound;
	}

	/**
	 * Executes the base behavior and triggers an explosion chain reaction.
	 * <p>
	 * The brick is marked as exploded and its neighbors are triggered to explode.
	 * Each brick can only explode once to prevent infinite recursion.
	 *
	 * @param brickObj The brick that was hit
	 * @param collider The game object that collided with the brick
	 */
	@Override
	public void onCollision(GameObject brickObj, GameObject collider) {
		this.behavior.onCollision(brickObj, collider);
		// ensure the brick is a Brick (not a paddle or ball)
		if (!(brickObj instanceof Brick brick))
			return;

        // prevent infinite loops: if already exploded, do nothing
		if (brick.hasExploded())
			return;

		brick.markExploded();
		if (explosionSound != null)
			explosionSound.play();

		// explode neighbors
		explodeNeighbors(brick);
	}

	private void explodeNeighbors(Brick brick) {
		int row = brick.getRow();
		int col = brick.getCol();

		int[][] neighbors = {
				{row - 1, col},   // up
				{row + 1, col},   // down
				{row, col - 1},   // left
				{row, col + 1}    // right
		};

		for (int[] rc : neighbors) {
			Brick neighbor = findBrick(rc[0], rc[1]);
			if (neighbor != null && !neighbor.hasExploded()) {
				// trigger its collision behavior as if the ball hit it
				neighbor.onHit();
			}
		}
	}

	private Brick findBrick(int row, int col) {
		for (GameObject obj : gameObjects) {
			if (obj instanceof Brick brick) {
                if (brick.getRow() == row && brick.getCol() == col)
					return brick;
			}
		}
		return null;
	}
}
