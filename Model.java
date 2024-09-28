import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;

public class Model extends JFrame implements ActionListener {

    JButton add, edit, view;
    Connection connection = null;

    public Model() {
        setTitle("Naari Fashion Inventory");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("2.png");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(null);

        JLabel heading = new JLabel("WELCOME TO NAARI FASHION");
        heading.setBounds(200, 100, 600, 30);
        heading.setFont(new Font("Arial", Font.PLAIN, 24));
        heading.setForeground(Color.RED);
        backgroundPanel.add(heading);

        add = new JButton("ADD");
        add.setBounds(200, 500, 100, 30);
        backgroundPanel.add(add);

        edit = new JButton("EDIT");
        edit.setBounds(350, 500, 100, 30);
        backgroundPanel.add(edit);

        view = new JButton("VIEW");
        view.setBounds(500, 500, 100, 30);
        backgroundPanel.add(view);

        add.addActionListener(this);
        edit.addActionListener(this);
        view.addActionListener(this);

        add(backgroundPanel);
        setVisible(true);

        // Establish the database connection when the GUI loads
        connectToDatabase();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            addItem();
        } else if (e.getSource() == edit) {
            editItem();
        } else if (e.getSource() == view) {
            viewItems(); // Call the new viewItems() function here
        }
    }

    private void addItem() {
        JFrame addFrame = new JFrame("Add Item");
        JPanel addPanel = new JPanel(new GridLayout(6, 2));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField costPriceField = new JTextField();
        JTextField sellingPriceField = new JTextField();

        addPanel.add(new JLabel("ID:"));
        addPanel.add(idField);
        addPanel.add(new JLabel("Name:"));
        addPanel.add(nameField);
        addPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        addPanel.add(dateField);
        addPanel.add(new JLabel("Cost Price:"));
        addPanel.add(costPriceField);
        addPanel.add(new JLabel("Selling Price:"));
        addPanel.add(sellingPriceField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(event -> {
            String dateInput = dateField.getText();

            if (idField.getText().isEmpty() || costPriceField.getText().isEmpty() || sellingPriceField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(addFrame, "Error: All fields must be filled.");
                return;
            }

            if (!dateInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(addFrame, "Error: Date must be in YYYY-MM-DD format.");
                return;
            }

            try {
                String query = "INSERT INTO items (id, name, date, costprice, sellingprice) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setInt(1, Integer.parseInt(idField.getText()));
                    pstmt.setString(2, nameField.getText());
                    pstmt.setDate(3, java.sql.Date.valueOf(dateInput));
                    pstmt.setInt(4, Integer.parseInt(costPriceField.getText()));
                    pstmt.setBigDecimal(5, new java.math.BigDecimal(sellingPriceField.getText()));
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(addFrame, "Item added successfully!");
                    addFrame.dispose();
                }
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(addFrame, "Error: " + ex.getMessage());
            }
        });

        addPanel.add(submitButton);
        addFrame.add(addPanel);
        addFrame.setSize(300, 300);
        addFrame.setVisible(true);
        addFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void editItem() {
        // Your existing code for the edit function
    }

    private void viewItems() {
        JFrame viewFrame = new JFrame("View Items");
        viewFrame.setSize(600, 400);
        viewFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String[] columns = {"ID", "Name", "Date", "Cost Price", "Selling Price"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        viewFrame.add(scrollPane, BorderLayout.CENTER);

        try {
            String query = "SELECT * FROM items";
            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String date = rs.getString("date");
                    String costPrice = rs.getString("costprice");
                    String sellingPrice = rs.getString("sellingprice");

                    model.addRow(new Object[]{id, name, date, costPrice, sellingPrice});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(viewFrame, "Error: " + ex.getMessage());
        }

        viewFrame.setVisible(true);
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/HELLO";
        String password = "wrecky";
        String user = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Model::new);
    }
}
