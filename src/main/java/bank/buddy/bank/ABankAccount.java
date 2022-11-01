package bank.buddy.bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import bank.buddy.bank.transaction.TransactionCollection;
import bank.buddy.file.FileMgr;
import bank.buddy.gson.GenericGSON;
import bank.buddy.gson.GenericGSON.AccountMeta;
import javafx.scene.image.Image;

public abstract class ABankAccount {

	private final List<String> cardNumbers;
	private final String accountName;
	private final String iban;
	private final long lastModified;

	public ABankAccount(GenericGSON.AccountMeta meta) {
		this(meta.cardNumber, meta.name, meta.iban, meta.lastUpdated);
	}

	public ABankAccount(String[] cardNumbers, String accoutName, String iban, long lastModified) {
		this.cardNumbers = new ArrayList<>(Arrays.asList(cardNumbers));
		this.accountName = accoutName;
		this.iban = iban;
		this.lastModified = lastModified;
	}

	protected final String accountName() {
		return accountName;
	}

	public final String iban() {
		return iban;
	}

	public void addCardNumber(String cardNumber) {
		cardNumbers.add(cardNumber);
	}

	public GenericGSON.AccountMeta toMeta() {
		var json = new AccountMeta();
		json.bankID = getBank().id;
		json.cardNumber = cardNumbers.toArray(new String[0]);
		json.name = accountName;
		json.iban = iban;
		json.lastUpdated = lastModified;

		return json;
	}

	protected abstract Image getBankIcon();

	protected abstract BankEnum getBank();

	public final void loadTransactionsFromRemote(Consumer<TransactionCollection> callback) {
		loadTransactionsFromRemoteImpl((transactioncoll) -> {

			FileMgr.get().storeAccountTransactions(iban, transactioncoll);

			callback.accept(transactioncoll);
		});
	}

	public abstract void loadTransactionsFromRemoteImpl(Consumer<TransactionCollection> callback);

	public String[] cardNumbers() {
		return cardNumbers.toArray(new String[0]);
	}
}
