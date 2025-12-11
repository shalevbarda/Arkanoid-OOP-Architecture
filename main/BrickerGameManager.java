package bricker.main;

import bricker.brick_strategies.*;
import bricker.gameobjects.Ball;
import bricker.gameobjects.Brick;
import bricker.gameobjects.Paddle;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Main game manager for the Bricker game.
 * <p>
 * Manages game initialization, game loop, collision strategies, and game state.
 * Supports special brick behaviors including extra lives, extra balls, extra paddles,
 * and exploding bricks. The game can be configured via command-line arguments
 * for the number of bricks per row and number of rows.
 *
 * @author Omri Markovich, Shalev Barda
 * @see GameManager
 */
public class BrickerGameManager extends GameManager {

	/**
	 * Tag identifier for the lower border (death zone) game object.
	 */
	public static final String TAG_LOWER_BORDER = "lower_border";

	/**
	 * Tag identifier for the main paddle game object.
	 */
	public static final String MAIN_PADDLE_TAG = "main_paddle";

	// ==== Window configuration ====
	private static final String WINDOW_TITLE = "Bricker Game";
	private static final Vector2 WINDOW_SIZE = new Vector2(700, 500);

	// ==== Brick grid configuration ====
	private static final int DEFAULT_BRICKS_PER_ROW = 8;
	private static final int DEFAULT_NUM_ROWS = 7;
	private static final float BRICK_HEIGHT = 15f;
	private static final float BRICK_SPACING = 5f;
	private static final float BRICKS_START_Y = 50f;

	// ==== Ball & paddle ====
	private static final float BALL_SPEED = 200f; // change to 200
	private static final Vector2 BALL_SIZE = new Vector2(20, 20);
	private static final Vector2 MOCK_BALL_SIZE = new Vector2(BALL_SIZE.mult(0.75f));
	private static final Vector2 PADDLE_SIZE = new Vector2(100, 15);
	private static final float PADDLE_Y_OFFSET = 30f; // distance from bottom
	private static final Vector2 HEART_DISC_SIZE = new Vector2(20, 20);
	private static final Vector2 HEART_DISC_VELOCITY = new Vector2(0, 100);

	// ==== Walls & death zone ====
	private static final float WALL_THICKNESS = 20f;
	private static final float DEATH_ZONE_HEIGHT = 20f;    // Height of the death zone
	private static final float DEATH_ZONE_Y_OFFSET = 0f;   // Offset (if wanted later)
	// tag for death zone

	// ==== Lives & UI ====
	private static final int INIT_LIVES = 3;
	private static final int MAX_LIVES = 4;
	private static final Vector2 LIVES_TEXT_DIMENSIONS = new Vector2(50, 30);

	private static final Vector2 HEART_SIZE = new Vector2(20, 20);
	private static final float HEART_SPACING = 30f;
	private static final float LIVES_UI_PADDING_LEFT = 10f;
	private static final float LIVES_UI_PADDING_BOTTOM = 40f;
	private static final float HEART_UI_Y_OFFSET = 8f;
	private static final int GREEN_BORDER = 3;
	private static final int YELLOW_BORDER = 2;

	// ==== Instance variables ====
	private final String[] args;
	private GameObject deathZone;
	private GameObject[] hearts;         // graphical UI (heart icons)
	private GameObject livesText;        // numeric UI (text)
	private TextRenderable livesTextRenderable;
	private GameObject ball;
	private WindowController windowController;
	private int lives;                   // life counter (updated during game)
	private final Random random = new Random();
	private final Counter bricksCounter = new Counter();
	private UserInputListener inputListener;

	// ==== Asset Paths ====
	private static final String BALL_IMG_PATH = "assets/ball.png";
	private static final String MOCK_BALL_IMG_PATH = "assets/mockBall.png";
	private static final String PADDLE_IMG_PATH = "assets/paddle.png";
	private static final String HEART_IMG_PATH = "assets/heart.png";
	private static final String BRICK_IMG_PATH = "assets/brick.png";
	private static final String BACKGROUND_IMG_PATH = "assets/DARK_BG2_small.jpeg";
	private static final String BALL_COLLISION_SOUND_PATH = "assets/blop.wav";
	private static final String EXPLOSION_SOUND_PATH = "assets/explosion.wav";

	// ==== Dialog messages ====
	private static final String MSG_LOSE = "You lose! Play again?";
	private static final String MSG_WIN = "You win! Play again?";


	// ==== SPECIAL_BEHAVIORS ====
	private  Sound explosionSound;
	private Sound collisionSound;
    private StrategyMaker strategyMaker;

	/**
	 * Creates a new Bricker game manager.
	 *
	 * @param windowTitle      The title of the game window
	 * @param windowDimensions The dimensions of the game window
	 * @param args             Command-line arguments (optional: bricks per row, number of rows)
	 */
	public BrickerGameManager(String windowTitle, Vector2 windowDimensions, String[] args) {
		super(windowTitle, windowDimensions);
		this.args = args;
	}

	/**
	 * Initializes the game by creating all game objects and setting up the game state.
	 * <p>
	 * Sets up the background, ball, paddle, walls, bricks grid, and lives UI.
	 * Loads all required assets (images and sounds).
	 *
	 * @param imageReader      Used to load image assets
	 * @param soundReader      Used to load sound assets
	 * @param inputListener    Receives keyboard input from the user
	 * @param windowController Controls the game window
	 */
	@Override
	public void initializeGame(ImageReader imageReader,
							   SoundReader soundReader,
							   UserInputListener inputListener,
							   WindowController windowController) {
		super.initializeGame(imageReader, soundReader, inputListener, windowController);

		// keep reference to window controller for the life lost dialog
		this.windowController = windowController;

		// keep reference to input listener
		this.inputListener = inputListener;

		// Load sounds
		explosionSound = soundReader.readSound(EXPLOSION_SOUND_PATH);
		collisionSound = soundReader.readSound(BALL_COLLISION_SOUND_PATH);

		// keep reference to strategy maker
		strategyMaker = new StrategyMaker(
				this,
				gameObjects(),
				inputListener,
				imageReader,
				bricksCounter,
				windowController,
				explosionSound,
				collisionSound,
				imageReader.readImage(MOCK_BALL_IMG_PATH, true),
				imageReader.readImage(HEART_IMG_PATH, true),
				HEART_DISC_SIZE,
				HEART_DISC_VELOCITY,
				MOCK_BALL_SIZE,
				BALL_SPEED,
				PADDLE_SIZE
		);

		// get window dimensions
		Vector2 windowDimensions = windowController.getWindowDimensions();

		// Initialize game Background
		initBackground(imageReader, windowDimensions);

		// Initialize ball
		initBall(imageReader, soundReader, windowDimensions);

		// Initialize paddle
		initPaddle(imageReader, inputListener, windowDimensions);

		// Initialize walls
		createWalls(windowDimensions);

		// Initialize bricks grid
		createBricksGrid(imageReader, windowDimensions);

		// Initialize lives
		initLivesUI(imageReader);

	}

	// ==== Ball initialization ====

	private void setRandomBallVelocity(GameObject ball) {
		Vector2[] directions = {
				new Vector2(1, 1),
				new Vector2(-1, 1),
				new Vector2(1, -1),
				new Vector2(-1, -1)
		};
		int index = random.nextInt(directions.length);
		Vector2 chosenDirection = directions[index];
		ball.setVelocity(chosenDirection.normalized().mult(BALL_SPEED));
	}

	private void resetBallPosition(GameObject ball, Vector2 windowDimensions) {
		Vector2[] dirs = {
				new Vector2(1, 1), new Vector2(-1, 1),
				new Vector2(1, -1), new Vector2(-1, -1)
		};
		int randomIndex = random.nextInt(dirs.length);
		ball.setCenter(windowDimensions.mult(0.5f));
		ball.setVelocity(dirs[randomIndex].normalized().mult(BALL_SPEED));
	}

	private void initBall(ImageReader imageReader,
						  SoundReader soundReader,
						  Vector2 windowDimensions) {

		Renderable ballImage = imageReader.readImage(BALL_IMG_PATH, true);
		Sound collisionSound = soundReader.readSound(BALL_COLLISION_SOUND_PATH);

		GameObject ball = new Ball(
				Vector2.ZERO,
				BALL_SIZE,
				ballImage,
				collisionSound,
				this::onLifeLost
		);

		ball.setCenter(windowDimensions.mult(0.5f));
		setRandomBallVelocity(ball);

		this.ball = ball;
		gameObjects().addGameObject(ball);
	}

	// ==== Initialize Background ====

	private void initBackground(ImageReader imageReader, Vector2 windowDimensions) {
		Renderable backgroundImage = imageReader.readImage(BACKGROUND_IMG_PATH, false);
		GameObject background = new GameObject(
				Vector2.ZERO,
				windowDimensions,
				backgroundImage
		);
		background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects().addGameObject(background, Layer.BACKGROUND);
	}


	// ==== Paddle initialization ====

	private void initPaddle(ImageReader imageReader,
							UserInputListener inputListener,
							Vector2 windowDimensions) {

		Renderable paddleImage = imageReader.readImage(PADDLE_IMG_PATH, true);
		GameObject paddle = new Paddle(
				Vector2.ZERO,
				PADDLE_SIZE,
				paddleImage,
				inputListener,
				windowDimensions
		);

		paddle.setCenter(new Vector2(
				windowDimensions.x() / 2,
				windowDimensions.y() - PADDLE_Y_OFFSET
		));

		paddle.setTag(MAIN_PADDLE_TAG); // here?
		gameObjects().addGameObject(paddle);
	}


	// ==== Create Walls include Death Zone ====

	private void createWalls(Vector2 windowDimensions) {
		//Create left wall
		GameObject leftWall = new GameObject(
				Vector2.ZERO,
				new Vector2(WALL_THICKNESS, windowDimensions.y()),
				null
		);
		gameObjects().addGameObject(leftWall, Layer.STATIC_OBJECTS);

		//Create right wall
		GameObject rightWall = new GameObject(
				new Vector2(windowDimensions.x() - WALL_THICKNESS, 0),
				new Vector2(WALL_THICKNESS, windowDimensions.y()),
				null
		);
		gameObjects().addGameObject(rightWall, Layer.STATIC_OBJECTS);

		//Create top wall
		GameObject topWall = new GameObject(
				Vector2.ZERO,
				new Vector2(windowDimensions.x(), WALL_THICKNESS),
				null
		);
		gameObjects().addGameObject(topWall, Layer.STATIC_OBJECTS);

		//Create bottom wall - DeathZone
		deathZone = new GameObject(
				new Vector2(0, windowDimensions.y() - DEATH_ZONE_Y_OFFSET),
				new Vector2(windowDimensions.x(), DEATH_ZONE_HEIGHT),
				null
		);
		deathZone.setTag(TAG_LOWER_BORDER);
		gameObjects().addGameObject(deathZone, Layer.STATIC_OBJECTS);
	}

	// ==== Create Bricks Grid ====
	private void createBricksGrid(ImageReader imageReader, Vector2 windowDimensions) {
		int bricksPerRow = DEFAULT_BRICKS_PER_ROW;
		int numRows = DEFAULT_NUM_ROWS;

		if (args.length == 2) {
			bricksPerRow = Integer.parseInt(args[0]);
			numRows = Integer.parseInt(args[1]);
		}

		createBricks(imageReader, bricksPerRow, numRows, windowDimensions);
	}

	private void createBricks(ImageReader imageReader,
							  int bricksPerRow,
							  int numRows,
							  Vector2 windowDimensions) {

		float brickWidth = (windowDimensions.x() - BRICK_SPACING * (bricksPerRow + 1)) / bricksPerRow;
		// calculate width based on spacing and number of bricks

		Renderable brickImage = imageReader.readImage(BRICK_IMG_PATH, false);

		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < bricksPerRow; col++) {
			CollisionStrategy strategy = strategyMaker.createStrategy();
			float brickX = BRICK_SPACING + col * (brickWidth + BRICK_SPACING);
			float brickY = BRICKS_START_Y + row * (BRICK_HEIGHT + BRICK_SPACING);

			GameObject brick = new Brick(
					new Vector2(brickX, brickY),
						new Vector2(brickWidth, BRICK_HEIGHT),
						brickImage,
						row,
						col,
						strategy
				);
				gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
				bricksCounter.increment(); // increment brick counter
			}
		}
	}


	// ==== Lives UI initialization ====

	private void initLivesUI(ImageReader imageReader) {
		lives = INIT_LIVES;

		Vector2 windowSize = windowController.getWindowDimensions();
		float uiY = windowSize.y() - LIVES_UI_PADDING_BOTTOM;

		// numeric UI
		livesTextRenderable = new TextRenderable(Integer.toString(lives));
		livesTextRenderable.setColor(Color.GREEN);

		livesText = new GameObject(
				new Vector2(LIVES_UI_PADDING_LEFT, uiY),
				LIVES_TEXT_DIMENSIONS,
				livesTextRenderable
		);
		livesText.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects().addGameObject(livesText, Layer.UI);

		// graphical hearts UI
		hearts = new GameObject[MAX_LIVES];
		Renderable heartImage = imageReader.readImage(HEART_IMG_PATH, true);

		float baseX = LIVES_TEXT_DIMENSIONS.x();

		for (int i = 0; i < MAX_LIVES; i++) {
			GameObject heart = new GameObject(
					new Vector2(baseX + i * HEART_SPACING, uiY + HEART_UI_Y_OFFSET),
					HEART_SIZE,
					heartImage
			);
			heart.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
			hearts[i] = heart;

			// show only initial lives
			if (i < INIT_LIVES) {
				gameObjects().addGameObject(heart, Layer.UI);
			}
		}
	}

	/**
	 * Updates the visual UI to match the current 'lives' value:
	 * - Updates numeric display and color
	 * - Shows/removes heart icons based on the current life count
	 */
	private void updateLivesUI() {
		livesTextRenderable.setString(Integer.toString(lives));

		if (lives >= GREEN_BORDER) {
			livesTextRenderable.setColor(Color.GREEN);
		} else if (lives == YELLOW_BORDER) {
			livesTextRenderable.setColor(Color.YELLOW);
		} else {
			livesTextRenderable.setColor(Color.RED);
		}

		// update visible hearts
		for (int i = 0; i < MAX_LIVES; i++) {
			if (i < lives) {
				gameObjects().addGameObject(hearts[i], Layer.UI);
			} else {
				gameObjects().removeGameObject(hearts[i], Layer.UI);
			}
		}
	}


	private void onLifeLost() {
		lives--;

		if (lives <= 0) {
			boolean playAgain = windowController.openYesNoDialog(MSG_LOSE);
			if (playAgain) windowController.resetGame();
			else windowController.closeWindow();
			return;
		}

		resetBallPosition(ball, windowController.getWindowDimensions());
		updateLivesUI();
	}

	/**
	 * Restores one life when the heart disc is caught.
	 * <p>
	 * Does nothing if the player already has the maximum number of lives.
	 */
	public void addLife() {
		if (lives < MAX_LIVES) {
			lives++;
			updateLivesUI();
		}
	}

	/**
	 * Updates the game state each frame.
	 * <p>
	 * Checks for win conditions and handles debug key presses.
	 *
	 * @param deltaTime The time elapsed since the last frame update
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		// check for win condition
		if (bricksCounter.value() == 0) {
			endGameWithWin();
			return;
		}

		// debug: press 'W' to win
		if (inputListener.isKeyPressed(KeyEvent.VK_W)) {
			endGameWithWin();
		}
	}

	private void endGameWithWin() {
		boolean playAgain = windowController.openYesNoDialog(MSG_WIN);
		if (playAgain) {
			windowController.resetGame();
		} else {
			windowController.closeWindow();
		}
	}

	/**
	 * Entry point for the Bricker game application.
	 * <p>
	 * Creates and runs the game manager. Optional command-line arguments:
	 * <ul>
	 *   <li>args[0]: Number of bricks per row (default: 8)</li>
	 *   <li>args[1]: Number of rows (default: 7)</li>
	 * </ul>
	 *
	 * @param args Command-line arguments for game configuration
	 */
	public static void main(String[] args) {
		new BrickerGameManager(WINDOW_TITLE, WINDOW_SIZE, args).run();
	}
}



