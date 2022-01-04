package Entity;

import GameState.GameStateManager;
import TileMap.TileMap;

import java.awt.*;

public class Projectile extends Hazard {
    public Projectile(TileMap tm, GameStateManager gsm, double spawnX, double spawnY) {
        super(tm, gsm);

        width = 30;
        height = 30;

        maxSpeed = 1.5;

        try {
            setPosition(spawnX, spawnY);
        } catch (LethalDamageException e) {
            this.kill();
        }

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
        setMapPosition();

        g.setColor(Color.RED);
        g.fillRect(
                (int) (x + xmap - (width / 2)),
                (int) (y + ymap - (height / 2)),
                this.width,
                this.height
        );
    }


}
