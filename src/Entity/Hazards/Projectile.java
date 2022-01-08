package Entity.Hazards;

import Entity.EntityState;
import Entity.Hazards.Hazard;
import Entity.LethalDamageException;
import GameState.GameStateManager;
import TileMap.TileMap;

import java.awt.*;

public class Projectile extends Hazard {
    public Projectile(TileMap tm, GameStateManager gsm, double spawnX, double spawnY, double speed) {
        super(tm, gsm);

        width = 30;
        height = 30;
        collisionHeight = 30;
        collisionWidth = 30;

        maxSpeed = speed;

        this.x = spawnX;
        this.y = spawnY;

        this.currentAction = EntityState.IDLE;
    }

    @Override
    public void update() {
        try {
            this.setPosition(this.getX() - this.maxSpeed, this.y);
        } catch (LethalDamageException e) {
            this.kill();
        }

    }

    @Override
    public void draw(Graphics2D g) {

        g.setColor(Color.RED);
        g.fillRect(
                (int) (x + tileMap.getX() - (width / 2)),
                (int) (y + tileMap.getY() - (height / 2)),
                this.width,
                this.height
        );
    }
}
