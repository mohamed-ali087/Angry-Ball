# AngryBall – JavaFX Bouncing Ball Simulation
A JavaFX physics simulation featuring bouncing balls with realistic collision detection, gravity, friction, and trajectory prediction.

## Features
 
- **Physics simulation** — gravity, friction, and configurable restitution (bounciness)
- **Pane boundary collision** — balls bounce off the edges of the container
- **Line collision** — balls reflect off arbitrary line segments placed in the scene
- **Ball-to-ball collision** — mass-weighted elastic collision using vector decomposition along the collision normal
- **Trajectory prediction** — renders a dashed path showing where the ball will travel, updated live while dragging
- **Throw mechanic** — drag and release a ball to throw it; path preview updates in real time during the drag
- **Refresh-rate independent physics** — speed and acceleration scale with the configured frame rate so behavior stays consistent at any refresh rate
- **Builder pattern** - easier object construction.
- **Observer Pattern** - objects can see each other using observer lists.

## Demo
![demo](Demos/v1.0.0.gif)
