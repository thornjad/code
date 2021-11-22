import pygame as game

from boids.boid import Boid

# FIXME NOT WORKING! We need some overarching video loop in order for pygame to do some of its stuff
# properly, apparently


class TestBoid:
    def test_get_neighbors_hypot(self):
        pass
        # boids = game.sprite.RenderUpdates()
        # for i in range(20):
        #     ib = Boid()
        #     ib.pos = game.math.Vector2(i * 10, i * 10)
        #     boids.add(ib)

        # test_boid = Boid()
        # test_boid.pos = game.math.Vector2(10, 10)

        # neighbors = b.get_neighbors(self.boids)
        # x = Boid.perception_distance ** 2
        # expected_dist = math.sqrt(x, x)

    def test_get_neighbors_horiz(self):
        boids = game.sprite.RenderUpdates()
        for i in range(20):
            ib = Boid()
            ib.pos = game.math.Vector2(i * 10, 0)
            boids.add(ib)

        test_boid = Boid()
        test_boid.pos = game.math.Vector2(0, 0)

        neighbors = test_boid.get_neighbors(boids)
        sight = Boid.perception_distance
        # number visible in plane, plus one at the same spot. Invalid location by motion rules, but
        # its a test
        expected = (sight / 10) + 1
        assert neighbors.length() == expected

    def test_get_neighbors_vert(self):
        boids = game.sprite.RenderUpdates()
        for i in range(20):
            ib = Boid()
            ib.pos = game.math.Vector2(0, i * 10)
            boids.add(ib)

        test_boid = Boid()
        test_boid.pos = game.math.Vector2(0, 0)

        neighbors = test_boid.get_neighbors(boids)
        sight = Boid.perception_distance
        # number visible in plane, plus one at the same spot. Invalid location by motion rules, but
        # its a test
        expected = (sight / 10) + 1
        assert neighbors.length() == expected
