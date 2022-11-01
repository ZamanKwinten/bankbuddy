package bank.buddy.bank;

import java.util.function.Supplier;

import bank.buddy.gson.GenericGSON.AccountMeta;
import javafx.stage.Stage;

public interface IBank {

	BankLabel getLabel();

	Supplier<Stage> getLoginStage();

	ABankAccount construct(AccountMeta meta);

}
