package eventplanner.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Represents a calendar event with optional recurrence
public class Event {
    private String title;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String description;
    private String category;
    private int reminderHours;          // hours before event to remind
    private boolean recurring;          // true if event repeats
    private String recurrenceType;      // "daily", "weekly", "monthly"
    private LocalDate recurrenceEnd;    // last occurrence date (null = forever)
    private transient Event originalEvent; // used for recurring occurrences to point back to base

    // Full constructor
    public Event(String title, LocalDate date, LocalTime time, String location,
                 String description, String category, int reminderHours,
                 boolean recurring, String recurrenceType, LocalDate recurrenceEnd) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.category = category;
        this.reminderHours = reminderHours;
        this.recurring = recurring;
        this.recurrenceType = recurrenceType;
        this.recurrenceEnd = recurrenceEnd;
        this.originalEvent = null;
    }

    // Simplified constructor for non‑recurring events (calls the full one)
    public Event(String title, LocalDate date, LocalTime time, String location,
                 String description, String category, int reminderHours) {
        this(title, date, time, location, description, category,
             reminderHours, false, null, null);
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

    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }

    public String getRecurrenceType() { return recurrenceType; }
    public void setRecurrenceType(String recurrenceType) { this.recurrenceType = recurrenceType; }

    public LocalDate getRecurrenceEnd() { return recurrenceEnd; }
    public void setRecurrenceEnd(LocalDate recurrenceEnd) { this.recurrenceEnd = recurrenceEnd; }

    public Event getOriginalEvent() { return originalEvent; }
    public void setOriginalEvent(Event original) { this.originalEvent = original; }

    // For displaying in the event list
    @Override
    public String toString() {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        String base = date.toString() + " - " + time.format(timeFmt) + "  " + title + " [" + category + "]";
        if (recurring) {
            base += " (repeats " + recurrenceType + ")";
        }
        return base;
    }

    // Convert to CSV line (10 fields separated by semicolon)
    public String toCSV() {
        return String.join(";",
            title,
            date.toString(),
            time.toString(),
            location == null ? "" : location,
            description == null ? "" : description,
            category == null ? "" : category,
            String.valueOf(reminderHours),
            String.valueOf(recurring),
            recurrenceType == null ? "" : recurrenceType,
            recurrenceEnd == null ? "" : recurrenceEnd.toString()
        );
    }

    // Create an Event from a CSV line (handles old format without recurrence too)
    public static Event fromCSV(String line) {
        String[] parts = line.split(";");
        // If the line has only 7 fields, it's from older version without recurrence
        if (parts.length == 7) {
            String title = parts[0];
            LocalDate date = LocalDate.parse(parts[1]);
            LocalTime time = LocalTime.parse(parts[2]);
            String loc = parts[3].isEmpty() ? null : parts[3];
            String desc = parts[4].isEmpty() ? null : parts[4];
            String cat = parts[5].isEmpty() ? null : parts[5];
            int rem = Integer.parseInt(parts[6]);
            return new Event(title, date, time, loc, desc, cat, rem);
        }
        // New format with recurrence fields
        if (parts.length >= 10) {
            String title = parts[0];
            LocalDate date = LocalDate.parse(parts[1]);
            LocalTime time = LocalTime.parse(parts[2]);
            String location = parts[3].isEmpty() ? null : parts[3];
            String description = parts[4].isEmpty() ? null : parts[4];
            String category = parts[5].isEmpty() ? null : parts[5];
            int reminderHours = Integer.parseInt(parts[6]);
            boolean recurring = Boolean.parseBoolean(parts[7]);
            String recurrenceType = parts[8].isEmpty() ? null : parts[8];
            LocalDate recurrenceEnd = parts[9].isEmpty() ? null : LocalDate.parse(parts[9]);
            return new Event(title, date, time, location, description, category,
                             reminderHours, recurring, recurrenceType, recurrenceEnd);
        }
        throw new IllegalArgumentException("Invalid CSV line: " + line);
    }
}