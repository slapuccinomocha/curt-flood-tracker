package curt;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.*;

// Panel that will pop up on right side
public class InfoPanel extends JPanel {

	JTextField titleLabel;
	JTextField hazardType;
	JSlider severitySlider;
	JTextArea InfoDesc;

	JLabel reportInfo;
	JButton submit;
	JLabel hazardTypeLabel;
	JLabel severityLabel;

	// SQL Commands
	static Connection conn;

	static String selectAll = "SELECT * FROM users;";

	// url to connect to database
	static String url = "jdbc:mysql://35.189.31.91/flood-tracker";

	// Username and password for the user
	static String DBusername = "Admin";
	static String DBpassword = "jewfit-wezfAx-1tiwhe";

	public JTextField getTitleLabel() {
		return titleLabel;
	}

	public void setTitleLabel(JTextField titleLabel) {
		this.titleLabel = titleLabel;
	}

	public JTextField getHazardType() {
		return hazardType;
	}

	public void setHazardType(JTextField hazardType) {
		this.hazardType = hazardType;
	}

	public JSlider getSeveritySlider() {
		return severitySlider;
	}

	public void setSeveritySlider(JSlider severitySlider) {
		this.severitySlider = severitySlider;
	}

	public JTextArea getInfoDesc() {
		return InfoDesc;
	}

	public void setInfoDesc(JTextArea infoDesc) {
		InfoDesc = infoDesc;
	}

	private static final long serialVersionUID = 1L;

	// once the submit button is pressed, this function will be called to add the
	// hazard to the database
	public void publishHazard(String name, String type, double lat, double lon, int severity, String username,
			String description) throws ClassNotFoundException, SQLException {
		boolean valid = true;
		
		char[] ch = name.toCharArray();
		for(char c : ch) {
			if(Character.isDigit(c)) {
				valid = false;
			}
		}
		
		ch = type.toCharArray();
		for(char c : ch) {
			if(Character.isDigit(c)) {
				valid = false;
			}
		}
		
		if (valid == false) {
			JOptionPane.showMessageDialog(new JFrame(), "Names and types must not contain numbers, please.");
			return;
		}
		
		Connection conn;

		BigDecimal Lat = new BigDecimal(lat);
		BigDecimal Lon = new BigDecimal(lon);
		float severityFl = (float) severity;
		java.util.Date date = new java.util.Date();
		java.sql.Date dateSql = new Date(date.getTime());
		System.out.println("Date: " + dateSql);

		java.sql.Time timeSql = new java.sql.Time(date.getTime());
		System.out.println("Time: " + timeSql);

		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(url, DBusername, DBpassword);

		String insertHazard = "INSERT INTO hazards(disasterName, disasterType, latitude, longitude, severity, dateReported, timeReported, userReported, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
		String updateReports = "UPDATE users SET NoOfReports = NoOfReports + 1 WHERE username=?";

		PreparedStatement insertStmt = conn.prepareStatement(insertHazard);
		PreparedStatement updateStmt = conn.prepareStatement(updateReports);

		insertStmt.setString(1, name);
		insertStmt.setString(2, type);
		insertStmt.setBigDecimal(3, Lat);
		insertStmt.setBigDecimal(4, Lon);
		insertStmt.setFloat(5, severityFl);
		insertStmt.setDate(6, dateSql);
		insertStmt.setTime(7, timeSql);
		insertStmt.setString(8, username);
		insertStmt.setString(9, description);

		updateStmt.setString(1, username);

		updateStmt.executeUpdate();
		insertStmt.executeUpdate();

		conn.close();
	}

	public InfoPanel(String username) throws FontFormatException, IOException {

		// fonts
		Font quicksand = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("Quicksand_Book.otf"));
		Font walkway = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("Walkway_Bold.ttf"));
		Font antipasto = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("Antipasto-Regular.ttf"));

		SpringLayout InfoLayout = new SpringLayout();

		setLayout(InfoLayout);
		setBackground(Color.white);
		;
		setOpaque(true);
		setBounds(500, 70, 280, 390);
		setOpaque(false);
		setBorder(new RoundedBorder(2, 10, "#ffffff"));
		setVisible(false);
		setAlignmentX(LEFT_ALIGNMENT);

		// title
		titleLabel = new JTextField("Title...", 10);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
		titleLabel.setFont(walkway.deriveFont(25f));
		titleLabel.setForeground(Color.decode("#000000"));
		titleLabel.setOpaque(false);

		// type of hazard
		hazardTypeLabel = new JLabel("Type of Hazard:");
		hazardTypeLabel.setBorder(
				BorderFactory.createCompoundBorder((BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK)),
						(BorderFactory.createEmptyBorder(10, 15, 0, 100))));
		hazardTypeLabel.setFont(antipasto.deriveFont(20f));
		hazardTypeLabel.setForeground(Color.decode("#000000"));
		hazardTypeLabel.setOpaque(false);

		hazardType = new JTextField("Type...", 10);
		hazardType.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
		hazardType.setFont(quicksand.deriveFont(12f));
		hazardType.setForeground(Color.BLACK);
		hazardType.setOpaque(false);

		// severity
		severityLabel = new JLabel("Severity: ");
		severityLabel.setBorder(
				BorderFactory.createCompoundBorder((BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK)),
						(BorderFactory.createEmptyBorder(10, 15, 0, 156))));
		severityLabel.setFont(antipasto.deriveFont(20f));
		severityLabel.setForeground(Color.decode("#000000"));
		severityLabel.setOpaque(false);

		severitySlider = new JSlider();
		severitySlider.setUI(new gradientSlider(severitySlider));
		severitySlider.setOpaque(false);

		// description
		JLabel MoreInfo = new JLabel("More Info: ");
		MoreInfo.setBorder(
				BorderFactory.createCompoundBorder((BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK)),
						(BorderFactory.createEmptyBorder(10, 15, 0, 145))));
		MoreInfo.setFont(antipasto.deriveFont(20f));
		MoreInfo.setForeground(Color.decode("#000000"));
		MoreInfo.setOpaque(false);

		InfoDesc = new JTextArea("Description...", 4, 17);
		InfoDesc.setLineWrap(true);
		InfoDesc.setFont(quicksand.deriveFont(12f));
		InfoDesc.setForeground(Color.BLACK);
		InfoDesc.setOpaque(false);

		JScrollPane InfoDescScroll = new JScrollPane(InfoDesc);
		InfoDescScroll.setBorder(BorderFactory.createCompoundBorder((BorderFactory.createEmptyBorder(0, 25, 0, 0)),
				(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#ffffff")))));

		// Reporter info
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		formatter.format(time);

		reportInfo = new JLabel("Reported at " + formatter.format(time) + ", " + date + " by " + username);
		reportInfo.setBorder(
				BorderFactory.createCompoundBorder((BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK)),
						(BorderFactory.createEmptyBorder(10, 10, 0, 125))));
		reportInfo.setFont(quicksand.deriveFont(8f));
		reportInfo.setForeground(Color.BLACK);
		reportInfo.setVisible(true);

		// submit button, for reporting hazards
		submit = new JButton("Submit");
		submit.setFont(antipasto.deriveFont(20f));
		submit.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
		submit.setVisible(false);

		// formatting and laying out UI
		InfoLayout.putConstraint(SpringLayout.NORTH, titleLabel, 10, SpringLayout.NORTH, this);
		InfoLayout.putConstraint(SpringLayout.NORTH, hazardTypeLabel, 15, SpringLayout.SOUTH, titleLabel);
		InfoLayout.putConstraint(SpringLayout.NORTH, hazardType, 8, SpringLayout.SOUTH, hazardTypeLabel);
		InfoLayout.putConstraint(SpringLayout.NORTH, severityLabel, 10, SpringLayout.SOUTH, hazardType);
		InfoLayout.putConstraint(SpringLayout.NORTH, severitySlider, 10, SpringLayout.SOUTH, severityLabel);
		InfoLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, severitySlider, 0, SpringLayout.HORIZONTAL_CENTER,
				this);
		InfoLayout.putConstraint(SpringLayout.NORTH, MoreInfo, 20, SpringLayout.SOUTH, severitySlider);
		InfoLayout.putConstraint(SpringLayout.NORTH, InfoDescScroll, 8, SpringLayout.SOUTH, MoreInfo);
		InfoLayout.putConstraint(SpringLayout.NORTH, reportInfo, 20, SpringLayout.SOUTH, InfoDescScroll);
		InfoLayout.putConstraint(SpringLayout.NORTH, submit, 10, SpringLayout.SOUTH, InfoDescScroll);
		InfoLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, submit, 0, SpringLayout.HORIZONTAL_CENTER, this);

		add(titleLabel);
		add(hazardTypeLabel);
		add(hazardType);
		add(severityLabel);
		add(severitySlider);
		add(MoreInfo);
		add(InfoDescScroll);
		add(reportInfo);
		add(submit);
	}
}
