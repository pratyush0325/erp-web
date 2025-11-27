package edu.univ.erp.ui.login;

import edu.univ.erp.api.auth.AuthApi;
import edu.univ.erp.api.auth.LoginResult;


import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class PanelLogin extends JPanel {

    // <-- 2. ADDED API FIELD
    private final AuthApi authApi = new AuthApi();

    // Fields for components
    private UserNameField txtUsername;
    private PasswordField txtPassword;
    private JLabel lblMessage;

    public PanelLogin() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(150, 150, 150, 150));

        JLabel title = new JLabel("ERP Login");
        title.setFont(new Font("sansserif", Font.BOLD, 24));
        title.setAlignmentX(CENTER_ALIGNMENT);

        txtUsername = new UserNameField();
        txtUsername.setHint("Username");
        txtUsername.setAlignmentX(CENTER_ALIGNMENT);
        txtUsername.setPreferredSize(new Dimension(250, 40));
        txtUsername.setMaximumSize(txtUsername.getPreferredSize());

        txtPassword = new PasswordField();
        txtPassword.setHint("Password");
        txtPassword.setAlignmentX(CENTER_ALIGNMENT);
        txtPassword.setPreferredSize(new Dimension(250, 40));
        txtPassword.setMaximumSize(txtPassword.getPreferredSize());

        JButton cmdLogin = new JButton("Login");
        cmdLogin.setAlignmentX(CENTER_ALIGNMENT);

        // <-- 3. REWROTE ACTION LISTENER
        cmdLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            // --- API Call ---
            LoginResult result = authApi.attemptLogin(username, password);

            // --- Handle Result ---
            switch (result.getStatus()) {
                case SUCCESS:
                    lblMessage.setText("Login Successful! Launching...");
                    lblMessage.setForeground(new Color(0, 128, 0)); // Dark Green
                    launchDashboard(result.getRole());
                    break;
                case USER_NOT_FOUND:
                    lblMessage.setText("Error: User not found.");
                    lblMessage.setForeground(Color.RED);
                    break;
                case INVALID_PASSWORD:
                    // Show remaining attempts (requires updated LoginResult class)
                    int tries = result.getRemainingAttempts();
                    if (tries >= 0) {
                        lblMessage.setText("Error: Invalid password. " + tries + " attempts remaining.");
                    } else {
                        lblMessage.setText("Error: Invalid username or password.");
                    }
                    lblMessage.setForeground(Color.RED);
                    break;
                case ACCOUNT_LOCKED:
                    lblMessage.setText("Account Locked! Too many failed attempts.");
                    lblMessage.setForeground(Color.RED);

                    Timer unlockTimer = new Timer(5000, evt -> {
                        lblMessage.setText("Lock expired. You may try again.");
                        lblMessage.setForeground(new Color(0, 128, 0)); // Green to signal "Go"
                    });
                    unlockTimer.setRepeats(false); // Run only once
                    unlockTimer.start();
                    // ------------------------------------------------
                    break;
                case ACCOUNT_INACTIVE:
                    lblMessage.setText("Account Inactive. Please contact Admin.");
                    lblMessage.setForeground(Color.RED);
                    break;
                default:
                    lblMessage.setText("Error: An unknown error occurred.");
                    lblMessage.setForeground(Color.RED);
                    break;
            }
        });

        lblMessage = new JLabel("Click Login Button To Proceed");
        lblMessage.setAlignmentX(CENTER_ALIGNMENT);
        lblMessage.setForeground(new Color(0, 0, 0));

        add(title);
        add(Box.createVerticalStrut(20));
        add(txtUsername);
        add(Box.createVerticalStrut(10));
        add(txtPassword);
        add(Box.createVerticalStrut(20));
        add(cmdLogin);
        add(Box.createVerticalStrut(10));
        add(lblMessage);
    }

    // <-- 4. ADDED HELPER METHOD
    /**
     * Launches the correct dashboard based on the user's role
     * and closes the current login window.
     */
    private void launchDashboard(String role) {
        // Run on the Swing thread
        SwingUtilities.invokeLater(() -> {
            // Launch the new dashboard's main method
            switch (role.toLowerCase()) {
                case "student":
                    edu.univ.erp.ui.student.Main.main(null);
                    break;
                case "admin":
                    edu.univ.erp.ui.admin.Main.main(null);
                    break;
                case "instructor":
                    edu.univ.erp.ui.instructor.Main.main(null);
                    break;
                default:
                    // Failsafe, shouldn't happen
                    lblMessage.setText("Error: Unknown role '" + role + "'.");
                    lblMessage.setForeground(Color.RED);
                    return; // Don't close the window
            }

            // Get the top-level JFrame this panel is in and dispose of it
            Window loginWindow = SwingUtilities.getWindowAncestor(this);
            if (loginWindow != null) {
                loginWindow.dispose();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        // (Your existing paintComponent code is perfect, no changes needed)
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Color startColor = new Color(0x2DE1C2);
        Color endColor = new Color(0x6AD5CB);
        GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}