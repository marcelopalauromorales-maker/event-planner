package eventplanner.view;

import eventplanner.model.Event;
import eventplanner.model.EventManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Panel that shows a list of events for the selected day (or search results)
public class EventListPanel extends JPanel {
    private EventManager eventManager;
    private MainFrame parent;
    private DefaultListModel<Event> listModel;
    private JList<Event> eventList;

    public EventListPanel(EventManager eventManager, MainFrame parent) {
        this.eventManager = eventManager;
        this.parent = parent;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Events"));

        listModel = new DefaultListModel<>();
        eventList = new JList<>(listModel);
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(eventList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Edit button action
        editButton.addActionListener(e -> {
            Event selected = eventList.getSelectedValue();
            if (selected != null) {
                parent.openEventDialog(selected);   // calls MainFrame method
            }
        });

        // Delete button action
        deleteButton.addActionListener(e -> {
            Event selected = eventList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete \"" + selected.getTitle() + "\"?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    eventManager.removeEvent(selected);
                    // Refresh to show current day's events again
                    showEvents(eventManager.getEventsOnDate(parent.getCalendarPanel().getSelectedDate()));
                    parent.refreshAll();  // saves and updates calendar highlight
                }
            }
        });
    }

    // Update the list with a new collection of events
    public void showEvents(List<Event> events) {
        listModel.clear();
        for (Event e : events) {
            listModel.addElement(e);
        }
        if (listModel.isEmpty()) {
            eventList.setEnabled(false);
        } else {
            eventList.setEnabled(true);
        }
    }
}