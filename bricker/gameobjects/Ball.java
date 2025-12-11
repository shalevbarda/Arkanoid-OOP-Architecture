package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Ball game object that bounces off walls and triggers actions when hitting the death zone.
 * <p>
 * The ball reflects off surfaces and plays a sound on collision. When it hits
 * the lower border (death zone), it triggers the collision actions callback.
 *
 * @author Omri Markovich, Shalev Barda
 */
public class Ball extends GameObject {

	private static final String TAG_LOWER_BORDER = "lower_border";
	private final Sound collisionSound;
	private final Runnable collisionActions;

	/**
	 * Creates a ball game object.
	 *
	 * @param topLeftCorner     Initial position of the ball
	 * @param dimensions        Width and height of the ball
	 * @param renderable         The image representing the ball
	 * @param collisionSound    Sound to play on collision
	 * @param collisionActions  Callback to execute when ball hits the death zone
	 */
	public Ball(Vector2 topLeftCorner,
				Vector2 dimensions,
				Renderable renderable,
				Sound collisionSound,
				Runnable collisionActions) {
		super(topLeftCorner, dimensions, renderable);
		this.collisionSound = collisionSound;
		this.collisionActions = collisionActions;
	}

	/**
	 * Handles collision with other game objects.
	 * <p>
	 * If the ball hits the death zone, the collision actions callback is executed.
	 * Otherwise, the ball's velocity is reflected and a collision sound is played.
	 *
	 * @param other     The other game object involved in the collision
	 * @param collision Information about the collision
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);

		if (other.getTag().equals(TAG_LOWER_BORDER)) {
			collisionActions.run();
			return;
		}

		Vector2 newVel = getVelocity().flipped(collision.getNormal());
		setVelocity(newVel);
		collisionSound.play();
	}
}
