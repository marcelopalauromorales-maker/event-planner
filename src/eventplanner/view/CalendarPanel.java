package eventplanner.view;

import eventplanner.model.EventManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

// Draws a monthly calendar grid with buttons for each day
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
        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        prev.addActionListener(e -> changeMonth(-1));
        next.addActionListener(e -> changeMonth(1));
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(prev, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);
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
        int offset = firstOfMonth.getDayOfWeek().getValue() - 1; // Monday=1 -> offset 0

        // Day name header
        String[] weekDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String d : weekDays) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            daysGrid.add(lbl);
        }

        // empty cells before month start
        for (int i = 0; i < offset; i++) {
            daysGrid.add(new JLabel(""));
        }

        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), day);
            JButton dayBtn = new JButton(String.valueOf(day));
            dayBtn.setFocusPainted(false);
            // highlight if there is any event
            if (!eventManager.getEventsOnDate(date).isEmpty()) {
                dayBtn.setBackground(new Color(173, 216, 230)); // light blue
            }
            if (date.equals(selectedDate)) {
                dayBtn.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }
            dayBtn.addActionListener(e -> {
                selectedDate = date;
                refreshCalendar();     // redraw to update border
                parent.refreshAll();
            });
            daysGrid.add(dayBtn);
        }

        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        daysGrid.revalidate();
        daysGrid.repaint();
    }

    public LocalDate getSelectedDate() { return selectedDate; }
}