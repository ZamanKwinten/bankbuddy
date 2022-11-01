package bank.buddy.ui;

import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Utility class containing some methods to do fx calculations
 * 
 * @author KWZA
 *
 */
public class FXUtils {

	public static double calculateTextControlWidth(Font font, String text, double padding) {
		Text tmp = new Text(text);
		tmp.setFont(font);
		return tmp.getLayoutBounds().getWidth() + padding;
	}

	/**
	 * A nasty hack to ensure that the scene only increases in size if needed.
	 * 
	 * 
	 * @param scene
	 */
	public static void expandToScene(Scene scene) {
		var root = scene.getRoot();
		root.applyCss();

		root.resize(root.prefWidth(-1), root.prefHeight(-1));

		var width = root.getLayoutX() + root.getTranslateX() + root.getLayoutBounds().getWidth();
		var height = root.getLayoutY() + root.getTranslateY() + root.getLayoutBounds().getHeight();

		var window = scene.getWindow();

		if (width > window.getWidth()) {
			window.setWidth(width);
		}

		if (height > window.getHeight()) {
			window.setHeight(height);
		}

		root.resize(window.getWidth() - 5, window.getHeight() - 35);
	}
}
