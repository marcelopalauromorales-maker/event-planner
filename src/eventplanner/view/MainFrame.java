package eventplanner.view;

import eventplanner.model.Event;
import eventplanner.model.EventManager;
import eventplanner.persistence.FileStorage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

// Main application window with calendar and event list
public class MainFrame extends JFrame {
    private EventManager eventManager;
    private CalendarPanel calendarPanel;
    private EventListPanel eventListPanel;

    public MainFrame() {
        setTitle("Java Event Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Load existing events from file
        eventManager = new EventManager();
        try {
            FileStorage.loadEvents(eventManager);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Could not load events: " + e.getMessage(),
                "Warning", JOptionPane.WARNING_MESSAGE);
        }

        // Create the two main panels
        calendarPanel = new CalendarPanel(eventManager, this);
        eventListPanel = new EventListPanel(eventManager, this);

        // Side by side with a split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          calendarPanel, eventListPanel);
        split.setResizeWeight(0.6);
        add(split, BorderLayout.CENTER);

        // Top search bar with tooltip
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        searchBtn.setToolTipText("Search events by title or description");
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText();
            if (kw != null && !kw.trim().isEmpty()) {
                var results = eventManager.searchByKeyword(kw);
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "No events found for: " + kw,
                        "Search", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    eventListPanel.showEvents(results);
                }
            } else {
                // Show events of selected date
                eventListPanel.showEvents(
                    eventManager.getEventsOnDate(calendarPanel.getSelectedDate()));
            }
        });
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        add(topPanel, BorderLayout.NORTH);

        // Menu bar
        createMenuBar();

        // Show reminders on startup
        showRemindersAtStart();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newEventItem = new JMenuItem("New Event");
        newEventItem.setToolTipText("Create a new event");
        newEventItem.addActionListener(e -> openEventDialog(null));
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setToolTipText("Save and exit the application");
        exitItem.addActionListener(e -> saveAndExit());

        fileMenu.add(newEventItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setToolTipText("Information about the application");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Java Event Planner - Compact Version\nFor SCC0504\n\nFeatures:\n" +
            "- Monthly calendar with event highlights\n" +
            "- Create/edit/delete events\n" +
            "- Recurring events (daily/weekly/monthly)\n" +
            "- Category colors\n" +
            "- Search by keyword\n" +
            "- Startup reminders",
            "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    // Called by other components to refresh view after changes
    public void refreshAll() {
        calendarPanel.refreshCalendar();          // redraw calendar with colors
        eventListPanel.showEvents(
            eventManager.getEventsOnDate(calendarPanel.getSelectedDate()));
        autoSave();
    }

    private void autoSave() {
        try {
            FileStorage.saveEvents(eventManager);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error saving events: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAndExit() {
        autoSave();
        System.exit(0);
    }

    // Opens the event dialog for creating or editing an event
    public void openEventDialog(Event oldEvent) {
        EventDialog dialog = new EventDialog(this, eventManager, oldEvent);
        dialog.setVisible(true);
        refreshAll();
    }

    // Opens the event dialog with a pre-filled date (used when "New" button is clicked)
    public void openEventDialogWithDate(LocalDate date) {
        Event dummy = new Event("", date, LocalTime.now(), null, null, "Meeting", 24);
        EventDialog dialog = new EventDialog(this, eventManager, null, dummy);
        dialog.setVisible(true);
        refreshAll();
    }

    private void showRemindersAtStart() {
        var reminders = eventManager.getUpcomingReminders(24);
        if (!reminders.isEmpty()) {
            ReminderDialog.showReminders(this, reminders);
        }
    }

    public CalendarPanel getCalendarPanel() { return calendarPanel; }
    public EventManager getEventManager() { return eventManager; }
}