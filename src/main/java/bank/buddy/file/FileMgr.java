package bank.buddy.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import bank.buddy.bank.transaction.TransactionCollection;
import bank.buddy.gson.GSONProvider;

/**
 * The manager of the local file cache
 * 
 * @author KWZA
 *
 */
public class FileMgr {

	private static final FileMgr INSTANCE = new FileMgr();

	public static FileMgr get() {
		return INSTANCE;
	}

	private final File bankbuddyRootDir;

	private final AccountFileMgr accountMgr;
	@SuppressWarnings("unused") // construction of this object registers some listeners responsible for
								// writing/updating files
	private final CategoryFileMgr categoryMgr;

	private FileMgr() {
		this.bankbuddyRootDir = new File(System.getProperty("user.home"), ".bankbuddy");
		bankbuddyRootDir.mkdirs();

		this.accountMgr = new AccountFileMgr(bankbuddyRootDir);
		this.categoryMgr = new CategoryFileMgr(bankbuddyRootDir);

	}

	public void storeAccountTransactions(String iban, TransactionCollection transactioncoll) {
		accountMgr.storeAccountTransactions(iban, transactioncoll);
	}

	public Optional<TransactionCollection> readAccountTransactions(String iban) {
		return accountMgr.readAccountTransactions(iban);
	}

	static void write(File f, Object o) {
		var json = GSONProvider.gson.toJson(o);

		try {
			Files.writeString(f.toPath(), json, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static <T> T read(File f, Class<T> clazz) {

		try {
			var json = Files.readString(f.toPath());
			return GSONProvider.gson.fromJson(json, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
