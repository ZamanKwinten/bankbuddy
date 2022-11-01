package bank.buddy.bank;

import bank.buddy.bank.transaction.Category;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Contains globally accessible lists and observables
 * 
 * @author KWZA
 *
 */
public class GlobalState {

	private GlobalState() {

	}

	public static final ObservableMap<String, ABankAccount> myAccounts = FXCollections.observableHashMap();

	public static final ObservableList<SimpleObjectProperty<Category>> incomeCategories = FXCollections
			.observableArrayList();

	public static final ObservableList<SimpleObjectProperty<Category>> expenseCategories = FXCollections
			.observableArrayList();

	public static final ObservableMap<String, SimpleObjectProperty<Category>> incomeTransactionIDMap = FXCollections
			.observableHashMap();

	public static final ObservableMap<String, SimpleObjectProperty<Category>> incomeCounterPartyNameMap = FXCollections
			.observableHashMap();

	public static final ObservableMap<String, SimpleObjectProperty<Category>> expenseTransactionIDMap = FXCollections
			.observableHashMap();

	public static final ObservableMap<String, SimpleObjectProperty<Category>> expenseCounterPartyNameMap = FXCollections
			.observableHashMap();
}