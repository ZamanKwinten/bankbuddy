package bank.buddy.util;

import java.text.NumberFormat;
import java.util.Locale;

public class ValutaUtil {
	public static String toValuta(double valuta) {
		return NumberFormat.getCurrencyInstance(new Locale("nl", "BE")).format(valuta);
	}

	private ValutaUtil() {

	}
}
