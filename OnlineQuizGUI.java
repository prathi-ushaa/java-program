import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class OnlineQuizGUI extends JFrame implements ActionListener {
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    JLabel lblQuestion;
    JRadioButton opt1, opt2, opt3, opt4;
    ButtonGroup bg;
    JButton btnNext;
    int correctAnswer, score = 0, qCount = 0, totalQ = 0;

    public OnlineQuizGUI() {
        // --- GUI Layout ---
        setTitle("Online Quiz Application");
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Question Label
        lblQuestion = new JLabel("Question will appear here");
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 16));
        lblQuestion.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Options
        opt1 = new JRadioButton();
        opt2 = new JRadioButton();
        opt3 = new JRadioButton();
        opt4 = new JRadioButton();

        bg = new ButtonGroup();
        bg.add(opt1); bg.add(opt2); bg.add(opt3); bg.add(opt4);

        JPanel optionPanel = new JPanel(new GridLayout(4, 1));
        optionPanel.add(opt1);
        optionPanel.add(opt2);
        optionPanel.add(opt3);
        optionPanel.add(opt4);

        // Next Button
        btnNext = new JButton("Next");
        btnNext.addActionListener(this);

        add(lblQuestion, BorderLayout.NORTH);
        add(optionPanel, BorderLayout.CENTER);
        add(btnNext, BorderLayout.SOUTH);

        connectDB();
        loadQuestion();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/quizdb", "root", "root");

            // Count total questions
            Statement st = con.createStatement();
            ResultSet rsCount = st.executeQuery("SELECT COUNT(*) FROM questions");
            if (rsCount.next()) totalQ = rsCount.getInt(1);

            pst = con.prepareStatement("SELECT * FROM questions", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pst.executeQuery();
            rs.next();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Connection Failed: " + e.getMessage());
        }
    }

    void loadQuestion() {
        try {
            if (rs.isAfterLast()) {
                // Quiz Over
                JOptionPane.showMessageDialog(this, "Quiz Over!\nYour Score: " + score + "/" + totalQ);
                System.exit(0);
            } else {
                qCount++;
                lblQuestion.setText("Q" + qCount + ": " + rs.getString("question"));
                opt1.setText(rs.getString("option1"));
                opt2.setText(rs.getString("option2"));
                opt3.setText(rs.getString("option3"));
                opt4.setText(rs.getString("option4"));

                correctAnswer = rs.getInt("answer");

                bg.clearSelection();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selected = -1;
        if (opt1.isSelected()) selected = 1;
        else if (opt2.isSelected()) selected = 2;
        else if (opt3.isSelected()) selected = 3;
        else if (opt4.isSelected()) selected = 4;

        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select an answer!");
            return;
        }

        if (selected == correctAnswer) {
            score++;
        }

        try {
            rs.next();
            loadQuestion();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new OnlineQuizGUI();
    }
}