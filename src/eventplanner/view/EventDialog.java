// Marcelo Palauro Morales	14594034
// Rodrigo Eduardo Rubiano	16311091
// Rapha Mendes	

package eventplanner.view;

import eventplanner.model.Event;
import eventplanner.model.EventManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

// Dialog for creating or editing an event (includes recurrence options)
public class EventDialog extends JDialog {
    private EventManager eventManager;
    private Event oldEvent; // null if creating new
    private JTextField titleField;
    private JTextField dateField;   // yyyy-MM-dd
    private JTextField timeField;   // HH:mm
    private JTextField locationField;
    private JTextArea descArea;
    private JComboBox<String> categoryCombo;
    private JSpinner reminderSpinner;
    // Recurrence components
    private JCheckBox recurringCheck;
    private JComboBox<String> recurrenceTypeCombo;
    private JTextField recurrenceEndField; // optional end date

    // Standard constructor
    public EventDialog(JFrame parent, EventManager eventManager, Event oldEvent) {
        super(parent, oldEvent == null ? "New Event" : "Edit Event", true);
        this.eventManager = eventManager;
        this.oldEvent = oldEvent;
        setSize(450, 580);
        setLocationRelativeTo(parent);
        initUI();
        if (oldEvent != null) loadData();
    }

    // Constructor that pre-fills fields from a given event (used for new event with suggested date)
    public EventDialog(JFrame parent, EventManager eventManager, Event oldEvent, Event prefill) {
        this(parent, eventManager, oldEvent);
        if (prefill != null && oldEvent == null) {
            titleField.setText(prefill.getTitle());
            dateField.setText(prefill.getDate().toString());
            timeField.setText(prefill.getTime().toString());
            locationField.setText(prefill.getLocation() == null ? "" : prefill.getLocation());
            descArea.setText(prefill.getDescription() == null ? "" : prefill.getDescription());
            if (prefill.getCategory() != null) categoryCombo.setSelectedItem(prefill.getCategory());
            reminderSpinner.setValue(prefill.getReminderHours());
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Title:*"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        form.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Date (yyyy-mm-dd):*"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(LocalDate.now().toString());
        form.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Time (HH:mm):*"), gbc);
        gbc.gridx = 1;
        timeField = new JTextField("18:00");
        form.add(timeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        locationField = new JTextField(20);
        form.add(locationField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descArea = new JTextArea(3, 20);
        form.add(new JScrollPane(descArea), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        String[] cats = {"Meeting", "Birthday", "Appointment", "Study", "Leisure", "Other"};
        categoryCombo = new JComboBox<>(cats);
        form.add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Reminder (hours before):"), gbc);
        gbc.gridx = 1;
        reminderSpinner = new JSpinner(new SpinnerNumberModel(24, 0, 168, 1));
        form.add(reminderSpinner, gbc);

        // Recurrence section
        gbc.gridx = 0;
        gbc.gridy++;
        recurringCheck = new JCheckBox("Recurring event");
        gbc.gridwidth = 2;
        form.add(recurringCheck, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Recurrence type:"), gbc);
        gbc.gridx = 1;
        String[] types = {"daily", "weekly", "monthly"};
        recurrenceTypeCombo = new JComboBox<>(types);
        recurrenceTypeCombo.setEnabled(false);
        form.add(recurrenceTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Recurrence end (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1;
        recurrenceEndField = new JTextField(15);
        recurrenceEndField.setEnabled(false);
        recurrenceEndField.setToolTipText("Leave empty for no end date");
        form.add(recurrenceEndField, gbc);

        // Enable/disable recurrence fields based on checkbox
        recurringCheck.addActionListener(e -> {
            boolean selected = recurringCheck.isSelected();
            recurrenceTypeCombo.setEnabled(selected);
            recurrenceEndField.setEnabled(selected);
        });

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        buttons.add(saveBtn);
        buttons.add(cancelBtn);
        add(buttons, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> saveEvent());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void loadData() {
        titleField.setText(oldEvent.getTitle());
        dateField.setText(oldEvent.getDate().toString());
        timeField.setText(oldEvent.getTime().toString());
        locationField.setText(oldEvent.getLocation() == null ? "" : oldEvent.getLocation());
        descArea.setText(oldEvent.getDescription() == null ? "" : oldEvent.getDescription());
        if (oldEvent.getCategory() != null) categoryCombo.setSelectedItem(oldEvent.getCategory());
        reminderSpinner.setValue(oldEvent.getReminderHours());

        // Recurrence data
        if (oldEvent.isRecurring()) {
            recurringCheck.setSelected(true);
            recurrenceTypeCombo.setEnabled(true);
            recurrenceEndField.setEnabled(true);
            if (oldEvent.getRecurrenceType() != null) {
                recurrenceTypeCombo.setSelectedItem(oldEvent.getRecurrenceType());
            }
            if (oldEvent.getRecurrenceEnd() != null) {
                recurrenceEndField.setText(oldEvent.getRecurrenceEnd().toString());
            }
        } else {
            recurringCheck.setSelected(false);
            recurrenceTypeCombo.setEnabled(false);
            recurrenceEndField.setEnabled(false);
        }
    }

    private void saveEvent() {
        try {
            String title = titleField.getText().trim();
            if (title.isEmpty()) throw new IllegalArgumentException("Title cannot be empty");
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime time = LocalTime.parse(timeField.getText().trim());
            String loc = locationField.getText().trim();
            if (loc.isEmpty()) loc = null;
            String desc = descArea.getText().trim();
            if (desc.isEmpty()) desc = null;
            String cat = (String) categoryCombo.getSelectedItem();
            int rem = (int) reminderSpinner.getValue();

            boolean recurring = recurringCheck.isSelected();
            String recurrenceType = null;
            LocalDate recurrenceEnd = null;
            if (recurring) {
                recurrenceType = (String) recurrenceTypeCombo.getSelectedItem();
                String endText = recurrenceEndField.getText().trim();
                if (!endText.isEmpty()) {
                    recurrenceEnd = LocalDate.parse(endText);
                    if (recurrenceEnd.isBefore(date)) {
                        throw new IllegalArgumentException("End date cannot be before start date");
                    }
                }
            }

            Event newEvent = new Event(title, date, time, loc, desc, cat, rem,
                                       recurring, recurrenceType, recurrenceEnd);

            if (oldEvent == null) {
                eventManager.addEvent(newEvent);
            } else {
                eventManager.updateEvent(oldEvent, newEvent);
            }
            JOptionPane.showMessageDialog(this, "Event saved!");
            dispose();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid date or time format. Use yyyy-MM-dd and HH:mm.",
                "Format error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }
}