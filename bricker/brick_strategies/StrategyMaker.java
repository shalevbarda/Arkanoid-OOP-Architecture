package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.util.Random;

/**
 * Factory class for creating collision strategies for bricks.
 * <p>
 * Creates random collision strategies based on probability distribution:
 * 50% basic behavior, 40% single special behavior, 10% double special behavior.
 * Special behaviors include extra life, extra balls, extra paddle, and
 * exploding bricks.
 *
 * @author Omri Markovich, Shalev Barda
 * @see CollisionStrategy
 */
public class StrategyMaker {

	private final UserInputListener inputListener;
	private final GameObjectCollection gameObjects;
	private final Counter bricksCounter;
	private final WindowController windowController;
	private final Renderable mockBallRenderable;
	private final Renderable heartRenderable;
	private final Vector2 paddleSize;
	private final BrickerGameManager brickerGameManager;
	private final Vector2 heartSize;
	private final Vector2 heartVelocity;
	private final Vector2 mockBallSize;
	private final float ballSpeed;
	private final Sound explosionSound;
	private final Sound collisionSound;
	private final ImageReader imageReader;

	private enum Behavior {
		EXTRA_LIFE,
		EXTRA_BALL,
		EXTRA_PADDLE,
		EXPLOSIVE_BRICK,
	}

	private static final int NUM_SPECIAL_BEHAVIORS = (Behavior.values().length);
	private static final int NUM_OF_DOUBLE_SPECIAL_BEHAVIORS = 2;
	private static final int DOUBLE_BEHAVIOR_INDEX = NUM_SPECIAL_BEHAVIORS + 1;
	private static final double DEFAULT_BEHAVIOR_PROBABILITY = 0.5;
	private static final double SINGLE_SPECIAL_BEHAVIOR_PROBABILITY = 0.9;

	/**
	 * Creates a strategy maker with all required dependencies.
	 *
	 * @param brickerGameManager  The game manager instance
	 * @param gameObjects          The collection of game objects
	 * @param inputListener        Receives keyboard input for paddle movement
	 * @param imageReader          Used to load images
	 * @param bricksCounter        Counter tracking remaining bricks
	 * @param windowController     Controls the game window
	 * @param explosionSound       Sound to play when bricks explode
	 * @param collisionSound       Sound to play when balls collide
	 * @param mockBallRenderable   Image for extra balls
	 * @param heartRenderable      Image for heart discs
	 * @param heartSize           Size of heart discs
	 * @param heartVelocity       Falling velocity of heart discs
	 * @param mockBallSize        Size of extra balls
	 * @param ballSpeed           Speed of extra balls
	 * @param paddleSize          Size of extra paddle
	 */
	public StrategyMaker(
			BrickerGameManager brickerGameManager,
			GameObjectCollection gameObjects,
			UserInputListener inputListener,
			ImageReader imageReader,
			Counter bricksCounter,
			WindowController windowController,
			Sound explosionSound,
			Sound collisionSound,
			Renderable mockBallRenderable,
			Renderable heartRenderable,
			Vector2 heartSize,
			Vector2 heartVelocity,
			Vector2 mockBallSize,
			float ballSpeed,
			Vector2 paddleSize
	) {
		this.brickerGameManager = brickerGameManager;
		this.heartSize = heartSize;
		this.heartVelocity = heartVelocity;
		this.mockBallSize = mockBallSize;
		this.ballSpeed = ballSpeed;
		this.explosionSound = explosionSound;
		this.collisionSound = collisionSound;
		this.inputListener = inputListener;
		this.imageReader = imageReader;
		this.bricksCounter = bricksCounter;
		this.gameObjects = gameObjects;
		this.windowController = windowController;
		this.mockBallRenderable = mockBallRenderable;
		this.heartRenderable = heartRenderable;
		this.paddleSize = paddleSize;
	}

	/**
	 * Creates a random collision strategy based on probability distribution.
	 * <p>
	 * Returns a basic strategy 50% of the time, a single special strategy 40%
	 * of the time, or a double special strategy 10% of the time.
	 *
	 * @return A collision strategy instance
	 */
	public CollisionStrategy createStrategy() {
		double roll = Math.random();
		CollisionStrategy basicStrategy = new BasicCollisionStrategy(gameObjects, bricksCounter);

		// Regular (50%)
		if (roll <= DEFAULT_BEHAVIOR_PROBABILITY) {
			return basicStrategy;
		}

		// Special (40%)
		if (roll <= SINGLE_SPECIAL_BEHAVIOR_PROBABILITY) {
			return getRandomSpecialStrategy(basicStrategy);
		}

		// Double Special (10%)
		return getDoubleSpecialStrategy(basicStrategy);
	}

	private CollisionStrategy getDoubleSpecialStrategy(CollisionStrategy behavior) {
		int maxBehaviorsOptions = NUM_SPECIAL_BEHAVIORS + 1;
		boolean canRollDouble = true;
		CollisionStrategy currentStrategy = behavior;
		Random random = new Random();
		for (int i = 0; i < StrategyMaker.NUM_OF_DOUBLE_SPECIAL_BEHAVIORS; i++) {
			if (canRollDouble) {
				int specialBehavior = random.nextInt(maxBehaviorsOptions);
				if (specialBehavior == DOUBLE_BEHAVIOR_INDEX) {
					currentStrategy = getRandomSpecialStrategy(currentStrategy);
					canRollDouble = false;
				}
			}
			currentStrategy = getRandomSpecialStrategy(currentStrategy);
		}
		return currentStrategy;
	}

	private CollisionStrategy getRandomSpecialStrategy(CollisionStrategy behavior) {
		Random random = new Random();
		int roll = random.nextInt(NUM_SPECIAL_BEHAVIORS);
		Behavior selected = Behavior.values()[roll];
		return switch (selected) {
			case EXTRA_LIFE -> new ExtraLifeStrategy(
					behavior,
					gameObjects,
					brickerGameManager,
					heartRenderable,
					heartSize,
					heartVelocity);

			case EXTRA_BALL -> new ExtraBallsStrategy(
					behavior,
					gameObjects,
					mockBallRenderable,
					collisionSound,
					mockBallSize,
					ballSpeed);

			case EXTRA_PADDLE -> new ExtraPaddleStrategy(
					behavior,
					gameObjects,
					imageReader,
					inputListener,
					windowController.getWindowDimensions(),
					paddleSize);

			case EXPLOSIVE_BRICK -> new ExplodingBrickStrategy(
					behavior,
					gameObjects,
					explosionSound);
		};
	}
}
