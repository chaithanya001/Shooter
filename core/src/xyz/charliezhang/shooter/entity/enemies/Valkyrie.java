package xyz.charliezhang.shooter.entity.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import xyz.charliezhang.shooter.Assets;
import xyz.charliezhang.shooter.MainGame;

import static xyz.charliezhang.shooter.Config.*;

public class Valkyrie extends Enemy
{
    private boolean intro;
    private long moveTimer;
    private int stop;

    public Valkyrie() {
        super();

        textureAtlas = Assets.manager.get(VALKYRIE_PATH, TextureAtlas.class);
        animation = new Animation(1/20f, textureAtlas.getRegions());

        sprite.setSize(91, 66);

        health = maxHealth = VALKYRIE_HEALTH;
        damage = VALKYRIE_DAMAGE;
        score = VALKYRIE_SCORE;

        intro = VALKYRIE_INITIAL_INTRO;
    }

    //json read method
    @Override
    public void read (Json json, JsonValue jsonMap) {
        super.read(json, jsonMap);
        stop = jsonMap.getInt("stop");
    }

    @Override
    public void update() {
        if(intro)
        {
            if(sprite.getY() <= MainGame.HEIGHT - stop) {
                intro = false;
                moveTimer = System.nanoTime();
                setDirection(0, 0);
            }
        }
        else
        {
            if((System.nanoTime() - moveTimer) / 1000000 > 500) {
                if(direction.x == 0) {
                    if(getPosition().x > manager.getViewport().getWorldWidth() - 200)
                        setDirection(-4, 0);
                    else if(getPosition().x < 200)
                        setDirection(4, 0);
                    else {
                        if(Math.random() >= 0.5) setDirection(4, 0);
                        else setDirection(-4, 0);
                    }
                }
                else {
                    setDirection(0, 0);
                }
                moveTimer = System.nanoTime();
            }
        }

        if(sprite.getY() < MainGame.HEIGHT)
        {
            if(System.currentTimeMillis() - lastFire >= 800)
            {
                EnemyLaser g1 = manager.getEnemyLaserPool().obtain();
                EnemyLaser g2 = manager.getEnemyLaserPool().obtain();
                g1.init(manager, this, 3);
                g1.setDirection(0.1f, -8);
                g1.setPosition(sprite.getX() + sprite.getWidth() / 2 + 20, sprite.getY());
                g2.init(manager, this, 3);
                g2.setDirection(-0.1f, -8);
                g2.setPosition(sprite.getX() + sprite.getWidth() / 2 - 20, sprite.getY());
                manager.spawnEnemyLaser(g1);
                manager.spawnEnemyLaser(g2);
                lastFire = System.currentTimeMillis();
            }
        }
        super.update();
    }

    @Override
    public void render(SpriteBatch sb)
    {
        sprite.setRegion(animation.getKeyFrame(animationTime, true));
        sb.draw(Assets.manager.get(HEALTH_PATH, Texture.class), sprite.getX(), sprite.getY() + sprite.getHeight(), sprite.getWidth(), 5);
        sb.draw(Assets.manager.get(HEALTH_FILL_PATH, Texture.class), sprite.getX(), sprite.getY() + sprite.getHeight(), (int)(sprite.getWidth() * ((double)health / maxHealth)), 5);
        super.render(sb);
    }

}
