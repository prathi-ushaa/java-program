import java.sql.*;
import java.util.Scanner;

public class HospitalManagement {
    static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
    static final String USER = "root";  // your MySQL username
    static final String PASS = "root"; // your MySQL password

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("✅ Connected to Database");

            while (true) {
                System.out.println("\n=== Hospital Management System ===");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. Add Doctor");
                System.out.println("4. View Doctors");
                System.out.println("5. Book Appointment");
                System.out.println("6. View Appointments");
                System.out.println("0. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1 -> addPatient(conn, sc);
                    case 2 -> viewPatients(conn);
                    case 3 -> addDoctor(conn, sc);
                    case 4 -> viewDoctors(conn);
                    case 5 -> bookAppointment(conn, sc);
                    case 6 -> viewAppointments(conn);
                    case 0 -> { 
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("❌ Invalid Choice!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add Patient
    public static void addPatient(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Gender: ");
        String gender = sc.nextLine();
        System.out.print("Enter Disease: ");
        String disease = sc.nextLine();
        System.out.print("Enter Admission Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        String sql = "INSERT INTO patient (name, age, gender, disease, admission_date) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setInt(2, age);
        stmt.setString(3, gender);
        stmt.setString(4, disease);
        stmt.setString(5, date);
        stmt.executeUpdate();

        System.out.println("✅ Patient added successfully!");
    }

    // View Patients
    public static void viewPatients(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM patient");
        System.out.println("\n--- Patient List ---");
        while (rs.next()) {
            System.out.printf("%d | %s | %d | %s | %s | %s\n",
                    rs.getInt("patient_id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("disease"),
                    rs.getDate("admission_date"));
        }
    }

    // Add Doctor
    public static void addDoctor(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Specialization: ");
        String specialization = sc.nextLine();
        System.out.print("Enter Phone: ");
        String phone = sc.nextLine();

        String sql = "INSERT INTO doctor (name, specialization, phone) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, specialization);
        stmt.setString(3, phone);
        stmt.executeUpdate();

        System.out.println("✅ Doctor added successfully!");
    }

    // View Doctors
    public static void viewDoctors(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM doctor");
        System.out.println("\n--- Doctor List ---");
        while (rs.next()) {
            System.out.printf("%d | %s | %s | %s\n",
                    rs.getInt("doctor_id"),
                    rs.getString("name"),
                    rs.getString("specialization"),
                    rs.getString("phone"));
        }
    }

    // Book Appointment
    public static void bookAppointment(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Patient ID: ");
        int pid = sc.nextInt();
        System.out.print("Enter Doctor ID: ");
        int did = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        String sql = "INSERT INTO appointment (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, pid);
        stmt.setInt(2, did);
        stmt.setString(3, date);
        stmt.executeUpdate();

        System.out.println("✅ Appointment booked successfully!");
    }

    // View Appointments
    public static void viewAppointments(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT a.appointment_id, p.name AS patient, d.name AS doctor, a.appointment_date " +
                     "FROM appointment a JOIN patient p ON a.patient_id = p.patient_id " +
                     "JOIN doctor d ON a.doctor_id = d.doctor_id";
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("\n--- Appointments ---");
        while (rs.next()) {
            System.out.printf("%d | Patient: %s | Doctor: %s | Date: %s\n",
                    rs.getInt("appointment_id"),
                    rs.getString("patient"),
                    rs.getString("doctor"),
                    rs.getDate("appointment_date"));
        }
    }
}