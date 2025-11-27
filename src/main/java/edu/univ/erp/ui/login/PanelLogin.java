package edu.univ.erp.ui.login;

import edu.univ.erp.api.auth.AuthApi;
import edu.univ.erp.api.auth.LoginResult;
import edu.univ.erp.ui.common.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class PanelLogin extends JPanel {

    private final AuthApi authApi = new AuthApi();

    private UserNameField txtUsername;
    private PasswordField txtPassword;
    private JLabel lblMessage;

    public PanelLogin() {
        initComponents();
    }

    private void initComponents() {
        // 1. Main Layout: Center everything
        setLayout(new GridBagLayout());

        // 2. Create the White "Card"
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        // Add a nice border: A thin gray line + lots of padding inside
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true), // Rounded-ish border
                new EmptyBorder(40, 50, 40, 50)
        ));

        // --- Title Section ---
        JLabel title = new JLabel("IIITD ERP");
        title.setFont(new Font("Raleway", Font.BOLD, 28));
        title.setForeground(ColorPalette.PRIMARY_END);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Welcome back! Please login.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(ColorPalette.TEXT_MUTED);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // --- Input Fields Styling ---
        // We define a consistent size and style for inputs
        Dimension inputSize = new Dimension(300, 45);
        Color inputBg = new Color(248, 250, 252); // Very light gray-blue
        Color inputBorder = new Color(200, 200, 200);

        txtUsername = new UserNameField();
        txtUsername.setHint("Username");
        txtUsername.setPreferredSize(inputSize);
        txtUsername.setMaximumSize(inputSize);
        txtUsername.setAlignmentX(CENTER_ALIGNMENT);
        txtUsername.setBackground(inputBg); // Make it visible!
        // Add a border so you can see the box
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(inputBorder, 1),
                new EmptyBorder(0, 10, 0, 10) // Padding text inside
        ));

        txtPassword = new PasswordField();
        txtPassword.setHint("Password");
        txtPassword.setPreferredSize(inputSize);
        txtPassword.setMaximumSize(inputSize);
        txtPassword.setAlignmentX(CENTER_ALIGNMENT);
        txtPassword.setBackground(inputBg); // Make it visible!
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(inputBorder, 1),
                new EmptyBorder(0, 10, 0, 10)
        ));

        // --- Login Button ---
        JButton cmdLogin = new JButton("LOGIN");
        cmdLogin.setPreferredSize(inputSize);
        cmdLogin.setMaximumSize(inputSize);
        cmdLogin.setAlignmentX(CENTER_ALIGNMENT);
        cmdLogin.setBackground(ColorPalette.PRIMARY_START);
        cmdLogin.setForeground(Color.WHITE);
        cmdLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        cmdLogin.setFocusPainted(false);
        cmdLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Remove default ugly border and make it flat
        cmdLogin.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        cmdLogin.addActionListener(e -> performLogin());
        txtPassword.addActionListener(e -> performLogin());

        // --- Message Label ---
        lblMessage = new JLabel(" ");
        lblMessage.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblMessage.setAlignmentX(CENTER_ALIGNMENT);
        lblMessage.setForeground(ColorPalette.ACCENT_ERR);

        // --- Assemble Card ---
        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));

        // Label for Username (Optional, keeping it clean with just hints inside)
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(15));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(25));
        card.add(cmdLogin);
        card.add(Box.createVerticalStrut(15));
        card.add(lblMessage);

        add(card);
    }

    private void performLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        LoginResult result = authApi.attemptLogin(username, password);

        switch (result.getStatus()) {
            case SUCCESS:
                lblMessage.setText("Login Successful!");
                lblMessage.setForeground(ColorPalette.ACCENT_OK);
                launchDashboard(result.getRole());
                break;
            case USER_NOT_FOUND:
                lblMessage.setText("User not found.");
                lblMessage.setForeground(ColorPalette.ACCENT_ERR);
                break;
            case INVALID_PASSWORD:
                int tries = result.getRemainingAttempts();
                if (tries >= 0) {
                    lblMessage.setText("Invalid password. " + tries + " attempts left.");
                } else {
                    lblMessage.setText("Invalid credentials.");
                }
                lblMessage.setForeground(ColorPalette.ACCENT_ERR);
                break;
            case ACCOUNT_LOCKED:
                lblMessage.setText("Account Locked! Too many attempts.");
                lblMessage.setForeground(ColorPalette.ACCENT_ERR);

                Timer unlockTimer = new Timer(5000, evt -> {
                    lblMessage.setText("Lock expired. You may try again.");
                    lblMessage.setForeground(ColorPalette.ACCENT_OK);
                });
                unlockTimer.setRepeats(false);
                unlockTimer.start();
                break;
            case ACCOUNT_INACTIVE:
                lblMessage.setText("Account is inactive.");
                lblMessage.setForeground(ColorPalette.ACCENT_ERR);
                break;
            default:
                lblMessage.setText("Unknown error.");
                lblMessage.setForeground(ColorPalette.ACCENT_ERR);
                break;
        }
    }

    private void launchDashboard(String role) {
        SwingUtilities.invokeLater(() -> {
            switch (role.toLowerCase()) {
                case "student": edu.univ.erp.ui.student.Main.main(null); break;
                case "admin": edu.univ.erp.ui.admin.Main.main(null); break;
                case "instructor": edu.univ.erp.ui.instructor.Main.main(null); break;
            }
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.dispose();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Background Gradient
        GradientPaint gradient = new GradientPaint(0, 0, ColorPalette.PRIMARY_START, getWidth(), getHeight(), ColorPalette.PRIMARY_END);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}