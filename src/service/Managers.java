package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import adapter.InstantAdapter;

import java.io.IOException;
import java.time.Instant;

public class Managers {

	public static HttpTaskManager getDefaultTaskManager() throws IOException, InterruptedException {
		return new HttpTaskManager();
	}

	public static HistoryManager getDefaultHistoryManager() {
		return new InMemoryHistoryManager();
	}

	public static Gson getGson() {
		return new GsonBuilder()
				.registerTypeAdapter(Instant.class, new InstantAdapter())
				.create();
	}
}
