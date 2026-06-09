package eventplanner.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Represents a single calendar event
public class Event {
    private String title;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String description;
    private String category;   // meeting, birthday, appointment, etc.
    private int reminderHours; // how many hours before the event to remind

    // Constructor
    public Event(String title, LocalDate date, LocalTime time, String location,
                 String description, String category, int reminderHours) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.category = category;
        this.reminderHours = reminderHours;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getReminderHours() { return reminderHours; }
    public void setReminderHours(int reminderHours) { this.reminderHours = reminderHours; }

    // For displaying in the event list
    @Override
    public String toString() {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        return date.toString() + " - " + time.format(timeFmt) + "  " + title + " [" + category + "]";
    }

    // Convert to CSV line for saving
    public String toCSV() {
        return String.join(";",
            title,
            date.toString(),
            time.toString(),
            location == null ? "" : location,
            description == null ? "" : description,
            category == null ? "" : category,
            String.valueOf(reminderHours)
        );
    }

    // Create an Event from a CSV line
    public static Event fromCSV(String line) {
        String[] parts = line.split(";");
        if (parts.length < 7) throw new IllegalArgumentException("Invalid CSV line");
        String t = parts[0];
        LocalDate d = LocalDate.parse(parts[1]);
        LocalTime tm = LocalTime.parse(parts[2]);
        String loc = parts[3].isEmpty() ? null : parts[3];
        String desc = parts[4].isEmpty() ? null : parts[4];
        String cat = parts[5].isEmpty() ? null : parts[5];
        int rem = Integer.parseInt(parts[6]);
        return new Event(t, d, tm, loc, desc, cat, rem);
    }
}