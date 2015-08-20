package com.bnana.physics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bnana.physics.screen.GameScreen;

public class Physics extends Game {
	@Override
	public void create () {
		setScreen(new GameScreen(this));
	}

	@Override
	public void render () {
        super.render();
	}
}
