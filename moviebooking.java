import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MovieBooking implements ActionListener {

    JFrame frame, resultFrame;
    JTextField movieIdField;
    JLabel label;
    JButton searchButton;
    JTable table;

    // Database connection details
    String driverName = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/MovieBooking";  // Database name
    String userName = "root";   // Change if needed
    String password = "root";   // Change if needed

    String[] columnNames = {"Movie ID", "Movie Name", "Timing", "Available Seats"};

    // GUI setup
    public void createUI() {
        frame = new JFrame("Movie Ticket Booking");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        label = new JLabel("Enter Movie ID:");
        label.setBounds(30, 30, 120, 25);

        movieIdField = new JTextField();
        movieIdField.setBounds(160, 30, 150, 25);

        searchButton = new JButton("Search");
        searchButton.setBounds(160, 70, 150, 25);
        searchButton.addActionListener(this);

        frame.add(label);
        frame.add(movieIdField);
        frame.add(searchButton);

        frame.setSize(400, 200);
        frame.setVisible(true);
    }

    // Action when button is clicked
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == searchButton) {
            showTableData();
        }
    }

    // Display movie details
    public void showTableData() {
        if (resultFrame != null) {
            resultFrame.dispose();
        }

        resultFrame = new JFrame("Movie Details");
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);

        table = new JTable();
        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        String movieIdText = movieIdField.getText().trim();

        if (movieIdText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a Movie ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Class.forName(driverName);
            Connection con = DriverManager.getConnection(url, userName, password);
            String sql = "SELECT * FROM movies WHERE movie_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(movieIdText));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("movie_id");
                String name = rs.getString("movie_name");
                String timing = rs.getString("timing");
                int seats = rs.getInt("available_seats");
                model.addRow(new Object[]{id, name, timing, seats});
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No movie found with ID: " + movieIdText, "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }

            con.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        resultFrame.add(scroll);
        resultFrame.setSize(500, 300);
        resultFrame.setVisible(true);
    }

    public static void main(String[] args) {
        MovieBooking mb = new MovieBooking();
        mb.createUI();
    }
}