package eventplanner.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Manages a list of events and provides query methods (with recurrence support)
public class EventManager {
    private List<Event> events;   // stores only base events (recurring events stored once)

    public EventManager() {
        events = new ArrayList<>();
    }

    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }

    public void addEvent(Event e) { events.add(e); }
    public void removeEvent(Event e) { events.remove(e); }

    public void updateEvent(Event oldEvent, Event newEvent) {
        int idx = events.indexOf(oldEvent);
        if (idx != -1) events.set(idx, newEvent);
    }

    // Returns all events that occur on the given date, including recurring occurrences
    public List<Event> getEventsOnDate(LocalDate targetDate) {
        List<Event> result = new ArrayList<>();
        for (Event base : events) {
            if (!base.isRecurring()) {
                // non‑recurring event: check exact date
                if (base.getDate().equals(targetDate)) {
                    result.add(base);
                }
            } else {
                // recurring event: check if targetDate is a valid occurrence
                if (isRecurrenceMatch(base, targetDate)) {
                    // create a virtual occurrence that knows its base
                    Event occurrence = cloneEventForDate(base, targetDate);
                    result.add(occurrence);
                }
            }
        }
        return result;
    }

    // Helper: checks whether a recurring event occurs on a specific date
    private boolean isRecurrenceMatch(Event e, LocalDate target) {
        LocalDate start = e.getDate();
        if (target.isBefore(start)) return false;
        // If recurrence end date is set and target is after it, skip
        if (e.getRecurrenceEnd() != null && target.isAfter(e.getRecurrenceEnd())) {
            return false;
        }
        String type = e.getRecurrenceType();
        if (type == null) return false;
        switch (type) {
            case "daily":
                return true;
            case "weekly":
                long daysBetween = ChronoUnit.DAYS.between(start, target);
                return daysBetween % 7 == 0;
            case "monthly":
                return start.getDayOfMonth() == target.getDayOfMonth();
            default:
                return false;
        }
    }

    // Creates a copy of the event with a different date and links back to original
    private Event cloneEventForDate(Event original, LocalDate newDate) {
        Event clone = new Event(
            original.getTitle(), newDate, original.getTime(),
            original.getLocation(), original.getDescription(),
            original.getCategory(), original.getReminderHours(),
            false, null, null
        );
        clone.setOriginalEvent(original);   // important for deletion
        return clone;
    }

    // Search by keyword in title or description (base events only)
    public List<Event> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty())
            return new ArrayList<>();
        String kw = keyword.toLowerCase();
        return events.stream()
                .filter(e -> e.getTitle().toLowerCase().contains(kw) ||
                        (e.getDescription() != null && e.getDescription().toLowerCase().contains(kw)))
                .collect(Collectors.toList());
    }

    // Returns upcoming reminders for the next maxHours, considering recurrences
    public List<Event> getUpcomingReminders(int maxHours) {
        LocalDateTime now = LocalDateTime.now();
        List<Event> reminders = new ArrayList<>();
        for (Event base : events) {
            if (!base.isRecurring()) {
                // single event
                LocalDateTime eventTime = LocalDateTime.of(base.getDate(), base.getTime());
                long diff = ChronoUnit.HOURS.between(now, eventTime);
                if (diff >= 0 && diff <= base.getReminderHours() && diff <= maxHours) {
                    reminders.add(base);
                }
            } else {
                // recurring event: check occurrences up to one year ahead or recurrence end
                LocalDate start = base.getDate();
                LocalDate end = base.getRecurrenceEnd() != null ?
                                base.getRecurrenceEnd() : start.plusYears(1);
                LocalDate current = start;
                while (!current.isAfter(end)) {
                    if (isRecurrenceMatch(base, current)) {
                        LocalDateTime occDateTime = LocalDateTime.of(current, base.getTime());
                        long diff = ChronoUnit.HOURS.between(now, occDateTime);
                        if (diff >= 0 && diff <= base.getReminderHours() && diff <= maxHours) {
                            reminders.add(cloneEventForDate(base, current));
                        }
                    }
                    current = current.plusDays(1);
                }
            }
        }
        return reminders;
    }
}