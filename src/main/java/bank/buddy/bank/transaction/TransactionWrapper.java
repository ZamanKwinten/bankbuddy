package bank.buddy.bank.transaction;

import bank.buddy.bank.GlobalState;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class TransactionWrapper {

	private final ChangeListener<? super Category> listener = (obs, ov, nv) -> {
		updateObservables();
	};

	private final Transaction transaction;

	private final ObservableMap<String, SimpleObjectProperty<Category>> counterPartyNameMap;
	private final ObservableMap<String, SimpleObjectProperty<Category>> transactionIDMap;

	private SimpleObjectProperty<Category> category;

	private final SimpleObjectProperty<String> categoryName;
	private final SimpleObjectProperty<Paint> textFill;
	private final SimpleObjectProperty<Background> background;

	public TransactionWrapper(Transaction transaction) {
		this.transaction = transaction;

		this.counterPartyNameMap = transaction.amount() < 0 ? GlobalState.expenseCounterPartyNameMap
				: GlobalState.incomeCounterPartyNameMap;
		this.transactionIDMap = transaction.amount() < 0 ? GlobalState.expenseTransactionIDMap
				: GlobalState.incomeTransactionIDMap;

		this.category = getCategory();
		textFill = new SimpleObjectProperty<>(category.get().text());
		background = new SimpleObjectProperty<>(
				new Background(new BackgroundFill(category.get().background(), CornerRadii.EMPTY, Insets.EMPTY)));
		categoryName = new SimpleObjectProperty<>(category.get().name());

		counterPartyNameMap.addListener(initListener());
		transactionIDMap.addListener(initListener());

	}

	private MapChangeListener<String, SimpleObjectProperty<Category>> initListener() {
		return new MapChangeListener<>() {
			@Override
			public void onChanged(Change<? extends String, ? extends SimpleObjectProperty<Category>> change) {
				category.removeListener(listener);

				category = getCategory();
				updateObservables();
			}
		};
	}

	private void updateObservables() {
		categoryName.set(category.get().name());
		textFill.set(category.get().text());
		background
				.set(new Background(new BackgroundFill(category.get().background(), CornerRadii.EMPTY, Insets.EMPTY)));
	}

	private SimpleObjectProperty<Category> getCategory() {
		var result = transactionIDMap.get(transaction.id());
		if (result == null) {
			result = counterPartyNameMap.get(transaction.counterpartyName());
			if (result == null) {
				result = new SimpleObjectProperty<Category>(new Category("<Onbekend>", Color.BLACK, Color.LIGHTGREY));
			}
		}

		result.addListener(listener);
		return result;
	}

	public Transaction transaction() {
		return transaction;
	}

	public ObservableValue<String> categoryName() {
		return categoryName;
	}

	public ObservableValue<Paint> textFill() {
		return textFill;
	}

	public ObservableValue<Background> background() {
		return background;
	}

	public Category category() {
		return category.get();
	}
}
