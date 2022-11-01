package bank.buddy.ui;

import bank.buddy.util.ValutaUtil;
import javafx.scene.control.Label;

public class ValutaLabel extends Label {

	public ValutaLabel(double valuta) {
		super(ValutaUtil.toValuta(valuta));
	}
}
