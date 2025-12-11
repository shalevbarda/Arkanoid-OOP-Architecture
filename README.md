# 🧱 Bricker - Arkanoid with Design Patterns

![Java](https://img.shields.io/badge/Java-11%2B-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Pattern](https://img.shields.io/badge/Patterns-Strategy%20%7C%20Decorator%20%7C%20Factory%20%7C%20Singleton-blueviolet?style=flat)
![Game Engine](https://img.shields.io/badge/Engine-DanoGL-green?style=flat)

> A modular, object-oriented implementation of the classic Breakout game. This project focuses on **flexible software architecture**, allowing dynamic runtime behaviors and complex collision interactions using Design Patterns.

---
## 🎮 Gameplay
![Gameplay Demo](https://github.com/user-attachments/assets/0517051c-e1ed-49e5-8c73-2553a351be20)

### Key Features
* **🧨 Exploding Bricks:** Triggers a chain reaction, destroying neighboring bricks recursively.
* **🥎 Multiball:** Spawns two extra "puck" balls that behave independently.
* **❤️ Extra Life:** Drops a falling heart disc. If caught by the main paddle, it restores 1 life.
* **🛶 Second Paddle:** Adds a temporary secondary paddle in the center. It mirrors the user's movement and disappears after **4 ball hits**.

---

## 🏗️ Architecture & Design Patterns

The core strength of this project is the decoupling of game logic from game objects. We utilized four major design patterns to achieve a modular and maintaining codebase.

### 1. The Strategy Pattern (Core Logic)
Instead of hardcoding collision logic into the `Brick` class, each brick holds a reference to a `CollisionStrategy` interface. When hit, the brick simply delegates the task to its strategy.
* **Benefit:** Allows mixing and matching behaviors without changing the Brick class.

### 2. The Decorator Pattern (Stacking Behaviors)
We needed bricks that could have *multiple* effects (e.g., "Exploding" AND "Extra Life"). Using the Decorator pattern, strategies can wrap other strategies.
* **Example:** `ExplodingBrickStrategy` executes its explosion logic and then calls the `onCollision` of the strategy it wraps.

### 3. The Factory Pattern (Probabilistic Generation)
The `StrategyMaker` class acts as a Factory. It encapsulates the complex creation logic and probability distribution (50% Basic, 40% Special, 10% Double).

### 4. The Singleton Pattern (Resource Management)
The `ExtraPaddle` class is implemented as a **Singleton**.
* **Logic:** The game allows only *one* extra paddle at a time. The `getInstance()` method ensures that if a bonus triggers while an extra paddle is already active, we don't spawn a second one.

```java
// From ExtraPaddle.java
public static ExtraPaddle getInstance(...) {
    if (instance == null) {
        instance = new ExtraPaddle(...);
        return instance;
    }
    return null; // Prevents duplicate paddles
}
```

---

## 🛠️ Installation & Controls

1.  **Clone the repo:**
    ```bash
    git clone [https://github.com/shalevbarda/Bricker.git](https://github.com/shalevbarda/Bricker.git)
    ```
2.  **Run the Game:**
    Run the `main` method in `BrickerGameManager.java`.

### Controls
* **Left / Right Arrows:** Move Paddle (controls both Main and Extra paddle).

---
*Developed by [Shalev Barda](https://github.com/shalevbarda)*
