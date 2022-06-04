package curt;

// Initial screen to ask user to login
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 2 box layouts
// 1 additional box layout9

public class loginPage extends JFrame {

	// Initialise all Panels, Labels, and elements on screen
	private static final long serialVersionUID = 1L;

	public String currentUser;

	private JPanel loginPane;

	private JPanel mainLoginPane;
	JButton submitBtn;
	private JLabel UserLab;
	JTextField User;
	private JLabel PassLab;
	JTextField Pass;
	private JLabel Forgotpass;

	private JPanel googleLoginPane;
	private JButton googleSignIn;
	private JLabel ORText;
	JButton registerButton;

	Connection conn;

	static String selectAll = "SELECT * FROM users;";

	String url = "jdbc:mysql://35.189.31.91/flood-tracker";

	// Username and password for the user
	String DBusername = "Admin";
	String DBpassword = "jewfit-wezfAx-1tiwhe";

	public String getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}

	public Boolean AuthLogin(JTextField usernameEntry, JTextField passwordEntry)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(url, DBusername, DBpassword);

		String checkUser = "SELECT username,password FROM users WHERE username=? AND password=? ;";
		PreparedStatement stmt = conn.prepareStatement(checkUser);

		boolean userExists = false;

		String username = usernameEntry.getText();
		String password = passwordEntry.getText();

		if (username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(new JFrame(), "Please fill out both fields.");

		} else {
			stmt.setString(1, username);
			stmt.setString(2, password);

			ResultSet queryResult = stmt.executeQuery();

			if (queryResult.next() && !userExists) {
				userExists = true;
				setCurrentUser(username);
			} else {
				userExists = false;
				JOptionPane.showMessageDialog(new JFrame(), "User Does Not Exist, Or Incorrect Password or Username");

			}
		}

		conn.close();

		return userExists;

	}

	public loginPage() throws IOException {

		// Instantiate the executive window
		setPreferredSize(new Dimension(600, 375));

		loginPane = new JPanel();
		loginPane.setBackground(Color.decode("#2c3e50"));
		loginPane.setPreferredSize(new Dimension(800, 450));

		SpringLayout mainLayout = new SpringLayout();
		loginPane.setLayout(mainLayout);

		add(loginPane);

		// Instantiate all of the previous elements into visible constructs
		mainLoginPane = new JPanel();
		mainLoginPane.setLayout(new BoxLayout(mainLoginPane, BoxLayout.Y_AXIS));
		mainLoginPane.setBackground(Color.decode("#34495e"));
		mainLoginPane.setBorder(new EmptyBorder(10, 20, 20, 10));

		UserLab = new JLabel("Username: ");
		UserLab.setForeground(Color.decode("#ffffff"));
		mainLoginPane.add(UserLab);

		User = new JTextField("", 20);
		mainLoginPane.add(User);

		mainLoginPane.add(Box.createRigidArea(new Dimension(0, 50)));

		PassLab = new JLabel("Password: ");
		PassLab.setForeground(Color.decode("#ffffff"));
		mainLoginPane.add(PassLab);

		Pass = new JTextField("", 20);
		mainLoginPane.add(Pass);

		Forgotpass = new JLabel("Forgot Password?");
		Forgotpass.setForeground(Color.decode("#ffffff"));
		mainLoginPane.add(Forgotpass);

		mainLoginPane.add(Box.createRigidArea(new Dimension(0, 50)));

		submitBtn = new JButton("Login");
		mainLoginPane.add(submitBtn);

		googleLoginPane = new JPanel();
		googleLoginPane.setLayout(new BoxLayout(googleLoginPane, BoxLayout.Y_AXIS));
		googleLoginPane.setBackground(Color.decode("#34495e"));
		googleLoginPane.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

		// Add in google API and sign in with google capabilities.
		// INCOMPLETE
		googleLoginPane.add(Box.createVerticalStrut(23));

		googleSignIn = new JButton("Sign in with google");
		googleSignIn.setAlignmentX(CENTER_ALIGNMENT);
		googleLoginPane.add(googleSignIn);

		googleLoginPane.add(Box.createVerticalStrut(70));

		ORText = new JLabel(" OR ");
		ORText.setAlignmentX(CENTER_ALIGNMENT);

		ORText.setForeground(Color.decode("#ffffff"));
		googleLoginPane.add(ORText);

		googleLoginPane.add(Box.createVerticalStrut(70));

		registerButton = new JButton("Register Now");
		registerButton.setAlignmentX(CENTER_ALIGNMENT);

		googleLoginPane.add(registerButton);

		googleLoginPane.add(Box.createVerticalStrut(23));

		// Insert Buffer panel for layout purposes
		JPanel loginGroup = new JPanel();
		loginGroup.setBackground(Color.decode("#2c3e50"));
		FlowLayout groupLayout = new FlowLayout(FlowLayout.CENTER, 20, 10);
		loginGroup.setLayout(groupLayout);

		// Add all elements to executive window and show
		mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, loginGroup, 0, SpringLayout.HORIZONTAL_CENTER,
				loginPane);
		mainLayout.putConstraint(SpringLayout.VERTICAL_CENTER, loginGroup, 0, SpringLayout.VERTICAL_CENTER, loginPane);

		loginGroup.add(mainLoginPane);
		loginGroup.add(googleLoginPane);

		loginPane.add(loginGroup);
		pack();

	}

}
