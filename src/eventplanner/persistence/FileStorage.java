package eventplanner.persistence;

import eventplanner.model.Event;
import eventplanner.model.EventManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Handles saving/loading events to/from a CSV file
public class FileStorage {
    private static final String DEFAULT_FILE = "data/events.txt";

    // Save all events to a file
    public static void saveEvents(EventManager manager, String filename) throws IOException {
        Path path = Paths.get(filename);
        if (path.getParent() != null && !Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Event e : manager.getEvents()) {
                writer.println(e.toCSV());
            }
        }
    }

    // Load events from a file
    public static void loadEvents(EventManager manager, String filename) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            manager.setEvents(new ArrayList<>());  // no file yet, start empty
            return;
        }

        List<Event> loaded = new ArrayList<>();   // FIXED: was EventListener -> now Event
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    loaded.add(Event.fromCSV(line));
                } catch (Exception ex) {
                    System.err.println("Skipping bad line: " + line);
                }
            }
        }
        manager.setEvents(loaded);
    }

    // Convenience methods using default file
    public static void saveEvents(EventManager manager) throws IOException {
        saveEvents(manager, DEFAULT_FILE);
    }

    public static void loadEvents(EventManager manager) throws IOException {
        loadEvents(manager, DEFAULT_FILE);
    }
}