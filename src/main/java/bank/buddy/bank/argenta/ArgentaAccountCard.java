package bank.buddy.bank.argenta;

import bank.buddy.ui.MaskedLabel;
import bank.buddy.ui.ValutaLabel;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

class ArgentaAccountCard extends GridPane {

	public ArgentaAccountCard(ArgentaAccount account) {
		setHgap(5);

		var icon = new ImageView(new Image(
				this.getClass().getResourceAsStream(account.isSaving() ? "savingsaccount.png" : "normalaccount.png")));

		this.add(icon, 0, 0, 1, 2);

		var name = new Label(account.accountName());

		name.setFont(new Font(25));
		this.add(name, 1, 0, 2, 1);

		var iban = new MaskedLabel(".... .... .... ....", account.iban());

		this.add(iban, 1, 1);

		var balance = new ValutaLabel(account.balance());
		this.add(balance, 2, 1);
		balance.setStyle(
				"-fx-background-color: rgba(0,74,101,.07); -fx-background-radius: 15px; -fx-text-fill: #004a65;");
		GridPane.setHalignment(balance, HPos.RIGHT);

		setStyle("-fx-background-color: rgba(0,74,101,.07); -fx-background-radius: 5px; -fx-text-fill: #004a65;");

		ColumnConstraints cc = new ColumnConstraints();
		cc.setHgrow(Priority.ALWAYS);
		getColumnConstraints().addAll(new ColumnConstraints(), cc);

	}
}
