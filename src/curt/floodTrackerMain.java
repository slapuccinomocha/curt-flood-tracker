package curt;

// import necessary packages and external libraries
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;


// this is the main class that runs, on startup
public class floodTrackerMain extends JFrame {

	// instantiate all objects of all other windows
	private static final long serialVersionUID = 1L;

	public static boolean loggedIn = false;

	public static loginPage loginTab;
	public static registerPage registerTab;
	public static MapPage MapTab;
	public static ProfilePage ProfileTab;
	private static JLayeredPane parent;

	// hold the currentUser logged in, in this
	public static String currentUser;

	// set up the register page before its needed.
	public static void iterateRegister(floodTrackerMain frame, Container register, Container login) {
		frame.setPreferredSize(new Dimension(600, 375));
		frame.setSize(new Dimension(600, 375));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(register);
		frame.add(login);

		frame.setVisible(true);

		register.setVisible(false);
		login.setVisible(true);

	}

	public static void main(String args[])
			throws IOException, ClassNotFoundException, SQLException, FontFormatException {

		final floodTrackerMain mainApp = new floodTrackerMain();
		mainApp.setResizable(false);
		final floodTrackerMain registerApp = new floodTrackerMain();
		registerApp.setResizable(false);

		mainApp.setPreferredSize(new Dimension(800, 500));
		mainApp.setLayout(new BorderLayout());
		parent = new JLayeredPane();
		parent.setLayout(null);

		mainApp.add(parent);
		parent.setSize(800, 500);
		parent.setBackground(Color.decode("#2c3e50"));
		parent.setOpaque(true);

		// initialise map/profile screens

		mainApp.setVisible(false);
		mainApp.setSize(800, 500);
		mainApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainApp.pack();

		ProfileTab = new ProfilePage();
		final Container profilePage = ProfileTab.getContentPane();
		ProfileTab.setBounds(0, 0, 800, 500);
		profilePage.setBounds(0, 0, 800, 500);
		parent.add(profilePage, JLayeredPane.PALETTE_LAYER);
		profilePage.setVisible(false);

		MapTab = new MapPage();
		final Container mapPage = MapTab.getContentPane();
		MapTab.setBounds(0, 0, 800, 500);
		mapPage.setBounds(0, 0, 800, 500);
		parent.add(mapPage, JLayeredPane.PALETTE_LAYER);
		mapPage.setVisible(true);

		// Initialize register and login pages
		registerTab = new registerPage();
		final Container registerPage = registerTab.getContentPane();
		registerTab.setVisible(false);

		loginTab = new loginPage();
		final Container loginPage = loginTab.getContentPane();
		loginTab.setVisible(false);

		// make the register/login window
		registerApp.setPreferredSize(new Dimension(600, 375));
		registerApp.setSize(new Dimension(600, 375));
		registerApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		registerApp.add(registerPage);
		registerApp.add(loginPage);

		registerApp.setVisible(true);

		registerPage.setVisible(false);
		loginPage.setVisible(true);

		// make the main app when the user logins

		if (loggedIn) {
			mainApp.setVisible(false);
			mainApp.setSize(800, 500);
			mainApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainApp.pack();

		}

		// Button Functionality

		// return to mpa page after pressing "back" on profile
		ProfileTab.back.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				profilePage.setVisible(false);
				mapPage.setVisible(true);
			}

		});

		// button to go to profile screen
		MapTab.ProfileBut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mapPage.setVisible(false);
				profilePage.setVisible(true);
				try {
					ProfileTab.getProfileInfo(currentUser);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
				mainApp.repaint();
			}

		});

		// switch between login and register page
		loginTab.registerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				registerPage.setVisible(true);
				loginPage.setVisible(false);

			}

		});

		// send login information to server for authentication
		loginTab.submitBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (loginTab.AuthLogin(loginTab.User, loginTab.Pass)) {
						registerApp.setVisible(false);
						mainApp.setVisible(true);
						currentUser = loginTab.getCurrentUser();

					}
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
			}

		});

		// dynamic "back" button for register screen
		registerTab.back.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (registerTab.getCurrentCard().getName() == "INTRO") {
					registerPage.setVisible(false);
					loginPage.setVisible(true);
				}
			}

		});

		// dynamic "next" button for register screen
		registerTab.next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Boolean added = false;
				
				if (registerTab.getCurrentCard().getName() == "step3") {
					
					try {
						added = registerTab.POSTmethod(registerTab.username, registerTab.password, registerTab.streetNo, registerTab.streetName, registerTab.suburb, registerTab.state);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (SQLException e1) {
						e1.printStackTrace();
					} catch (NumberFormatException e1) {
						e1.printStackTrace();
					} catch (NoSuchAlgorithmException e1) {
						e1.printStackTrace();
					}
					 if (added) {
					registerPage.setVisible(false);
					loginPage.setVisible(true);
					 } else {
						 return;
					 }
				}
			}

		});

		// button to upload hazard to database
		MapTab.inputPanel.submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					MapTab.inputPanel.publishHazard(MapTab.inputPanel.titleLabel.getText(),
							MapTab.inputPanel.hazardType.getText(),
							MapTab.mapPins.get(MapTab.mapPins.size() - 1).getLat(),
							MapTab.mapPins.get(MapTab.mapPins.size() - 1).getLon(),
							MapTab.inputPanel.severitySlider.getValue(), currentUser,
							MapTab.inputPanel.InfoDesc.getText());

					MapTab.inputPanel.setVisible(false);
					MapTab.getPins();
				} catch (ClassNotFoundException | SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

	}
}
