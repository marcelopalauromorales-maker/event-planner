package eventplanner.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Holds all events and provides search/filter methods
public class EventManager {
    private List<Event> events;

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

    // Get events that occur on a specific date
    public List<Event> getEventsOnDate(java.time.LocalDate date) {
        return events.stream()
                .filter(e -> e.getDate().equals(date))
                .collect(Collectors.toList());
    }

    // Search by keyword in title or description
    public List<Event> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty())
            return new ArrayList<>();
        String kw = keyword.toLowerCase();
        return events.stream()
                .filter(e -> e.getTitle().toLowerCase().contains(kw) ||
                        (e.getDescription() != null && e.getDescription().toLowerCase().contains(kw)))
                .collect(Collectors.toList());
    }

    // Returns events that should trigger a reminder within the next 'maxHours'
    public List<Event> getUpcomingReminders(int maxHours) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<Event> result = new ArrayList<>();
        for (Event e : events) {
            java.time.LocalDateTime eventTime = java.time.LocalDateTime.of(e.getDate(), e.getTime());
            long diff = java.time.Duration.between(now, eventTime).toHours();
            if (diff >= 0 && diff <= e.getReminderHours() && diff <= maxHours) {
                result.add(e);
            }
        }
        return result;
    }
}