package vn.name.chanhdai.chatapp.client.view;

import vn.name.chanhdai.chatapp.client.Client;
import vn.name.chanhdai.chatapp.client.utils.ViewUtils;
import vn.name.chanhdai.chatapp.common.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * vn.name.chanhdai.chatapp.client
 *
 * @created by ncdai3651408 - StudentID : 18120113
 * @date 7/21/20 - 6:58 PM
 * @description
 */
public class AuthView extends JFrame {
    private Client client;

    private JTextField textFieldUsername;
    private JPasswordField passwordField;

    private JLabel jLabelRePassword;
    private JPasswordField rePasswordField;

    private JButton buttonSubmit;

    public AuthView() {
        createUI();
    }

    private void createUI() {
        this.setTitle("DaiChat");

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(32, 64, 32, 64));
        form.setBackground(Color.WHITE);

        JLabel title = new JLabel("Đăng Nhập");
        title.setFont(new Font("", Font.BOLD, 20));
        form.add(title, ViewUtils.createFormConstraints(0, 0, 3, 0, 0, 8, 0));

        JRadioButton radioButtonLogin = ViewUtils.createRadioButton("Đăng Nhập", 128, 24, SwingConstants.CENTER);
        radioButtonLogin.addActionListener(e -> {
            setRePasswordVisible(false);
            resetForm();
        });

        JRadioButton radioButtonRegister = ViewUtils.createRadioButton("Đăng Ký", 128, 24, SwingConstants.CENTER);
        radioButtonRegister.addActionListener(e -> {
            setRePasswordVisible(true);
            resetForm();
        });

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButtonLogin);
        buttonGroup.add(radioButtonRegister);

        form.add(radioButtonLogin, ViewUtils.createFormConstraints(0, 1, 1));
        form.add(radioButtonRegister, ViewUtils.createFormConstraints(1, 1, 1));

        form.add(new JLabel("Tên Đăng Nhập"), ViewUtils.createFormConstraints(0, 2, 1, 8, 8, 8, 0));
        textFieldUsername = new JTextField(10);

        form.add(textFieldUsername, ViewUtils.createFormConstraints(1, 2, 2, 8, 0));
        textFieldUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Pressed Enter -> Focus passwordField
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });

        form.add(new JLabel("Mật Khẩu"), ViewUtils.createFormConstraints(0, 3, 1));
        passwordField = new JPasswordField();

        form.add(passwordField, ViewUtils.createFormConstraints(1, 3, 2));
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Pressed Enter
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (radioButtonRegister.isSelected()) {
                        // Register -> Focus xacNhanPasswordField
                        rePasswordField.requestFocus();
                    } else {
                        // Login -> Submit
                        buttonSubmit.doClick();
                    }
                }
            }
        });

        jLabelRePassword = new JLabel("Nhập Lại Mật Khẩu");
        form.add(jLabelRePassword, ViewUtils.createFormConstraints(0, 4, 1, 8, 0, 0, 0));

        rePasswordField = new JPasswordField();
        rePasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Pressed Enter -> Submit
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buttonSubmit.doClick();
                }
            }
        });
        form.add(rePasswordField, ViewUtils.createFormConstraints(1, 4, 2, 8, 0, 0, 0));

        buttonSubmit = new JButton("Đăng Nhập");
        buttonSubmit.addActionListener(e -> {
            String username = textFieldUsername.getText();
            String password = String.valueOf(passwordField.getPassword());
            String rePassword = String.valueOf(rePasswordField.getPassword());

            if (username.equals("") || password.equals("") || (radioButtonRegister.isSelected() && rePassword.equals(""))) {
                JOptionPane.showMessageDialog(null, "Bạn chưa nhập đủ thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (radioButtonLogin.isSelected()) {
                login(username, password);
            } else {
                if (!password.equals(rePassword)) {
                    JOptionPane.showMessageDialog(null, "Nhập lại mật khẩu không khớp!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    rePasswordField.setText("");
                    rePasswordField.requestFocus();
                    return;
                }
                register(username, password);
            }
        });
        form.add(buttonSubmit, ViewUtils.createFormConstraints(0, 5, 3, 8, 0, 0, 0));

        JPanel temp = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        temp.setBorder(BorderFactory.createEmptyBorder(64, 64, 64, 64));
        temp.add(form);

        this.add(temp, BorderLayout.CENTER);

        JPanel panelFooter = ViewUtils.createFooter();
        this.add(panelFooter, BorderLayout.PAGE_END);

        this.pack();
        this.setLocationRelativeTo(null);

        radioButtonLogin.setSelected(true);
        setRePasswordVisible(false);

        this.setVisible(true);
    }

    private void login(String username, String password) {
        client = new Client(Config.CHAT_SERVER_HOST, Config.CHAT_SERVER_PORT);
        if (!client.connect()) {
            return;
        }

        UserListPanel userListPanel = new UserListPanel(client);

        if (!client.login(username, password)) {
            return;
        }

        this.setVisible(false);

        JFrame userListFrame = new JFrame();
        userListFrame.setTitle("DaiChat - " + username);
        userListFrame.setLayout(new BorderLayout());
        userListFrame.setSize(new Dimension(500, 500));

        userListFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        userListFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.disconnect();
            }
        });

        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.X_AXIS));
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("<html><h1 style='margin-top: 0; margin-bottom: 0'>" + username + "</h1></html>");

        JButton buttonLogout = new JButton("Đăng Xuất");
        buttonLogout.setBackground(Color.decode("#eeeeee"));
        buttonLogout.setPreferredSize(new Dimension(144, 24));
        buttonLogout.setFocusPainted(false);
        buttonLogout.addActionListener(e -> {
            client.disconnect();
            userListFrame.setVisible(false);
            setVisible(true);
        });

        panelHeader.add(title);
        panelHeader.add(Box.createHorizontalGlue());
        panelHeader.add(buttonLogout);

        userListFrame.getContentPane().add(panelHeader, BorderLayout.PAGE_START);
        userListFrame.getContentPane().add(userListPanel, BorderLayout.CENTER);

        userListFrame.setLocationRelativeTo(null);
        userListFrame.setVisible(true);
    }

    private void register(String username, String password) {
        client = new Client(Config.CHAT_SERVER_HOST, Config.CHAT_SERVER_PORT);
        if (!client.connect()) {
            return;
        }

        if (!client.register(username, password)) {
            JOptionPane.showMessageDialog(null, "Đăng ký không thành công!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("register ok");
        this.login(username, password);
    }

    private void resetForm() {
        passwordField.setText("");
        textFieldUsername.setText("");
        textFieldUsername.requestFocus();
    }

    private void setRePasswordVisible(boolean visible) {
        jLabelRePassword.setVisible(visible);
        rePasswordField.setVisible(visible);

        if (visible) {
            buttonSubmit.setText("Đăng Ký");
        } else {
            buttonSubmit.setText("Đăng Nhập");
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        if (b) {
            textFieldUsername.setText("");
            passwordField.setText("");
            rePasswordField.setText("");

            textFieldUsername.requestFocus();
        }
    }
}
