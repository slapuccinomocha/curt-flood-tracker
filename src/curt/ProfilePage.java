package curt;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Profile page
public class ProfilePage extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Panels
	private JPanel ProfilePage;
	private SpringLayout ProfileLayout;
	private JPanel HomeAddressPanel;
	private JPanel PersonalInfoPanel;

	// Text Labels
	private JLabel title;

	// Back Button
	JButton back;

	// Profile Pic
	private String pathtopic = "images/defproPic.png";
	private BufferedImage profileImageBuffered;
	private ImageIcon profileImage;
	private JLabel profileIcon;

	// Field Labels
	private JLabel usernameLbl;
	private JLabel passwordLbl;
	private JLabel streetNoLbl;
	private JLabel streetNameLbl;
	private JLabel suburbLbl;
	private JLabel stateLbl;
	private JLabel countryLbl;
	private JLabel NoOfReportsLbl;

	// Text Fields
	private JTextField username;
	private JTextField password;
	private JTextField streetNo;
	private JTextField streetName;
	private JTextField suburb;
	private JTextField state;
	private JTextField country;
	private JTextField NoOfReports;

	// connection variable to database
	Connection conn;
	// Username and password for the user
	static String DBusername = "Admin";
	static String DBpassword = "jewfit-wezfAx-1tiwhe";
	static String url = "jdbc:mysql://35.189.31.91/flood-tracker";

	public void getProfileInfo(String username) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(url, DBusername, DBpassword);

		String getProfInfo = "SELECT username, password, NoOfReports, streetNumber, streetName, Suburb, State FROM users WHERE username=?";
		String name = null;
		String passwordVal = null;
		String noOfreports = null;
		String streetNumber = null;
		String streetName = null;
		String suburb = null;
		String state = null;
		PreparedStatement getInfo = conn.prepareStatement(getProfInfo);
		getInfo.setString(1, username);
		ResultSet rs = getInfo.executeQuery();
		if (rs.next()) {
			name = rs.getString(1);
			passwordVal = rs.getString(2);
			noOfreports = rs.getString(3);
			streetNumber = rs.getString(4);
			streetName = rs.getString(5);
			suburb = rs.getString(6);
			state = rs.getString(7);
		}
		this.username.setText(name);
		this.password.setText(passwordVal);
		this.streetNo.setText(streetNumber);
		this.NoOfReports.setText(noOfreports);
		this.streetName.setText(streetName);
		this.suburb.setText(suburb);
		this.state.setText(state);

		conn.close();
	}

	public void iterateProfile() throws IOException, ClassNotFoundException, SQLException {

		// Main page
		ProfilePage = new JPanel();
		ProfileLayout = new SpringLayout();
		ProfilePage.setLayout(ProfileLayout);
		ProfilePage.setBackground(Color.decode("#2c3e50"));

		title = new JLabel("Profile");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Avenir", Font.PLAIN, 50));

		back = new JButton("< back");
		back.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
		back.setForeground(Color.WHITE);
		back.setFont(new Font("Avenir", Font.PLAIN, 25));
		back.setActionCommand("BACK");

		// Left side, personal info
		PersonalInfoPanel = new JPanel();
		SpringLayout personalLayout = new SpringLayout();
		PersonalInfoPanel.setLayout(personalLayout);
		PersonalInfoPanel.setBackground(Color.decode("#34495e"));
		PersonalInfoPanel.setPreferredSize(new Dimension(350, 384));
		PersonalInfoPanel.setVisible(true);
//		PersonalInfoPanel.setBorder(BorderFactory.createEmptyBorder(42,40,85,40));

		usernameLbl = new JLabel("Username");
		usernameLbl.setForeground(Color.WHITE);
		usernameLbl.setFont(new Font("Avenir", Font.PLAIN, 15));

		username = new JTextField("", 20);

		passwordLbl = new JLabel("Password");
		passwordLbl.setForeground(Color.WHITE);
		passwordLbl.setFont(new Font("Avenir", Font.PLAIN, 15));

		password = new JTextField("", 20);

		NoOfReportsLbl = new JLabel("NoOfReports:");
		NoOfReportsLbl.setFont(new Font("Avenir", Font.PLAIN, 15));
		NoOfReportsLbl.setForeground(Color.WHITE);

		NoOfReports = new JTextField("", 20);

		profileImageBuffered = ImageIO.read(new File(pathtopic));
		Image profileResized = profileImageBuffered.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		profileImage = new ImageIcon(profileResized);
		profileIcon = new JLabel(profileImage);

		PersonalInfoPanel.add(profileIcon);
		PersonalInfoPanel.add(usernameLbl);
		PersonalInfoPanel.add(username);
		PersonalInfoPanel.add(passwordLbl);
		PersonalInfoPanel.add(password);
		PersonalInfoPanel.add(NoOfReportsLbl);
		PersonalInfoPanel.add(NoOfReports);

		personalLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, profileIcon, 0, SpringLayout.HORIZONTAL_CENTER,
				PersonalInfoPanel);
		personalLayout.putConstraint(SpringLayout.NORTH, profileIcon, 50, SpringLayout.NORTH, PersonalInfoPanel);

		personalLayout.putConstraint(SpringLayout.NORTH, usernameLbl, 20, SpringLayout.SOUTH, profileIcon);
		personalLayout.putConstraint(SpringLayout.WEST, usernameLbl, 40, SpringLayout.WEST, PersonalInfoPanel);

		personalLayout.putConstraint(SpringLayout.NORTH, username, 5, SpringLayout.SOUTH, usernameLbl);
		personalLayout.putConstraint(SpringLayout.WEST, username, 40, SpringLayout.WEST, PersonalInfoPanel);

		personalLayout.putConstraint(SpringLayout.NORTH, passwordLbl, 20, SpringLayout.SOUTH, username);
		personalLayout.putConstraint(SpringLayout.WEST, passwordLbl, 40, SpringLayout.WEST, PersonalInfoPanel);

		personalLayout.putConstraint(SpringLayout.NORTH, password, 5, SpringLayout.SOUTH, passwordLbl);
		personalLayout.putConstraint(SpringLayout.WEST, password, 40, SpringLayout.WEST, PersonalInfoPanel);

		personalLayout.putConstraint(SpringLayout.NORTH, NoOfReportsLbl, 20, SpringLayout.SOUTH, password);
		personalLayout.putConstraint(SpringLayout.WEST, NoOfReportsLbl, 40, SpringLayout.WEST, PersonalInfoPanel);

		personalLayout.putConstraint(SpringLayout.NORTH, NoOfReports, 5, SpringLayout.SOUTH, NoOfReportsLbl);
		personalLayout.putConstraint(SpringLayout.WEST, NoOfReports, 40, SpringLayout.WEST, PersonalInfoPanel);
		// right side, home address panel
		HomeAddressPanel = new JPanel();
		HomeAddressPanel.setLayout(new BoxLayout(HomeAddressPanel, BoxLayout.Y_AXIS));
		HomeAddressPanel.setBackground(Color.decode("#34495e"));
		HomeAddressPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

		streetNoLbl = new JLabel("Street Number:");
		streetNoLbl.setFont(new Font("Avenir", Font.PLAIN, 15));
		streetNoLbl.setForeground(Color.WHITE);

		HomeAddressPanel.add(streetNoLbl);

		streetNo = new JTextField("", 20);

		HomeAddressPanel.add(streetNo);

		streetNameLbl = new JLabel("Street Name:");
		streetNameLbl.setFont(new Font("Avenir", Font.PLAIN, 15));
		streetNameLbl.setForeground(Color.WHITE);

		streetName = new JTextField("", 20);

		suburbLbl = new JLabel("Suburb:");
		suburbLbl.setFont(new Font("Avenir", Font.PLAIN, 15));
		suburbLbl.setForeground(Color.WHITE);

		suburb = new JTextField("", 20);

		stateLbl = new JLabel("State:");
		stateLbl.setFont(new Font("Avenir", Font.PLAIN, 15));
		stateLbl.setForeground(Color.WHITE);

		state = new JTextField("", 20);

		countryLbl = new JLabel("Country:");
		countryLbl.setFont(new Font("Avenir", Font.PLAIN, 15));
		countryLbl.setForeground(Color.WHITE);

		country = new JTextField("Australia", 20);

		HomeAddressPanel.add(streetNoLbl);
		HomeAddressPanel.add(streetNo);
		HomeAddressPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		HomeAddressPanel.add(streetNameLbl);
		HomeAddressPanel.add(streetName);
		HomeAddressPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		HomeAddressPanel.add(suburbLbl);
		HomeAddressPanel.add(suburb);
		HomeAddressPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		HomeAddressPanel.add(stateLbl);
		HomeAddressPanel.add(state);
		HomeAddressPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		HomeAddressPanel.add(countryLbl);
		HomeAddressPanel.add(country);
		HomeAddressPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		ProfilePage.add(title);
		ProfilePage.add(back);
		ProfilePage.add(PersonalInfoPanel);
		ProfilePage.add(HomeAddressPanel);

		JTextField[] formTextFields = { username, password, streetNo, streetName, suburb, state, country, NoOfReports };

		// Styling for all textfields:
		for (JTextField tEntry : formTextFields) {
			tEntry.setBackground(Color.decode("#34495e"));
			tEntry.setForeground(Color.decode("#ffffff"));
			tEntry.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 3, 0, Color.decode("#ffffff")),
					BorderFactory.createEmptyBorder(0, 0, 4, 0)));
			tEntry.setFont(new Font("Avenir", Font.PLAIN, 15));
			tEntry.setEditable(false);
		}

		add(ProfilePage);

		ProfileLayout.putConstraint(SpringLayout.NORTH, title, 15, SpringLayout.NORTH, ProfilePage);
		ProfileLayout.putConstraint(SpringLayout.WEST, title, 45, SpringLayout.WEST, ProfilePage);

		ProfileLayout.putConstraint(SpringLayout.NORTH, back, 20, SpringLayout.NORTH, ProfilePage);
		ProfileLayout.putConstraint(SpringLayout.WEST, back, 475, SpringLayout.EAST, title);

		ProfileLayout.putConstraint(SpringLayout.NORTH, PersonalInfoPanel, 80, SpringLayout.NORTH, ProfilePage);
		ProfileLayout.putConstraint(SpringLayout.WEST, PersonalInfoPanel, 50, SpringLayout.WEST, ProfilePage);

		ProfileLayout.putConstraint(SpringLayout.NORTH, HomeAddressPanel, 80, SpringLayout.NORTH, ProfilePage);
		ProfileLayout.putConstraint(SpringLayout.WEST, HomeAddressPanel, 20, SpringLayout.EAST, PersonalInfoPanel);

		pack();
	}

	public ProfilePage() throws IOException, ClassNotFoundException, SQLException {
		iterateProfile();

	}
}
