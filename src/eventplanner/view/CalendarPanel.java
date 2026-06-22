// Marcelo Palauro Morales	14594034
// Rodrigo Eduardo Rubiano	16311091
// Rapha Mendes	

package eventplanner.view;

import eventplanner.model.Event;
import eventplanner.model.EventManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Draws a monthly calendar grid with colored buttons for days that have events
public class CalendarPanel extends JPanel {
    private EventManager eventManager;
    private MainFrame parent;
    private LocalDate currentMonth;   // first day of current displayed month
    private LocalDate selectedDate;
    private JPanel daysGrid;
    private JLabel monthLabel;

    public CalendarPanel(EventManager eventManager, MainFrame parent) {
        this.eventManager = eventManager;
        this.parent = parent;
        this.currentMonth = LocalDate.now().withDayOfMonth(1);
        this.selectedDate = LocalDate.now();
        setLayout(new BorderLayout());
        createHeader();
        daysGrid = new JPanel(new GridLayout(0, 7));
        add(daysGrid, BorderLayout.CENTER);
        refreshCalendar();
    }

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        JButton todayBtn = new JButton("Today");

        prev.addActionListener(e -> changeMonth(-1));
        next.addActionListener(e -> changeMonth(1));
        todayBtn.addActionListener(e -> {
            selectedDate = LocalDate.now();
            currentMonth = selectedDate.withDayOfMonth(1);
            refreshCalendar();
            parent.refreshAll();
        });

        navPanel.add(prev);
        navPanel.add(next);
        navPanel.add(todayBtn);

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 16));

        header.add(navPanel, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
    }

    private void changeMonth(int delta) {
        currentMonth = currentMonth.plusMonths(delta);
        refreshCalendar();
    }

    public void refreshCalendar() {
        daysGrid.removeAll();
        YearMonth yearMonth = YearMonth.from(currentMonth);
        LocalDate firstOfMonth = currentMonth.withDayOfMonth(1);
        // Monday = 1, Sunday = 7; offset for Monday‑first calendar
        int offset = (firstOfMonth.getDayOfWeek().getValue() - 1 + 7) % 7;

        // Weekday headers
        String[] weekDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String d : weekDays) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            daysGrid.add(lbl);
        }

        // Empty cells before month start
        for (int i = 0; i < offset; i++) {
            daysGrid.add(new JLabel(""));
        }

        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), day);
            List<Event> eventsOnDate = eventManager.getEventsOnDate(date);
            int eventCount = eventsOnDate.size();

            // Button label: day number + event count in parentheses if any
            String label = (eventCount > 0) ? day + " (" + eventCount + ")" : String.valueOf(day);
            JButton dayBtn = new JButton(label);
            dayBtn.setFocusPainted(false);

            // Background color based on category of first event (if any)
            if (eventCount > 0) {
                String cat = eventsOnDate.get(0).getCategory();
                dayBtn.setBackground(getColorForCategory(cat));
            } else {
                dayBtn.setBackground(null);
            }

            // Border: red for selected date, blue for today (if not selected)
            if (date.equals(selectedDate)) {
                dayBtn.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            } else if (date.equals(LocalDate.now())) {
                dayBtn.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            } else {
                dayBtn.setBorder(null);
            }

            // Tooltip: list all event titles for that day
            if (eventCount > 0) {
                StringBuilder tip = new StringBuilder("<html>");
                for (Event e : eventsOnDate) {
                    tip.append(e.getTitle()).append("<br>");
                }
                tip.append("</html>");
                dayBtn.setToolTipText(tip.toString());
            } else {
                dayBtn.setToolTipText("No events");
            }

            dayBtn.addActionListener(e -> {
                selectedDate = date;
                refreshCalendar();       // update border highlight
                parent.refreshAll();     // show events in the list panel
            });

            daysGrid.add(dayBtn);
        }

        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        daysGrid.revalidate();
        daysGrid.repaint();
    }

    // Returns a color based on event category (light pastel tones)
    private Color getColorForCategory(String category) {
        if (category == null) return new Color(173, 216, 230); // light blue default
        switch (category) {
            case "Meeting":     return new Color(255, 200, 200); // light red
            case "Birthday":    return new Color(200, 255, 200); // light green
            case "Appointment": return new Color(200, 200, 255); // light blue
            case "Study":       return new Color(255, 255, 200); // light yellow
            case "Leisure":     return new Color(255, 200, 255); // light pink
            default:            return new Color(173, 216, 230);
        }
    }

    public LocalDate getSelectedDate() { return selectedDate; }
}