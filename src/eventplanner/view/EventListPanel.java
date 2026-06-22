// Marcelo Palauro Morales	14594034
// Rodrigo Eduardo Rubiano	16311091
// Rapha Mendes	

package eventplanner.view;

import eventplanner.model.Event;
import eventplanner.model.EventManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Panel that shows the list of events for the currently selected day
public class EventListPanel extends JPanel {
    private EventManager eventManager;
    private MainFrame parent;
    private DefaultListModel<Event> listModel;
    private JList<Event> eventList;

    public EventListPanel(EventManager eventManager, MainFrame parent) {
        this.eventManager = eventManager;
        this.parent = parent;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Events for selected day"));

        listModel = new DefaultListModel<>();
        eventList = new JList<>(listModel);
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(eventList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton newButton = new JButton("New");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        // Tooltips for buttons
        newButton.setToolTipText("Create a new event (date preset to selected day)");
        editButton.setToolTipText("Edit the selected event");
        deleteButton.setToolTipText("Delete the selected event (whole series if recurring)");

        buttonPanel.add(newButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // New action: open dialog with the selected calendar date pre-filled
        newButton.addActionListener(e -> {
            parent.openEventDialogWithDate(parent.getCalendarPanel().getSelectedDate());
        });

        // Edit action: open dialog with the selected event
        editButton.addActionListener(e -> {
            Event selected = eventList.getSelectedValue();
            if (selected != null) {
                parent.openEventDialog(selected);
            } else {
                JOptionPane.showMessageDialog(this, "No event selected",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Delete action: remove event (if occurrence, remove base event)
        deleteButton.addActionListener(e -> {
            Event selected = eventList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete \"" + selected.getTitle() + "\"?",
                        "Confirm deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Event toRemove = selected;
                    // If this is a recurring occurrence (clone), get the original base event
                    if (selected.getOriginalEvent() != null) {
                        toRemove = selected.getOriginalEvent();
                    }
                    // If it's a recurring base event, ask for confirmation to delete whole series
                    if (toRemove.isRecurring()) {
                        int delSeries = JOptionPane.showConfirmDialog(this,
                                "This is a recurring event. Delete all occurrences?",
                                "Delete series", JOptionPane.YES_NO_OPTION);
                        if (delSeries != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    eventManager.removeEvent(toRemove);
                    // Refresh display
                    showEvents(eventManager.getEventsOnDate(parent.getCalendarPanel().getSelectedDate()));
                    parent.refreshAll();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No event selected",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // Updates the list with a new collection of events
    public void showEvents(List<Event> events) {
        listModel.clear();
        if (events.isEmpty()) {
            eventList.setEnabled(false);
        } else {
            for (Event e : events) {
                listModel.addElement(e);
            }
            eventList.setEnabled(true);
        }
    }
}