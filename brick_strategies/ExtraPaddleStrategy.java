package bricker.brick_strategies;

import bricker.gameobjects.ExtraPaddle;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Collision strategy that creates an extra paddle when a brick is hit.
 * <p>
 * The extra paddle is positioned at the center of the screen horizontally and
 * at half the screen height vertically. Only one extra paddle can exist at a time.
 * If one already exists, only the basic behavior (brick removal) occurs.
 *
 * @author Omri Markovich, Shalev Barda
 * @see CollisionStrategy
 * @see ExtraPaddle
 */
public class ExtraPaddleStrategy implements CollisionStrategy {

	private final CollisionStrategy behavior;
	private final GameObjectCollection gameObjects;
	private final ImageReader imageReader;
	private final UserInputListener inputListener;
	private final Vector2 windowDimensions;
	private final Vector2 paddleSize;
	private static final String PADDLE_IMG_PATH = "assets/paddle.png";

	/**
	 * Creates an extra paddle strategy.
	 *
	 * @param behavior        The base collision strategy to wrap
	 * @param gameObjects     The collection of game objects to add the paddle to
	 * @param imageReader     Used to load the paddle image
	 * @param inputListener   Receives keyboard input for paddle movement
	 * @param windowDimensions The window dimensions for positioning and bounds
	 * @param paddleSize      The size of the extra paddle
	 */
	public ExtraPaddleStrategy(CollisionStrategy behavior,
							   GameObjectCollection gameObjects,
							   ImageReader imageReader,
							   UserInputListener inputListener,
							   Vector2 windowDimensions,
							   Vector2 paddleSize) {
		this.behavior = behavior;
		this.gameObjects = gameObjects;
		this.imageReader = imageReader;
		this.inputListener = inputListener;
		this.windowDimensions = windowDimensions;
		this.paddleSize = paddleSize;
	}

	/**
	 * Executes the base behavior and creates an extra paddle if one doesn't exist.
	 * <p>
	 * If an extra paddle already exists, only the basic behavior is executed.
	 * Otherwise, a new extra paddle is created at the center of the screen.
	 *
	 * @param brick    The brick that was hit
	 * @param collider The game object that collided with the brick
	 */
	@Override
	public void onCollision(GameObject brick, GameObject collider) {
		// If an extra paddle already exists, only do basic behavior
		if (ExtraPaddle.instanceExists()) {
			this.behavior.onCollision(brick, collider);
			return;
		}

		this.behavior.onCollision(brick, collider);

		Renderable paddleImage = imageReader.readImage(PADDLE_IMG_PATH, true);

		Vector2 paddleCenter = new Vector2(
				windowDimensions.x() / 2,
				windowDimensions.y() / 2
		);


		ExtraPaddle extraPaddle = ExtraPaddle.getInstance(
				Vector2.ZERO,
				paddleSize,
				paddleImage,
				inputListener,
				windowDimensions,
				gameObjects
		);

		if (extraPaddle != null) {
			extraPaddle.setCenter(paddleCenter);
			gameObjects.addGameObject(extraPaddle);
		}
	}
}
