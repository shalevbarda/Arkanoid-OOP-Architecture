package bricker.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.awt.event.KeyEvent;

/**
 * Paddle controlled by the player using keyboard arrow keys.
 * <p>
 * The paddle moves left and right based on keyboard input and is constrained
 * to stay within the window boundaries.
 *
 * @author Omri Markovich, Shalev Barda
 */
public class Paddle extends GameObject {

	private final UserInputListener inputListener;
	private final Vector2 windowDimensions;
	private final float movementSpeed = 300;

	/**
	 * Creates a paddle game object.
	 *
	 * @param topLeftCorner     Initial position of the paddle
	 * @param dimensions        Width and height of the paddle
	 * @param renderable        The visual image of the paddle
	 * @param inputListener     Receives keyboard input from the user
	 * @param windowDimensions  Used to keep the paddle within screen bounds
	 */
	public Paddle(Vector2 topLeftCorner,
			Vector2 dimensions,
			Renderable renderable,
			UserInputListener inputListener,
			Vector2 windowDimensions) {
		super(topLeftCorner, dimensions, renderable);
		this.inputListener = inputListener;
		this.windowDimensions = windowDimensions;
	}

	/**
	 * Updates the paddle position based on keyboard input.
	 * <p>
	 * Moves the paddle left or right based on arrow key input and ensures
	 * it stays within the window boundaries.
	 *
	 * @param deltaTime The time elapsed since the last frame update
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		float movementDir = 0f;

		// Check keyboard input
		if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
			movementDir -= movementSpeed;
		}
		if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
			movementDir += movementSpeed;
		}

		// Apply movement (scaled by deltaTime for smooth motion)
		setVelocity(Vector2.RIGHT.mult(movementDir));

		// --- Prevent exiting screen ---
		Vector2 currentTopLeft = getTopLeftCorner();
		float paddleX = currentTopLeft.x();
		float paddleWidth = getDimensions().x();

		// If paddle goes too far left
		if (paddleX < 0) {
			setTopLeftCorner(new Vector2(0, currentTopLeft.y()));
		}

		// If paddle goes too far right
		else if (paddleX + paddleWidth > windowDimensions.x()) {
			float correctedX = windowDimensions.x() - paddleWidth;
			setTopLeftCorner(new Vector2(correctedX, currentTopLeft.y()));
		}
	}
}
