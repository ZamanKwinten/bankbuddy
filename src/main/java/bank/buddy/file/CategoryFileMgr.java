package bank.buddy.file;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import bank.buddy.bank.GlobalState;
import bank.buddy.bank.transaction.Category;
import bank.buddy.gson.GenericGSON;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

class CategoryFileMgr {

	private static class CategoryListener implements ListChangeListener<SimpleObjectProperty<Category>> {

		private final ObservableList<SimpleObjectProperty<Category>> list;
		private final File file;

		private final ChangeListener<Category> listener;

		CategoryListener(ObservableList<SimpleObjectProperty<Category>> list, File file) {
			this.list = list;
			this.file = file;

			this.listener = (obs, ov, nv) -> {
				writeCategoryData();
			};
		}

		@Override
		public void onChanged(Change<? extends SimpleObjectProperty<Category>> c) {
			c.getList().forEach(x -> {
				x.removeListener(listener);
				x.addListener(listener);
			});
			writeCategoryData();
		}

		private void writeCategoryData() {
			var data = list.stream().map(x -> new GenericGSON.Category(x.get())).toArray(GenericGSON.Category[]::new);
			FileMgr.write(file, data);
		}

	}

	private final File categoryDir;
	private final File transCatDir;

	public CategoryFileMgr(File rootDir) {
		categoryDir = new File(rootDir, "category");
		categoryDir.mkdirs();

		initCategory(GlobalState.incomeCategories, "income_categories");
		initCategory(GlobalState.expenseCategories, "expense_categories");

		transCatDir = new File(rootDir, "trans_cat");
		transCatDir.mkdirs();

		initMap(GlobalState.incomeCategories, GlobalState.incomeTransactionIDMap, GlobalState.incomeCounterPartyNameMap,
				"income_map");
		initMap(GlobalState.expenseCategories, GlobalState.expenseTransactionIDMap,
				GlobalState.expenseCounterPartyNameMap, "expense_map");
	}

	private void initCategory(ObservableList<SimpleObjectProperty<Category>> list, String name) {
		var file = new File(categoryDir, name);
		if (file.exists()) {
			list.addAll(Arrays.stream(FileMgr.read(file, GenericGSON.Category[].class))
					.map(x -> new SimpleObjectProperty<>(x.to())).collect(Collectors.toList()));
		}
		var listener = new CategoryListener(list, file);
		list.addListener(listener);
		list.forEach(cat -> {
			cat.removeListener(listener.listener);
			cat.addListener(listener.listener);
		});
	}

	private void initMap(ObservableList<SimpleObjectProperty<Category>> list,
			ObservableMap<String, SimpleObjectProperty<Category>> transactionIDMap,
			ObservableMap<String, SimpleObjectProperty<Category>> counterpartyNameMap, String name) {

		var categoryMap = list.stream().collect(Collectors.toMap(v -> v.get().name(), v -> v));

		var file = new File(transCatDir, name);
		if (file.exists()) {
			var map = FileMgr.read(file, GenericGSON.TransactionCategoryMap.class);

			for (var entry : map.counterPartyName) {
				counterpartyNameMap.put(entry.key, categoryMap.get(entry.value));
			}
			for (var entry : map.transactionID) {
				transactionIDMap.put(entry.key, categoryMap.get(entry.value));
			}
		}

		var listener = new MapChangeListener<String, SimpleObjectProperty<Category>>() {
			@Override
			public void onChanged(Change<? extends String, ? extends SimpleObjectProperty<Category>> change) {
				var transactionID = transactionIDMap.entrySet().stream().map(GenericGSON.TransactionCategoryEntry::new)
						.toArray(GenericGSON.TransactionCategoryEntry[]::new);
				var counterPartyName = counterpartyNameMap.entrySet().stream()
						.map(GenericGSON.TransactionCategoryEntry::new)
						.toArray(GenericGSON.TransactionCategoryEntry[]::new);

				var data = new GenericGSON.TransactionCategoryMap(counterPartyName, transactionID);

				FileMgr.write(file, data);
			}
		};

		transactionIDMap.addListener(listener);
		counterpartyNameMap.addListener(listener);
	}
}
