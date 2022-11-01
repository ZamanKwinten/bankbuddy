package bank.buddy.bank.argenta;

import java.util.function.Consumer;
import java.util.function.Supplier;

import bank.buddy.bank.ABankAccount;
import bank.buddy.bank.BankEnum;
import bank.buddy.bank.BankLabel;
import bank.buddy.bank.IBank;
import bank.buddy.bank.transaction.TransactionCollection;
import bank.buddy.gson.GenericGSON.AccountMeta;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Argenta implements IBank {

	private static final String ARGENTA = "Argenta";

	@Override
	public BankLabel getLabel() {
		return new BankLabel(ARGENTA, this.getClass().getResourceAsStream("icon.png"));
	}

	@Override
	public Supplier<Stage> getLoginStage() {
		return () -> {
			var stage = ArgentaLoginStage.withUnknownCardNumber();

			stage.afterLogin(client -> {
				var selection = new ArgentaAccountSelectionStage(client);

				selection.show();
				stage.hide();
			});

			return stage;
		};
	}

	private static class BankAccount extends ABankAccount {

		public BankAccount(String[] cardNumbers, String accoutName, String iban, long lastModified) {
			super(cardNumbers, accoutName, iban, lastModified);
		}

		public BankAccount(AccountMeta meta) {
			super(meta);
		}

		@Override
		protected Image getBankIcon() {
			return new Image(this.getClass().getResourceAsStream("icon.png"));
		}

		@Override
		protected BankEnum getBank() {
			return BankEnum.Argenta;
		}

		@Override
		public void loadTransactionsFromRemoteImpl(Consumer<TransactionCollection> callback) {
			var stage = ArgentaLoginStage.withKnownCardNumber(this.cardNumbers());
			stage.afterLogin(client -> {
				callback.accept(client.getTransactions(this.iban()));

				stage.close();
			});

			stage.setAlwaysOnTop(true);
			stage.show();
		}
	}

	static ABankAccount convert(ArgentaAccount account) {
		return new BankAccount(new String[] { account.cardNumber() }, account.accountName(), account.iban(), 0L);
	}

	@Override
	public ABankAccount construct(AccountMeta meta) {
		return new BankAccount(meta);
	}
}
