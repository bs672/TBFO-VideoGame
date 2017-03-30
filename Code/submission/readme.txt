Physics
1. Heavier things take more force to accelerate by the same amount.
2. Momentum is the vector quantity m*v, basically mass in motion, and impulse is change in momentum.
3. This is one where no kinetic energy is lost. An example is 2 pool balls colliding.
4. This is where the 2 colliding things stick together. Like if you throw 2 balls of clay into each other.
5. The ratio of the final to initial velocity of 2 colliding object. Can range from 0 to 1.
6. The speed with which an object rotates about a point (measured in radins/degrees per unit time), angular acceleration is the change in this.
7. Torque is how a force applied to an object rotates it about a point.
8. Torque is directly proportional to the moment of inertia and the angular acceleration.

Box2D
1. A body is what you woudl apply forces to. A shape is just used for collision.
2. Physical properties and forces apply to bodies.
3. You might do that if you want to make a non-standard looking object.
4. A world is the thing that manages memory and the objects in the game.
5. When an object is sleeping, it uses very little CPU power. This should happen whenever an object is at rest.
6. A dynamic body is affected by forces and interacts with other bodies. A static body is the opposite except it interacts with dynamic bodies. You specify the type when you create it.
7. A dynamic body with the bullet field set to true is a bullet. Fast moving objects that may tunnel should be made into bullets
8. A contact listener will help notify you of anything involving collision. This is helpful when you want there to be no collision response using a sensor.
9. Once you instantiate a body, you can use the createFixture(Shape shape, float density) to add a shape to it.

For the spinner you can't just set its type to static because then you can't spin it by shooting it.