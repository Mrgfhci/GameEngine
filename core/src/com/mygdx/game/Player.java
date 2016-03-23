package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by k9sty on 2016-03-12.
 */


public class Player {
    // what benefits are you getting from your player being an Actor? None, unless you are going to get a stage on the screen.
    State state; // enumeration definition below
    Body bdyMain, bdyFoot;
    BodyDef bdefMain, bdefFoot;
    FixtureDef fdefPlayer, fdefFoot;
    PolygonShape shape;
    TextureAtlas taIdle = new TextureAtlas(Gdx.files.internal("player/idle/idle.pack"));
    TextureAtlas taRun = new TextureAtlas(Gdx.files.internal("player/run/run.pack"));
    Sprite[] arSprIdle = new Sprite[9];
    Sprite[] arSprRun = new Sprite[9];
    Sprite sprPlayer; // a temporary Sprite to clean up the draw function - useless since I need a TR.
    TextureRegion trPlayer; // since an animation returns a TextureRegion, and not a Sprite.
    Animation aniIdle, aniRun;
    float fElapsedTime = 0;
    World world;
    float fX, fY, fW, fH; // for player dimensions.

    boolean bRight = true;
    boolean isIdle; // getting this attribute out of the State enumeration.

    enum State {
        left, right
        // enumeration for animations
        // why not iL iR, rL, rR ???
    }

    Player(World world, Vector2 v2SpawnPoint) {
        this.world = world;
        createbdyMain(v2SpawnPoint);
        createbdyFoot();
    }


    private void createbdyMain(Vector2 v2SpawnPoint) {// v2SpawnPt
        isIdle = true;
        //this.state = state.idle;
        for (int i = 1; i < 10; i++) {
            arSprIdle[i - 1] = new Sprite(taIdle.findRegion("idle (" + i + ")"));
            arSprRun[i - 1] = new Sprite(taRun.findRegion("run (" + i + ")"));
        }
        // the next two variables are needed in the draw function.
        fW = arSprIdle[0].getWidth();
        fH = arSprIdle[0].getHeight();
        aniIdle = new Animation(10, arSprIdle); // the first int is the frame duration. Idle is slower than running.
        aniRun = new Animation(5, arSprRun);
        bdefMain = new BodyDef();
        shape = new PolygonShape();

        bdefMain.position.set(new Vector2(v2SpawnPoint.x / 2, v2SpawnPoint.y / 2));
        bdefMain.type = BodyDef.BodyType.DynamicBody;
        bdyMain = world.createBody(bdefMain);
        bdyMain.setFixedRotation(true);

        shape.setAsBox(arSprIdle[0].getWidth() / 4, arSprIdle[0].getHeight() / 4);
        fdefPlayer = new FixtureDef();
        fdefPlayer.shape = shape;
        fdefPlayer.filter.categoryBits = 0;
        fdefPlayer.friction = 1;
        bdyMain.setSleepingAllowed(false);
        bdyMain.createFixture(fdefPlayer);
        shape.dispose();
        // set categorybit to 0 so it collides with nothing
    }

    private void createbdyFoot() {
        shape = new PolygonShape();

        shape.setAsBox(arSprIdle[0].getWidth() / 4, 0.2f, new Vector2(bdyMain.getWorldCenter().x / 4 - arSprIdle[0].getWidth() / 4 + 0.5f, bdyMain.getPosition().y / 4 - arSprIdle[0].getHeight() - 9.5f), 0);
        fdefFoot = new FixtureDef();
        fdefFoot.shape = shape;
        fdefFoot.filter.categoryBits = 1;

        bdyMain.createFixture(fdefFoot);
        shape.dispose();
        // create a foot sensor to detect whether or not the player is grounded
    }

    Vector3 getPosition() {
        return new Vector3(bdyMain.getPosition().x, bdyMain.getPosition().y, 0);
    }

    void draw(SpriteBatch sb) {
        // drawing sprite on player body using default library, not using animatedbox2dsprite because it doesn't loop the animation
        fElapsedTime++;
        // I will comment out the code below to show how I will try to optimize it. Don't code is logical, but the isIdle
        // may be able to clean it up. I will use his bRight variable to get rid of the need for any "states".
        // Don's code is commented below
        fX =  bdyMain.getPosition().x;
        fY =  bdyMain.getPosition().y;
        // the next two vars should be populated in the body creation, since the values never change.
        // since these values are the same as the player running, they are all good.
        // update - I added the fW and fH population in the createBdyMain function since we only need to do it once.



        if (isIdle) {
            trPlayer = aniIdle.getKeyFrame(fElapsedTime, true);
            if (bRight) {
                //sb.draw(aniIdle.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x - arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, arSprIdle[0].getWidth() / 2, arSprIdle[0].getHeight() / 2);
                //sb.draw(aniIdle.getKeyFrame(fElapsedTime, true), fX - fW / 4, fY - fH / 4, fW / 2, fH / 2);
                sb.draw(trPlayer, fX - fW / 4, fY - fH / 4, fW / 2, fH / 2);
            } else {
                //sb.draw(aniIdle.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x + arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, -arSprIdle[0].getWidth() / 2, arSprIdle[0].getHeight() / 2);
                sb.draw(trPlayer, fX + fW / 4, fY - fH / 4, -fW / 2, fH / 2);
            }
        } else {
            trPlayer = aniRun.getKeyFrame(fElapsedTime, true);
            if (bRight) {
                sb.draw(trPlayer, bdyMain.getPosition().x - arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, arSprRun[0].getWidth() / 2, arSprRun[0].getHeight() / 2);
            } else {
                sb.draw(trPlayer, bdyMain.getPosition().x + arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, -arSprRun[0].getWidth() / 2, arSprRun[0].getHeight() / 2);
            }
        }

        /*
        if (this.state == state.idle) {
            if (bRight) {
                sb.draw(idle.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x - arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, arSprIdle[0].getWidth() / 2, arSprIdle[0].getHeight() / 2);
            } else {
                sb.draw(idle.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x + arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, -arSprIdle[0].getWidth() / 2, arSprIdle[0].getHeight() / 2);
            }
        } else if (this.state == state.right) {
            sb.draw(run.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x - arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, arSprRun[0].getWidth() / 2, arSprRun[0].getHeight() / 2);
        } else if (this.state == state.left) {
            sb.draw(run.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x + arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, -arSprRun[0].getWidth() / 2, arSprRun[0].getHeight() / 2);
        }*/
        }

    void move() {
        // i don't even need to pass a keycode, i just call player.move() in render and i can check
        // for keypresses by detecting if a button is down or not
        if (bdyMain.getLinearVelocity().x > 100) {
            bdyMain.getLinearVelocity().x--;
        } else if (bdyMain.getLinearVelocity().x < -100) {
            bdyMain.getLinearVelocity().x++;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            //this.state = state.left;
            bRight = false;
            isIdle = false;
            //bdyMain.applyForceToCenter(-200, 0, true);
            bdyMain.setLinearVelocity(-100, bdyMain.getLinearVelocity().y);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.state = state.right;
            bRight = true;
            isIdle = false;
            //bdyMain.applyForceToCenter(200, 0, true);
            bdyMain.setLinearVelocity(100, bdyMain.getLinearVelocity().y);
        }
    }

    void stop() {
        // stop movement on release of keycode
        //this.state = state.idle;
        isIdle = true;
        bdyMain.setLinearVelocity(0, bdyMain.getLinearVelocity().y);
    }

    boolean isGrounded = true;

    void jump() {
        // using getmass so i can get around being limited by gravity
        // gravity sucks
        // there is one issue: jumping while moving gives you a lower jump height than jumping while standing
        // used applyForceToCenter and setLinearVelocity, same result
        bdyMain.applyLinearImpulse(new Vector2(0, bdyMain.getMass() * 500), bdyMain.getWorldCenter(), true);
    }

    Vector2 getLinearVelocity() {
        return bdyMain.getLinearVelocity();
    }

}
