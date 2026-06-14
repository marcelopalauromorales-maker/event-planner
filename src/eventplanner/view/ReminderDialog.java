package eventplanner.view;

import eventplanner.model.Event;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Simple popup window to show reminders when the application starts
public class ReminderDialog {
    public static void showReminders(JFrame parent, List<Event> reminders) {
        if (reminders == null || reminders.isEmpty()) return;

        StringBuilder msg = new StringBuilder();
        msg.append("The following events need your attention:\n\n");
        for (Event e : reminders) {
            msg.append("• ").append(e.getTitle())
               .append(" on ").append(e.getDate())
               .append(" at ").append(e.getTime())
               .append(" (reminder set ").append(e.getReminderHours()).append(" hours before)\n");
        }
        JOptionPane.showMessageDialog(parent, msg.toString(),
                "Upcoming Reminders", JOptionPane.INFORMATION_MESSAGE);
    }
}