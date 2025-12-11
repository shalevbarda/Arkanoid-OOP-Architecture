package bricker.gameobjects;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Brick object in the game grid that delegates collision behavior to a strategy.
 * <p>
 * Each brick has a position in the grid (row and column) and uses the strategy
 * pattern to define what happens when it is hit.
 *
 * @author Omri Markovich, Shalev Barda
 * @see CollisionStrategy
 */
public class Brick extends GameObject {

	private final int rowIndex;
	private final int colIndex;
	private final CollisionStrategy collisionStrategy;

	/**
	 * Creates a brick at the specified grid position.
	 *
	 * @param topLeftCorner     The position of the brick in the game window
	 * @param dimensions        The dimensions of the brick
	 * @param renderable        The renderable representing the brick
	 * @param rowIndex          The row index of the brick in the grid
	 * @param colIndex          The column index of the brick in the grid
	 * @param collisionStrategy The strategy to execute upon collision
	 */
	public Brick(Vector2 topLeftCorner,
				 Vector2 dimensions,
				 Renderable renderable,
				 int rowIndex,
				 int colIndex,
				 CollisionStrategy collisionStrategy) {

		super(topLeftCorner, dimensions, renderable);
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;
		this.collisionStrategy = collisionStrategy;
	}

	/**
	 * Delegates collision handling to the brick's collision strategy.
	 *
	 * @param other     The other game object involved in the collision
	 * @param collision Information about the collision
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		collisionStrategy.onCollision(this, other);
	}

	private boolean exploded = false;

	/**
	 * Returns the row index of this brick in the grid.
	 *
	 * @return The row index
	 */
	public int getRow() {
		return rowIndex;
	}

	/**
	 * Returns the column index of this brick in the grid.
	 *
	 * @return The column index
	 */
	public int getCol() {
		return colIndex;
	}

	/**
	 * Checks if this brick has been marked as exploded.
	 *
	 * @return <code>true</code> if the brick has exploded, <code>false</code> otherwise
	 */
	public boolean hasExploded() {
		return exploded;
	}

	/**
	 * Marks this brick as exploded to prevent infinite explosion loops.
	 */
	public void markExploded() {
		exploded = true;
	}

	/**
	 * Simulates a hit on this brick, triggering its collision strategy programmatically.
	 * <p>
	 * Used by exploding bricks to trigger neighboring bricks without an actual collision.
	 */
	public void onHit() {
		collisionStrategy.onCollision(this, null);
	}

}
