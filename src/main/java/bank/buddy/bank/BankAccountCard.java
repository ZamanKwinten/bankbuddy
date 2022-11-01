package bank.buddy.bank;

import bank.buddy.ui.MaskedLabel;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class BankAccountCard extends GridPane {

	public BankAccountCard(ABankAccount item) {

		setHgap(10);

		var icon = new ImageView(item.getBankIcon());
		icon.setFitHeight(50);
		icon.setFitWidth(50);
		add(icon, 0, 0, 1, 2);

		var name = new Label(item.accountName());
		name.setFont(new Font(20));

		add(name, 1, 0);

		var iban = new MaskedLabel(".... .... .... ....", item.iban());
		add(iban, 1, 1);
	}

}
