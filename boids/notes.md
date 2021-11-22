### Behavior

#### Motion

Boid motion is governed by a set of defined rules, which is informed by physics and the natural limits of what a real bird can do. It is unrealistic, for example, for a bird in motion to suddenly come to a complete stop, then accelerate to 100 m/s in any amount of time, and certainly not quickly. 

Boids follow what Reynolds deems geometric flight. That is, the boid moves along a discrete, rigid transformation along a geometric curve. The discrete nature of the path allows us to simulate continuous motion using blur, without requiring unnecessary computation. The rigid nature allows us to represent decisions as rotations about a local axis. 

Each boid possesses a local coordinate system (one coordinate for each dimension of the simulation). Boids make independent flight decisions which transform this local coordinate system relative to the static global coordinate system, collectively called _steering_.

Geometric flight must also correctly model conservation of momentum, disallowing unrealistic acceleration. It also models air resistance by enforcing a maximum speed, putting a limit on the power of a boid. For convenience, we will also assume the boid is like most birds in that it cannot have a negative speed, and cannot maintain zero speed for long (we are not modeling boids at rest).

Unlike Reynolds' program, since this is in 2D (for the time being), we will omit modeling gravity, since it only meaningfully affects banking, ascending and descending.

#### Coordination

For a boid flock to be interesting, each individual must make decisions based on the state of the whole, or at least the local group of other boids. Reynolds presents three basic rules for such behavior, in order of decreasing precedence:

> 1. Collision avoidance avoid collisions with nearby flockmates
>
> 2. Velocity Matching: attempt to match velocity with nearby flockmates
>
> 3. Flock Centering: attempt to stay close to nearby flockmates



## Future improvement ideas

- 3D simulation
- Explore additional dimensions, what would that even look like???
- improved animations
- obstacle avoidance
- goal seeking
- meta-flock centering: smaller flocks seek to join with larger flocks
  - reynolds mentions a maneuver wave, where birds on the fringes of the flock make the decision to join the distant flock (or other goal/obstacle action), and the change travels through the local flock. Interestingly, this wave travels faster than individual reaction time would suggest (three times faster), perhaps indicating that they see the wave oncoming and time their own reaction to match
