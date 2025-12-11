package bricker.gameobjects;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Falling heart disc that restores one life if caught by the main paddle.
 * <p>
 * The disc falls downward and only collides with the main paddle and the death zone.
 * If caught by the paddle, it restores one life. The disc is removed after any collision.
 *
 * @author Omri Markovich, Shalev Barda
 */
public class HeartLifeDisc extends GameObject {

	private final BrickerGameManager gameManager;
	private final GameObjectCollection gameObjects;

	/**
	 * Creates a heart life disc that falls downward.
	 *
	 * @param topLeftCorner Initial position of the disc
	 * @param dimensions    Width and height of the disc
	 * @param renderable    The image representing the disc
	 * @param gameManager   The game manager to restore lives
	 * @param gameObjects   The collection of game objects to remove the disc from
	 */
	public HeartLifeDisc(Vector2 topLeftCorner,
						 Vector2 dimensions,
						 Renderable renderable,
						 BrickerGameManager gameManager,
						 GameObjectCollection gameObjects) {
		super(topLeftCorner, dimensions, renderable);
		this.gameManager = gameManager;
		this.gameObjects = gameObjects;
	}

	/**
	 * Determines which objects this disc can collide with.
	 * <p>
	 * The disc only collides with the main paddle and the death zone,
	 * ignoring all other objects.
	 *
	 * @param other The other game object to check collision with
	 * @return <code>true</code> if collision should occur, <code>false</code> otherwise
	 */
	@Override
	public boolean shouldCollideWith(GameObject other) {
		String tag = other.getTag();

		// If collided with original paddle
		if (BrickerGameManager.MAIN_PADDLE_TAG.equals(tag)){
			return true;
		}

		// If collided with lower border → to remove
		// Block collisions with ALL other objects (ball, bricks, powerups...)
		return BrickerGameManager.TAG_LOWER_BORDER.equals(tag);
	}

	/**
	 * Handles collision with the main paddle or death zone.
	 * <p>
	 * If caught by the main paddle, restores one life. The disc is always
	 * removed after collision, regardless of what it hit.
	 *
	 * @param other     The other game object involved in the collision
	 * @param collision Information about the collision
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		String tag = other.getTag();

		if (BrickerGameManager.MAIN_PADDLE_TAG.equals(tag)) {
			gameManager.addLife();
		}

		gameObjects.removeGameObject(this);
	}
}
