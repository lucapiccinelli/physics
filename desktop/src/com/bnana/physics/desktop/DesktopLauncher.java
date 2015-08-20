package com.bnana.physics.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bnana.physics.Const;
import com.bnana.physics.Physics;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Const.VIEW_WIDTH;
		config.height = Const.VIEW_HEIGHT;
		new LwjglApplication(new Physics(), config);
	}
}
