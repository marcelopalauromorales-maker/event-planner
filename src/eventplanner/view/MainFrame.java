package eventplanner.view;

import eventplanner.model.EventManager;
import eventplanner.persistence.FileStorage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

// Main window with calendar and event list
public class MainFrame extends JFrame {
    private EventManager eventManager;
    private CalendarPanel calendarPanel;
    private EventListPanel eventListPanel;

    public MainFrame() {
        setTitle("Java Event Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        // Load existing events from file
        eventManager = new EventManager();
        try {
            FileStorage.loadEvents(eventManager);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not load events:\n" + e.getMessage(),
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }

        // Create the two main panels
        calendarPanel = new CalendarPanel(eventManager, this);
        eventListPanel = new EventListPanel(eventManager, this);

        // Put them side by side with a split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, calendarPanel, eventListPanel);
        split.setResizeWeight(0.6);
        add(split, BorderLayout.CENTER);

        // Top search bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText();
            if (kw != null && !kw.trim().isEmpty()) {
                var results = eventManager.searchByKeyword(kw);
                if (results.isEmpty())
                    JOptionPane.showMessageDialog(this, "No events found for: " + kw);
                else
                    eventListPanel.showEvents(results);
            } else {
                // show events of selected date
                eventListPanel.showEvents(eventManager.getEventsOnDate(calendarPanel.getSelectedDate()));
            }
        });
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        add(topPanel, BorderLayout.NORTH);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newEventItem = new JMenuItem("New Event");
        newEventItem.addActionListener(e -> openEventDialog(null));
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> { saveAndExit(); });
        fileMenu.add(newEventItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Event Planner - Compact Version\nFor SCC0504", "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Show reminders on startup
        showRemindersAtStart();
    }

    // Called by other components to refresh view after changes
    public void refreshAll() {
        calendarPanel.refreshCalendar();
        eventListPanel.showEvents(eventManager.getEventsOnDate(calendarPanel.getSelectedDate()));
        autoSave();
    }

    private void autoSave() {
        try {
            FileStorage.saveEvents(eventManager);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAndExit() {
        autoSave();
        System.exit(0);
    }

    public void openEventDialog(eventplanner.model.Event oldEvent) {
        EventDialog dlg = new EventDialog(this, eventManager, oldEvent, calendarPanel.getSelectedDate());
        dlg.setVisible(true);
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
