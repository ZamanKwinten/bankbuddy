package bank.buddy.bank;

import java.io.InputStream;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class BankLabel extends HBox {

	public BankLabel(String bankName, InputStream icon) {
		setAlignment(Pos.CENTER_LEFT);
		setSpacing(5);

		var label = new javafx.scene.control.Label(bankName);
		label.setFont(new Font(50));

		var image = new Image(icon);

		var view = new ImageView(image);
		view.setFitHeight(50);
		view.setFitWidth(50);

		getChildren().addAll(view, label);

		setCursor(Cursor.HAND);
	}

}
