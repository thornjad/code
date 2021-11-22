from random import random
from typing import List, Optional, TypeVar

import pygame as game
from pygame.math import Vector2

# Types
T = TypeVar("T", bound="Boid")
BoidList = game.sprite.RenderUpdates


class Boid(game.sprite.Sprite):
    # TODO use SRCALPHA?
    img = game.surface.Surface((20, 10))

    # third arg is polygon points
    game.draw.polygon(img, game.Color("white"), [(0, 0), (20, 10), (0, 20)])

    max_x, max_y = 0, 0  # units (placeholders)
    max_speed = 0.5  # units/tick
    max_accel = 0.1  # units/tick²
    max_crowding = 20  # units, min units between boids before avoidance kicks in
    perception_distance = 100  # units

    def __init__(self) -> None:
        super(Boid, self).__init__()

        # only calculate once, effectively
        if Boid.max_x == 0:
            info = game.display.Info()
            Boid.max_x, Boid.max_y = info.current_w, info.current_h

        # give ourselves a random starting point
        # TODO random pos/neg velocity
        # TODO overlay a desired distribution
        self.pos = Vector2(random() * Boid.max_x, random() * Boid.max_y)
        self.vel = Vector2(random() * Boid.max_speed, random() * Boid.max_speed)
        self.image = Boid.img.copy()
        self.rect = self.img.get_rect(center=self.pos)

    def update(self, boids: BoidList) -> None:
        avoidance: Vector2 = self.collision_avoidance(boids)
        centering: Vector2 = self.flock_centering(boids)
        matching: Vector2 = self.velocity_matching(boids)
        # TODO only need to clamp here?
        accel = self.clamp_velocity(avoidance + centering + matching)

        # TODO this is ignoring angular velocity
        self.vel += accel

        self.pos += self.vel
        self.screen_warp()
        _, angle = self.vel.as_polar()
        self.img = game.transform.rotate(Boid.img, -angle)
        self.rect = self.img.get_rect(center=self.pos)

    # TODO paper identified inverse square of distance as a decent metric for weighting how much the
    # boid wants to adhere. Also consider inverse cube?

    def collision_avoidance(self: T, boids: BoidList) -> Vector2:
        """Return velocity away from collision."""
        steer_vel = Vector2()
        neighbors: List[T] = self.get_neighbors(boids, self.max_crowding)
        if (nlen := len(neighbors)) > 0:
            for b in neighbors:
                steer_vel += Vector2(self.pos - b.pos) / self.pos.distance_to(b.pos)
            steer_vel /= nlen
            steer_vel = self.clamp_velocity(steer_vel)
        # TODO check on this, if no neighbors, this tends to stop, possibly need better way to say
        # "no change"?
        return steer_vel

    def flock_centering(self: T, boids: BoidList) -> Vector2:
        """Return velocity toward the local flock center."""
        steer_vel = Vector2()
        neighbors: List[T] = self.get_neighbors(boids)
        if (nlen := len(neighbors)) > 0:
            for b in neighbors:
                steer_vel += b.pos
            steer_vel /= nlen
            steer_vel = self.clamp_velocity(steer_vel)
        return steer_vel

    def velocity_matching(self: T, boids: BoidList) -> Vector2:
        """Return velocity in line with local group velocity."""
        steer_vel = Vector2()
        neighbors: List[T] = self.get_neighbors(boids)
        if (nlen := len(neighbors)) > 0:
            for b in neighbors:
                steer_vel += b.vel
            steer_vel /= nlen
            # TODO need to subtract self position before clamp?
            steer_vel = self.clamp_velocity(steer_vel)
        return steer_vel

    def get_neighbors(
        self: T, boids: BoidList, perception: Optional[int] = None
    ) -> List[T]:
        """
        Return list of boids in the local vicinity, with local defined by perception with
        inclusive bounds.
        """
        perception_dist: int = (
            self.perception_distance if perception is None else perception
        )
        return list(
            filter(
                lambda x: (
                    x != self and (x.pos - self.pos).magnitude() <= perception_dist
                ),
                boids,
            )
        )

    def clamp_velocity(self, steer_vel: Vector2) -> Vector2:
        """
        Return maximum allowed velocity toward steer_vel, such that velocity does not exceed
        bounds for speed and acceleration. If bounds would have been exceeded, the velocity returned
        will not.
        """
        if steer_vel.magnitude() > self.max_speed:
            steer_vel = steer_vel.normalize() * self.max_speed
        steer_vel -= self.vel
        if steer_vel.magnitude() > self.max_accel:
            steer_vel = steer_vel.normalize() * self.max_accel
        return steer_vel

    def screen_warp(self) -> None:
        """
        If we've hit a screen edge, warp to the corresponding side.

        for z ∈ {x, y}, z < 0 -> max z, z > max z -> 0
        """
        if self.pos.x < 0:
            self.pos.x = Boid.max_x
        elif self.pos.x > Boid.max_x:
            self.pos.x = 0

        if self.pos.y < 0:
            self.pos.y = Boid.max_y
        elif self.pos.y > Boid.max_y:
            self.pos.y = 0
