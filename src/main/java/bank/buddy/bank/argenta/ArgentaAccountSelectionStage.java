package bank.buddy.bank.argenta;

import java.util.stream.Collectors;

import bank.buddy.bank.GlobalState;
import bank.buddy.file.FileMgr;
import bank.buddy.ui.AsyncObservable;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

class ArgentaAccountSelectionStage extends Stage {

	public ArgentaAccountSelectionStage(ArgentaClient client) {

		var root = new BorderPane();
		root.setPadding(new Insets(5));

		var heading = new Label("Mijn Rekeningen");
		heading.setFont(new Font(25));
		heading.setPadding(new Insets(0, 0, 15, 0));
		root.setTop(heading);

		var content = new VBox();

		content.setSpacing(10);

		var obs = new AsyncObservable<>(client::getAccounts);
		obs.whenReady(accounts -> {

			Platform.runLater(() -> {

				content.getChildren().setAll(accounts.stream().map(account -> {
					var card = new ArgentaAccountCard(account);

					card.setCursor(Cursor.HAND);
					card.setOnMouseClicked((event) -> {

						FileMgr.get().storeAccountTransactions(account.iban(), client.getTransactions(account.iban()));

						var acc = GlobalState.myAccounts.get(account.iban());
						if (acc == null) {
							GlobalState.myAccounts.put(account.iban(), Argenta.convert(account));
						} else {
							acc.addCardNumber(account.cardNumber());
							GlobalState.myAccounts.put(account.iban(), acc);
						}

						this.hide();
					});

					return card;
				}).collect(Collectors.toList()));

				getScene().getWindow().sizeToScene();
			});
		});

		var loading = new Label("Bezig met Laden ...");

		content.getChildren().setAll(loading);
		root.setCenter(content);

		setTitle("Bankrekening selecteren");
		setResizable(false);

		initModality(Modality.APPLICATION_MODAL);
		setScene(new Scene(root));
	}
}
