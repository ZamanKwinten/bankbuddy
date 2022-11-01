package bank.buddy.file;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import bank.buddy.bank.ABankAccount;
import bank.buddy.bank.BankEnum;
import bank.buddy.bank.GlobalState;
import bank.buddy.bank.transaction.TransactionCollection;
import bank.buddy.gson.GenericGSON;
import javafx.collections.MapChangeListener;

public class AccountFileMgr {

	private final File accountDir;

	public AccountFileMgr(File rootDir) {
		accountDir = new File(rootDir, "accounts");
		accountDir.mkdirs();

		var accountingMeta = new File(accountDir, "meta");
		if (accountingMeta.exists()) {
			Arrays.stream(FileMgr.read(accountingMeta, GenericGSON.AccountMeta[].class)).map(BankEnum::convert)
					.flatMap(Optional::stream).forEach(acc -> GlobalState.myAccounts.put(acc.iban(), acc));
		}
		GlobalState.myAccounts.addListener(myAccountsListener(accountingMeta));
	}

	private MapChangeListener<String, ABankAccount> myAccountsListener(File file) {
		return new MapChangeListener<>() {
			@Override
			public void onChanged(Change<? extends String, ? extends ABankAccount> change) {
				var metaData = GlobalState.myAccounts.values().stream().map(ABankAccount::toMeta)
						.toArray(GenericGSON.AccountMeta[]::new);

				FileMgr.write(file, metaData);
			}
		};
	}

	void storeAccountTransactions(String iban, TransactionCollection transactioncoll) {
		var accountFile = new File(accountDir, iban);

		var data = transactioncoll.getAllTransactions().map(GenericGSON.Transaction::new)
				.toArray(GenericGSON.Transaction[]::new);

		FileMgr.write(accountFile, data);
	}

	Optional<TransactionCollection> readAccountTransactions(String iban) {
		var accountFile = new File(accountDir, iban);

		if (!accountFile.exists()) {
			return Optional.empty();
		}

		return Optional.of(new TransactionCollection(Arrays
				.stream(FileMgr.read(accountFile, GenericGSON.Transaction[].class)).map(GenericGSON.Transaction::to)));
	}
}
