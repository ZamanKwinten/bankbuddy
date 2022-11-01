package bank.buddy.bank.argenta;

import java.util.function.Consumer;
import java.util.function.Supplier;

import bank.buddy.bank.BankSelectionStage;
import bank.buddy.ui.AsyncObservable;
import bank.buddy.ui.MaskedLabel;
import bank.buddy.ui.MaskedTextInputField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

abstract class ArgentaLoginStage extends Stage {
	private final AsyncObservable<ArgentaClient> client;

	private DigipasSection digipas;
	protected Supplier<String> cardNumberGetter;

	private Consumer<ArgentaClient> afterLogin;

	static ArgentaLoginStage withUnknownCardNumber() {
		return new ArgentaLoginStage() {
			@Override
			protected VBox initCardNumberSection() {
				var section = new VBox();
				section.setSpacing(10);
				var h1 = heading("1. Vul je kaartnummer in");

				var cardNumber = new MaskedTextInputField(".... .... .... .... .", 5);
				cardNumberGetter = cardNumber::getValue;

				section.getChildren().addAll(h1, cardNumber);
				return section;
			}

			@Override
			protected void handleCancel() {
				var stage = new BankSelectionStage();
				stage.show();
				this.close();
			}
		};
	}

	private static class CardNumberCell extends ListCell<String> {
		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null) {
				setText(null);
				setGraphic(null);
			} else {
				setText(null);
				var graph = new MaskedLabel(".... .... .... .... .", item);
				graph.setTextFill(Color.BLACK);
				setGraphic(graph);
			}
		}
	}

	static ArgentaLoginStage withKnownCardNumber(String[] cardNumbers) {
		return new ArgentaLoginStage() {
			@Override
			protected VBox initCardNumberSection() {
				var section = new VBox();
				section.setSpacing(10);
				var h1 = heading("1. Kaartnummer");
				section.getChildren().add(h1);

				if (cardNumbers.length > 1) {
					var cb = new ComboBox<>(FXCollections.observableArrayList(cardNumbers));
					cb.setCellFactory((param) -> new CardNumberCell());
					cb.setButtonCell(new CardNumberCell());
					cardNumberGetter = cb::getValue;

					section.getChildren().add(cb);
				} else {
					String cardNumber = cardNumbers[0];
					cardNumberGetter = () -> cardNumber;

					section.getChildren().add(new MaskedLabel(".... .... .... .... .", cardNumber));
				}

				return section;
			}

			@Override
			protected void handleCancel() {
				this.close();
			}
		};
	}

	private ArgentaLoginStage() {
		this.client = new AsyncObservable<>(ArgentaClient::new);
		setTitle("Argenta");

		BorderPane content = new BorderPane();

		content.setCenter(initCenter());
		content.setBottom(initFooter());

		content.setPadding(new Insets(5));

		var scene = new Scene(content);
		setScene(scene);

		initModality(Modality.APPLICATION_MODAL);
		setResizable(false);
	}

	private Node initCenter() {

		var center = new VBox();
		center.setSpacing(15);

		var section1 = initCardNumberSection();

		var section2 = new VBox();
		section2.setSpacing(10);
		var h2 = heading("2. Steek je debetkaart in de digipas");
		digipas = new DigipasSection(client);
		digipas.setPadding(new Insets(0, 0, 0, 25));
		section2.getChildren().setAll(h2, digipas);

		center.getChildren().addAll(section1, section2);

		return center;
	}

	protected abstract VBox initCardNumberSection();

	protected abstract void handleCancel();

	private Node initFooter() {
		var footer = new HBox();
		footer.setSpacing(5);

		var cancel = new Button("Cancel");
		cancel.setOnMouseClicked(event -> {
			handleCancel();
		});
		var login = new Button("Loading");
		login.setDisable(true);
		client.whenReady((client) -> {
			Platform.runLater(() -> {
				login.setText("Login");
				login.setDisable(false);

				login.setOnMouseClicked((event) -> {
					var card = this.cardNumberGetter.get();
					var digiResponse = this.digipas.getResponse().getValue();

					client.login(card, digiResponse);
					if (afterLogin != null) {
						afterLogin.accept(client);
					}

				});
			});
		});

		footer.getChildren().addAll(cancel, login);

		footer.setAlignment(Pos.BOTTOM_RIGHT);

		footer.setPadding(new Insets(10, 0, 0, 0));

		return footer;
	}

	private static Label heading(String content) {
		var heading = new Label(content);
		heading.setStyle("-fx-font-weight:bold; -fx-font-size: 15px");

		return heading;
	}

	void afterLogin(Consumer<ArgentaClient> callback) {
		afterLogin = callback;
	}
}
