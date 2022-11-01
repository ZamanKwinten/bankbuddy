package bank.buddy.bank.argenta;

import bank.buddy.ui.AsyncObservable;
import bank.buddy.ui.MaskedTextInputField;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class DigipasSection extends HBox {
	private final AsyncObservable<ArgentaClient> client;

	private MaskedTextInputField responseCode;

	public DigipasSection(AsyncObservable<ArgentaClient> client) {
		this.client = client;

		var digipasSteps = new VBox();
		digipasSteps.setSpacing(10);

		digipasSteps.getChildren().setAll(initStep1(), new Separator(), initStep2(), new Separator(), initStep3(),
				new Separator(), initStep4());

		var digipasImage = getImage("digipas.png");

		setSpacing(15);
		getChildren().setAll(digipasSteps, digipasImage);
	}

	private Node initStep1() {
		var step = new HBox();
		step.setAlignment(Pos.CENTER_LEFT);
		step.setSpacing(5);

		step.getChildren().setAll(new Label("Druk op"), getImage("m1.png"));

		return step;
	}

	private Node initStep2() {
		var step = new HBox();
		step.setAlignment(Pos.CENTER_LEFT);
		step.setSpacing(5);

		var challenge = new Label("Bezig met Laden");
		client.whenReady((client) -> {
			var challengeStr = client.getChallenge();
			Platform.runLater(() -> {
				challenge.setText(String.format("%s %s", challengeStr.substring(0, 4), challengeStr.substring(4)));
				getScene().getWindow().sizeToScene();
			});
		});

		challenge.setStyle(
				"-fx-background-color: rgba(0,74,101,.07); -fx-background-radius: 15px; -fx-text-fill: #004a65;");
		challenge.setPadding(new Insets(3));

		step.getChildren().setAll(new Label("Geef de challenge"), challenge, new Label("en druk op"),
				getImage("ok.png"));

		return step;

	}

	private Node initStep3() {
		var step = new HBox();
		step.setAlignment(Pos.CENTER_LEFT);
		step.setSpacing(5);

		step.getChildren().setAll(new Label("Geef je pincode in en druk op"), getImage("ok.png"));

		return step;
	}

	private Node initStep4() {
		var step = new VBox();
		step.setSpacing(10);

		responseCode = new MaskedTextInputField(".... ....");

		step.getChildren().setAll(new Label("Vul hier de response code in"), responseCode);
		return step;
	}

	private ImageView getImage(String icon) {
		return new ImageView(new Image(this.getClass().getResourceAsStream(icon)));
	}

	public MaskedTextInputField getResponse() {
		return responseCode;
	}
}
