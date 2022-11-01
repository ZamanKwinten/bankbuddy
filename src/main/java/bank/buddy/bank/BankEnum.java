package bank.buddy.bank;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import bank.buddy.bank.argenta.Argenta;
import bank.buddy.gson.GenericGSON;
import javafx.stage.Stage;

public enum BankEnum {
	Argenta(0, new Argenta());

	private static Map<Integer, BankEnum> map = new HashMap<>();
	static {
		for (var e : BankEnum.values()) {
			map.put(e.id, e);
		}
	}

	public final int id;
	private final IBank iBank;

	private BankEnum(int id, IBank iBank) {
		this.id = id;
		this.iBank = iBank;
	}

	public BankLabel getLabel() {
		return iBank.getLabel();
	}

	public Stage getLoginStage() {
		return iBank.getLoginStage().get();
	}

	public static Optional<ABankAccount> convert(GenericGSON.AccountMeta meta) {
		var e = map.get(meta.bankID);
		if (e == null) {
			return Optional.empty();
		}

		return Optional.of(e.iBank.construct(meta));
	}
}
