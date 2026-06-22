// Marcelo Palauro Morales	14594034
// Rodrigo Eduardo Rubiano	16311091
// Rapha Mendes	

package eventplanner.persistence;

import eventplanner.model.Event;
import eventplanner.model.EventManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Handles saving and loading events to/from a CSV file
public class FileStorage {
    private static final String DEFAULT_FILE = "data/events.txt";

    // Save all events from the manager into a file
    public static void saveEvents(EventManager manager, String filename) throws IOException {
        Path path = Paths.get(filename);
        // Create parent directory if it doesn't exist
        if (path.getParent() != null && !Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Event e : manager.getEvents()) {
                writer.println(e.toCSV());
            }
        }
    }

    // Load events from a file into the manager (replaces existing list)
    public static void loadEvents(EventManager manager, String filename) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            // No file yet, start with empty list
            manager.setEvents(new ArrayList<>());
            return;
        }

        List<Event> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    // Event.fromCSV handles both old (7 fields) and new (10 fields) format
                    loaded.add(Event.fromCSV(line));
                } catch (Exception ex) {
                    // Skip bad lines but print a warning
                    System.err.println("Skipping invalid line: " + line);
                }
            }
        }
        manager.setEvents(loaded);
    }

    // Convenience methods using the default file path
    public static void saveEvents(EventManager manager) throws IOException {
        saveEvents(manager, DEFAULT_FILE);
    }

    public static void loadEvents(EventManager manager) throws IOException {
        loadEvents(manager, DEFAULT_FILE);
    }
}