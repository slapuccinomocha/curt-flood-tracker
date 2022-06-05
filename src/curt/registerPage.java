package curt;

// Register page to add username
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

// Register page for first time users
public class registerPage extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// Super Panel
	private JPanel registerPanel;
	public CardLayout DynamicLayout = new CardLayout();

	// Consistent elements between panels
	private int pageNo = 1;
	private JLabel stepShower;
	JButton back;
	JButton next;
	private JPanel DynamicPanel;

	// Elements of the intro screen
	private JPanel introPane;
	private JButton Begin;

	private String logopathtoimage = "images/curtLogo.png";
	private BufferedImage logoImageBuffered;
	private ImageIcon logoImage;
	private JLabel logoIcon;

	// Elements of first panel of setup
	private JPanel step1Pane;

	private JPanel normalSignUp;

	private JLabel usernameLab;
	public JTextField username;
	private JLabel passwordLab;
	public JTextField password;

	private JPanel googleSignUp;
	private JButton googleSignIn;

	// Elements of second panel of setup
	private JPanel step2Pane;

	private JPanel formGroup;

	private JLabel step2Header;

	public JTextField streetNo;
	public JTextField streetName;
	public JTextField suburb;
	public JTextField state;
	public JTextField country;
	public JTextField postcode;

	// Elements of third panel of setup
	private JPanel step3Pane;

	private JLabel TermsAgreement;

	// SQL Commands
	static Connection conn;

	static String selectAll = "SELECT * FROM users;";

	// url to connect to database
	static String url = "jdbc:mysql://35.189.31.91/flood-tracker";

	// Username and password for the user
	static String DBusername = "Admin";
	static String DBpassword = "jewfit-wezfAx-1tiwhe";

	// print database table
	public static void printSQLTable(String query) throws SQLException, ClassNotFoundException {

		// connect to database
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(url, DBusername, DBpassword);

		// prepare sql code to execute
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		ResultSetMetaData rsmd = rs.getMetaData();

		int colCount = rsmd.getColumnCount();

		// Print one row
		for (int i = 1; i <= colCount; i++) {
			System.out.print(rsmd.getColumnName(i) + " | ");
		}

		while (rs.next()) {
			System.out.println("");
			for (int i = 1; i <= colCount; i++) {
				// Print one element of a row
				System.out.print(rs.getString(i) + " | ");
			}
			// Move to the next line to print the next row.
			System.out.println();
		}

		conn.close();
	}

	// method to add user to database
	public static void addUser(String name, String password, int streetNumber, String streetName, String Suburb,
			String State) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		
		// connect to database
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(url, DBusername, DBpassword);

		
		String insertUser = "INSERT INTO users(username, password, NoOfReports, streetNumber, streetName, Suburb, State) VALUES (?, ?, 0, ?, ?, ?, ?);";
		String checkUser = "SELECT username FROM users WHERE username=?;";
		
		
		boolean userExists = false;
		PreparedStatement insertStmt = conn.prepareStatement(insertUser);
		PreparedStatement checkStatement = conn.prepareStatement(checkUser);

		checkStatement.setString(1, name);

		ResultSet queryResult = checkStatement.executeQuery();

		if (queryResult.next()) {
			userExists = true;
			JOptionPane.showMessageDialog(new JFrame(), "This username is taken :(");

		} else {
			userExists = false;
		}

		if (!userExists) {
			insertStmt.setString(1, name);
			insertStmt.setString(2, password);
			insertStmt.setInt(3, streetNumber);
			insertStmt.setString(4, streetName);
			insertStmt.setString(5, Suburb);
			insertStmt.setString(6, State);
			insertStmt.executeUpdate();
			JOptionPane.showMessageDialog(new JFrame(),
					"Thank you for signing up! You will be returned to the login screen.");

		}

		conn.close();

	}

	public JPanel getCurrentCard() {
		JPanel card = null;

		for (Component comp : DynamicPanel.getComponents()) {
			if (comp.isVisible() == true) {
				card = (JPanel) comp;
			}
		}
		return card;
	}

	// add user after all information has been enter
	public boolean POSTmethod(JTextField name, JTextField password, JTextField streetNumber, JTextField streetName,
			JTextField Suburb, JTextField State) throws ClassNotFoundException, SQLException, NumberFormatException, NoSuchAlgorithmException {
		boolean valid = true;
		// check if all fields are filled out
		if (name != null && password != null && streetNumber != null && streetName != null && Suburb != null
			&& State != null) {
			
			// ensure password has special symbols
			if (password.getText().matches("[a-zA-Z. ]*")) {
				JOptionPane.showMessageDialog(new JFrame(), "Your Password Does not contain any special symbols");
				valid = false;
			}
			
			// ensure password is more than 8 characters
			if (password.getText().length() <= 8) {
				JOptionPane.showMessageDialog(new JFrame(), "You're password needs to be at least 8 characters");
				valid = false;
			}
			try {
			     Integer.parseInt(streetNumber.getText());
			     System.out.println("An integer");
			}
			catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(new JFrame(), "Enter a correct street number please :|");
			}
			// add user to database
			if (valid) {
				System.out.println(streetNumber.getText());
				addUser(name.getText(), password.getText(), Integer.parseInt(streetNumber.getText()),
						streetName.getText(), Suburb.getText(), State.getText());
				printSQLTable("SELECT * FROM users");
				return true;
			}

		// if they aren't then this message will show
		} else {
			valid = false;
			JOptionPane.showMessageDialog(new JFrame(), "Please fill out all fields");
			return false;
		}
		
		return false;
	}

	// method to place all elements on the window
	public void iterateRegister() throws IOException {

		registerPanel = new JPanel();
		registerPanel.setBackground(Color.decode("#2c3e50"));
		registerPanel.setPreferredSize(new Dimension(400, 250));
		SpringLayout registerLayout = new SpringLayout();
		registerPanel.setLayout(registerLayout);

		stepShower = new JLabel("Step " + Integer.toString(pageNo) + " of 3");
		stepShower.setVisible(false);
		stepShower.setForeground(Color.decode("#e74c3c"));
		stepShower.setFont(new Font("Avenir", Font.PLAIN, 50));

		back = new JButton("< login");
		back.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
		back.setForeground(Color.WHITE);
		back.setFont(new Font("Avenir", Font.PLAIN, 25));

		// Ensure back button isnt visible at start
		back.setVisible(true);

		back.setActionCommand("BACK");
		back.addActionListener(this);

		next = new JButton("next >");
		next.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
		next.setForeground(Color.WHITE);
		next.setFont(new Font("Avenir", Font.PLAIN, 25));
		next.setVisible(false);
		next.setActionCommand("NEXT");
		next.addActionListener(this);

		DynamicPanel = new JPanel();
		DynamicPanel.setLayout(DynamicLayout);

		// Intro panel
		introPane = new JPanel();
		introPane.setLayout(new BoxLayout(introPane, BoxLayout.Y_AXIS));
		introPane.setBackground(Color.decode("#2c3e50"));
		introPane.setName("INTRO");

		logoImageBuffered = ImageIO.read(new File(logopathtoimage));
		Image logoResized = logoImageBuffered.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		logoImage = new ImageIcon(logoResized);
		logoIcon = new JLabel(logoImage);
		logoIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		logoIcon.setAlignmentX(CENTER_ALIGNMENT);

		Begin = new JButton("Begin");
		Begin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
		Begin.setForeground(Color.decode("#e74c3c"));
		Begin.setAlignmentX(CENTER_ALIGNMENT);
		Begin.setFont(new Font("Avenir", Font.PLAIN, 25));

		Begin.setActionCommand("BEGIN");
		Begin.addActionListener(this);

		introPane.add(logoIcon);
		introPane.add(Begin);

		DynamicPanel.add(introPane, "INTRO");

		// First Panel Here
		step1Pane = new JPanel();
		FlowLayout step1Layout = new FlowLayout(FlowLayout.CENTER, 20, 10);
		step1Pane.setLayout(step1Layout);
		step1Pane.setBackground(Color.decode("#2c3e50"));
		step1Pane.setName("step1");

		normalSignUp = new JPanel();
		normalSignUp.setLayout(new BoxLayout(normalSignUp, BoxLayout.Y_AXIS));
		normalSignUp.setBackground(Color.decode("#2c3e50"));
		normalSignUp.setBorder(new EmptyBorder(10, 20, 20, 10));
		step1Pane.add(normalSignUp);

		usernameLab = new JLabel("Username: ");
		usernameLab.setForeground(Color.decode("#ffffff"));
		normalSignUp.add(usernameLab);

		username = new JTextField("", 20);
		normalSignUp.add(username);

		normalSignUp.add(Box.createRigidArea(new Dimension(0, 50)));

		passwordLab = new JLabel("Password: ");
		passwordLab.setForeground(Color.decode("#ffffff"));
		normalSignUp.add(passwordLab);

		password = new JTextField("", 20);
		normalSignUp.add(password);

		googleSignUp = new JPanel();
		googleSignUp.setLayout(new BoxLayout(googleSignUp, BoxLayout.Y_AXIS));
		googleSignUp.setBackground(Color.decode("#2c3e50"));
		googleSignUp.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, Color.decode("#7f8c8d")));

		// Add in Google API and sign in with google capabilities.
		googleSignIn = new JButton("Sign in with google");

		googleSignUp.add(Box.createRigidArea(new Dimension(50, 50)));
		googleSignUp.add(googleSignIn);
		googleSignUp.add(Box.createRigidArea(new Dimension(50, 50)));
		step1Pane.add(googleSignUp);

		DynamicPanel.add(step1Pane, "step1");

		// Second Panel Here
		step2Pane = new JPanel();
		BoxLayout step2Layout = new BoxLayout(step2Pane, BoxLayout.Y_AXIS);
		step2Pane.setLayout(step2Layout);
		step2Pane.setBackground(Color.decode("#2c3e50"));
		step2Pane.setName("step2");

		step2Header = new JLabel("Please enter your home address: ");
		step2Header.setAlignmentX(Component.CENTER_ALIGNMENT);
		step2Header.setForeground(Color.WHITE);
		step2Header.setFont(new Font("Avenir Next", Font.PLAIN, 35));

		step2Pane.add(step2Header);

		formGroup = new JPanel();
		formGroup.setLayout(new GridBagLayout());
		formGroup.setBackground(Color.decode("#2c3e50"));

		Insets formInset = new Insets(10, 10, 10, 10);
		GridBagConstraints a = new GridBagConstraints();

		a.insets = formInset;
		a.gridx = 0;
		a.gridy = 0;
		streetNo = new JTextField("Street Number...", 20);
		formGroup.add(streetNo, a);

		a.gridx = 1;
		a.gridy = 0;
		streetName = new JTextField("Street Name...", 20);
		formGroup.add(streetName, a);

		a.gridx = 0;
		a.gridy = 1;
		suburb = new JTextField("Suburb...", 20);
		formGroup.add(suburb, a);

		a.gridx = 1;
		a.gridy = 1;
		state = new JTextField("State...", 20);
		formGroup.add(state, a);

		a.gridx = 0;
		a.gridy = 2;
		country = new JTextField("Country...", 20);
		formGroup.add(country, a);

		a.gridx = 1;
		a.gridy = 2;
		postcode = new JTextField("Postcode...", 20);
		formGroup.add(postcode, a);

		JTextField[] formTextFields = { streetNo, streetName, suburb, state, country, postcode };

		// Styling for all textfields:
		for (JTextField tEntry : formTextFields) {
			tEntry.setBackground(Color.decode("#2c3e50"));
			tEntry.setForeground(Color.decode("#ffffff"));
			tEntry.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 3, 0, Color.decode("#ffffff")),
					BorderFactory.createEmptyBorder(0, 0, 4, 0)));
			tEntry.setFont(new Font("Avenir", Font.PLAIN, 15));
		}

		step2Pane.add(formGroup);

		DynamicPanel.add(step2Pane, "step2");

		// Third Panel Here
		step3Pane = new JPanel();
		step3Pane.setLayout(new BoxLayout(step3Pane, BoxLayout.Y_AXIS));
		step3Pane.setBackground(Color.decode("#2c3e50"));
		step3Pane.setName("step3");

		TermsAgreement = new JLabel(
				"<html><body style='text-align: center;'>By clicking finish, you agree to our <a href=''> Terms and Conditions, and Privacy Policy</a> </body></html>",
				SwingConstants.CENTER);
		TermsAgreement.addMouseListener(new MouseAdapter() {
			 
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    	try {
					Desktop.getDesktop().browse(new URI("https://singh-aryan.gitbook.io/curt-hazard-tracker/licensing-and-terms/terms-and-conditions"));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
		    }
		});
		TermsAgreement.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		TermsAgreement.setBackground(Color.decode("#2c3e50"));
		TermsAgreement.setForeground(Color.WHITE);
		TermsAgreement.setFont(new Font("Avenir Next", Font.PLAIN, 15));

		step3Pane.add(Box.createVerticalGlue());
		step3Pane.add(TermsAgreement);
		step3Pane.add(Box.createVerticalGlue());

		DynamicPanel.add(step3Pane, "step3");

		// Put Everything in Window
		registerPanel.add(stepShower);

		registerLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, stepShower, 0, SpringLayout.HORIZONTAL_CENTER,
				registerPanel);

		registerLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, DynamicPanel, 0, SpringLayout.HORIZONTAL_CENTER,
				registerPanel);
		registerLayout.putConstraint(SpringLayout.VERTICAL_CENTER, DynamicPanel, 0, SpringLayout.VERTICAL_CENTER,
				registerPanel);

		registerPanel.add(DynamicPanel);

		registerLayout.putConstraint(SpringLayout.WEST, back, 0, SpringLayout.WEST, registerPanel);
		registerLayout.putConstraint(SpringLayout.SOUTH, back, 0, SpringLayout.SOUTH, registerPanel);

		registerLayout.putConstraint(SpringLayout.EAST, next, 0, SpringLayout.EAST, registerPanel);
		registerLayout.putConstraint(SpringLayout.SOUTH, next, 0, SpringLayout.SOUTH, registerPanel);

		registerPanel.add(back);

		registerPanel.add(next);

		pack();
	}

	// method called when object is initialised

	public registerPage() throws IOException, ClassNotFoundException, SQLException {
		// Set size of window
		setPreferredSize(new Dimension(600, 375));
		iterateRegister();
		add(registerPanel);

	}

	// Button Functionality

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand() == "BACK") {
			if (getCurrentCard().getName() == "step1") {

			}
			if (getCurrentCard().getName() == "step2") {
				back.setVisible(false);
				back.setText("< login");
				pageNo -= 1;
				DynamicLayout.previous(DynamicPanel);
			}
			if (getCurrentCard().getName() == "step3") {
				next.setText("next >");
				pageNo -= 1;
				DynamicLayout.previous(DynamicPanel);

			}
		}

		if (e.getActionCommand() == "NEXT") {
			if (getCurrentCard().getName() == "step3") {

			}
			if (getCurrentCard().getName() == "step2") {
				next.setText("finish!");
				pageNo += 1;
				DynamicLayout.next(DynamicPanel);
			}
			if (getCurrentCard().getName() == "step1") {
				back.setVisible(true);
				back.setText("< back");
				pageNo += 1;
				DynamicLayout.next(DynamicPanel);
			}
		}

		if (e.getActionCommand() == "BEGIN") {
			Begin.setVisible(false);
			next.setVisible(true);
			back.setVisible(false);
			DynamicLayout.next(DynamicPanel);
			stepShower.setVisible(true);

		}
		stepShower.setText("Step " + Integer.toString(pageNo) + " of 3");

	}

}
