package me.totom3;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public abstract class AbstractEngine implements GameEngine {

	private final Scene scene;
	private final Pane pane;

	public AbstractEngine(Scene scene, Pane pane) {
		this.scene = scene;
		this.pane = pane;
	}

	@Override
	public final void tick() {
		if (pane.isDisable()) {
			return;
		}
		tick0();
		Platform.runLater(this::render);
	}

	protected abstract void tick0();

	public double screenHeight() {
		return pane.getHeight();
	}

	public double screenWidth() {
		return pane.getWidth();
	}

	@Override
	public Pane getPane() {
		return pane;
	}

	@Override
	public Scene getScene() {
		return scene;
	}

}
