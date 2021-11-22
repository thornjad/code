import sys
import pygame as game
from boids.boid import Boid

# Types
BoidList = game.sprite.RenderUpdates


def main() -> None:
    # set up the game
    game.init()
    game.event.set_allowed([game.QUIT])
    game.display.set_caption("Boids")

    fps = 60  # TODO higher?
    fpsClock = game.time.Clock()

    # TODO configurable
    size = 1000, 1000
    num_boids = 10

    screen = game.display.set_mode(size)
    background = game.Surface(screen.get_size()).convert()
    boids: BoidList = make_boids(num_boids)

    dt = 1 / fps
    while True:
        update(screen, background, dt, boids)
        dt = fpsClock.tick(fps)


def make_boids(num: int) -> BoidList:
    boids = game.sprite.RenderUpdates()
    for _ in range(num):
        boids.add(Boid())
    return boids


def update(screen, background, dt, boids) -> None:
    """
    Update game state for a new frame. DT is time passed, BOIDS is the boids to place.
    """
    # TODO scale with time, x += v * dt

    for event in game.event.get():
        if event.type == game.QUIT:
            sys.exit()
        # TODO keys for changing parameters? Or probably text boxes if engine supports it?

    screen.fill((0, 0, 0))
    for b in boids:
        b.update(boids)
        screen.blit(b.image, b.pos)
    game.display.flip()


def draw(screen, bg, boids) -> None:
    """Draw a frame"""
    # TODO need to clear or fill?
    boids.clear(screen, bg)
    boids.draw(screen)


if __name__ == "__main__":
    main()
