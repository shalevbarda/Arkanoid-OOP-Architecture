package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Extra paddle that appears when a brick with ExtraPaddleStrategy is hit.
 * <p>
 * This paddle uses a singleton pattern to ensure only one instance exists at a time.
 * It disappears after being hit by a ball 4 times. The paddle responds to the same
 * keyboard input as the main paddle.
 *
 * @author Omri Markovich, Shalev Barda
 * @see Paddle
 */
public class ExtraPaddle extends Paddle {
    
	/**
	 * Maximum number of hits before the extra paddle is removed.
	 */
	public static final int MAX_HITS = 4;
	private static ExtraPaddle instance = null;
	private int numberOfHits;
	private final GameObjectCollection gameObjects;

	private ExtraPaddle(Vector2 topLeftCorner,
					   Vector2 dimensions,
					   Renderable renderable,
					   UserInputListener inputListener,
					   Vector2 windowDimensions,
					   GameObjectCollection gameObjects) {
		super(topLeftCorner, dimensions, renderable, inputListener, windowDimensions);
		this.numberOfHits = 0;
		this.gameObjects = gameObjects;
	}

	/**
	 * Gets the singleton instance of ExtraPaddle.
	 * <p>
	 * Creates a new instance if one doesn't exist, otherwise returns <code>null</code>
	 * to prevent multiple instances.
	 *
	 * @param topLeftCorner     Initial position of the paddle
	 * @param dimensions        Width and height of the paddle
	 * @param renderable        The visual image of the paddle
	 * @param inputListener     Receives keyboard input from the user
	 * @param windowDimensions  Used to keep the paddle within screen bounds
	 * @param gameObjects       Game object collection for removing the paddle
	 * @return The singleton instance of ExtraPaddle, or <code>null</code> if an instance
	 *         already exists
	 */
	public static ExtraPaddle getInstance(Vector2 topLeftCorner,
										 Vector2 dimensions,
										 Renderable renderable,
										 UserInputListener inputListener,
										 Vector2 windowDimensions,
										 GameObjectCollection gameObjects) {
		if (instance == null) {
			instance = new ExtraPaddle(topLeftCorner, dimensions, renderable, 
									 inputListener, windowDimensions, gameObjects);
			return instance;
		}
		return null; // Instance already exists
	}

	/**
	 * Resets the singleton instance.
	 * <p>
	 * Useful for testing or game reset scenarios.
	 */
	public static void resetInstance() {
		instance = null;
	}

	/**
	 * Checks if an extra paddle instance currently exists.
	 *
	 * @return <code>true</code> if an instance exists, <code>false</code> otherwise
	 */
	public static boolean instanceExists() {
		return instance != null;
	}

	/**
	 * Handles collision with game objects and tracks ball hits.
	 * <p>
	 * When hit by a ball, increments the hit counter. After <code>MAX_HITS</code>
	 * ball collisions, the paddle removes itself from the game.
	 *
	 * @param other     The other game object involved in the collision
	 * @param collision Information about the collision
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
        
		// Only count hits from balls (not walls or other objects)
		if (other instanceof Ball) {
			numberOfHits++;
            
			if (numberOfHits >= MAX_HITS) {
				// Remove the extra paddle from the game
				if (gameObjects != null) {
					gameObjects.removeGameObject(this);
				}
				// Reset the singleton instance
				instance = null;
			}
		}
	}
}

