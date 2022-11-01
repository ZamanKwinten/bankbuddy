package bank.buddy.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GSONProvider {

	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
}
