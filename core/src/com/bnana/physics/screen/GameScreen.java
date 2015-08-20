package com.bnana.physics.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.bnana.physics.Physics;
import com.bnana.physics.stage.PhysicsStage;

/**
 * Created by luca.piccinelli on 20/08/2015.
 */
public class GameScreen implements Screen {
    private final PhysicsStage stage;

    public GameScreen(Physics physics) {
        this.stage = new PhysicsStage();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
