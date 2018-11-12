package me.totom3;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public interface GameEngine {

	void tick();

	void render();

	Pane getPane();

	Scene getScene();
	
	int getTickDelay();
}
