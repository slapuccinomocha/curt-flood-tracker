package curt;

// Page that allows user to view map
// google maps platform, api key: AIzaSyBOWfkpm4hKBixrlnm0OztCcNfg8D0fRH4
import javax.imageio.ImageIO;

import javax.swing.*;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class MapPage extends JFrame {
	private static final long serialVersionUID = 1L;
	private JLayeredPane MapPage;
	private JPanel MapPanel;
	private JPanel SearchBar;
	private JPanel BurgerMenuFull;
	private JPanel BurgerButton;
	private JPanel ZoomButtons;
	private JPanel Dimmer;
	InfoPanel inputPanel;
	InfoPanel infoPanel;

	private String username = "Jimothy";

	// Map Panel Elements
	private JMapViewer map;
	private Coordinate School;
	private BufferedImage PinBuffered = ImageIO.read(new File("images/pin.png"));
	private Image pinImg = PinBuffered.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
	public List<MapPin> mapPins = new ArrayList<MapPin>();

	// Zoom elements
	private JButton ZoomIn;
	private JButton ZoomOut;
	private JPanel zoomInPanel = new JPanel();
	private JPanel zoomOutPanel = new JPanel();

	// Search Bar Elements
	private JTextField SearchTextEntry;
	// private JComboBox<Object> searchResults;
	private JList<Object> searchResults;
	private String[] results = { "syd", "qld", "new york", "San francisco" };
	//	private Coordinate searchCoord;

	private String glasspathtoimage = "images/m-glass.png";
	private BufferedImage searchImageBuffered;
	private ImageIcon searchImage;
	private JButton searchLogo;

	// Burger Menu
	private JButton closeMenu;
	private String closepathtoimage = "images/close_but.png";
	private JLabel profileIcon;
	private String profilepathtoimage = "images/defProPic.png";
	JButton ProfileBut;
	private JButton NearYouBut;
	private JButton LocInfoBut;
	private JButton HelpButton;
	private int menuPadding = 30;

	// Burger Menu Button
	private String burgerpathtoimage = "images/burger.png";
	private BufferedImage burgerImageBuffered;
	private ImageIcon BurgerImage;
	private JButton BurgerLogo;

	// SQL Commands
	static Connection conn;

	static String selectAll = "SELECT * FROM users;";

	// url to connect to database
	static String url = "jdbc:mysql://35.189.31.91/flood-tracker";

	// Username and password for database access
	static String DBusername = "Admin";
	static String DBpassword = "jewfit-wezfAx-1tiwhe";

	// retrieve all pins from database and display them on the map
	public void getPins() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(url, DBusername, DBpassword);

		// Select coordinates and IDs from database
		String getPinInfo = "SELECT ID, latitude, longitude from hazards";

		Statement getPinInfoStmt = conn.createStatement();
		ResultSet PinInfo = getPinInfoStmt.executeQuery(getPinInfo);

		double lat;
		double lon;
		int id;
		MapPin marker;

		// Place pins at all coordiantes
		while (PinInfo.next()) {
			id = PinInfo.getInt(1);
			lat = PinInfo.getDouble(2);
			lon = PinInfo.getDouble(3);
			marker = new MapPin(new Coordinate(lat, lon), pinImg, id);
			mapPins.add(marker);
			map.addMapMarker(marker);

		}

		conn.close();
	}

	public void beginPinRefresh() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(url, DBusername, DBpassword);
		java.util.TimerTask task = new java.util.TimerTask() {
			@Override
			public void run() {
				try {
					getPins();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		java.util.Timer timer = new java.util.Timer(true);// true to run timer as daemon thread
		timer.schedule(task, 0, 5000);// Run task every 5 second
		try {
			Thread.sleep(600000); // Cancel task after 1 minute.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timer.cancel();
		
		conn.close();
	}
	

	// Retrieve info for information panel on specific pins
	public void getInfoPanel(MapPin mapPin) throws SQLException, ClassNotFoundException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(url, DBusername, DBpassword);

		// Select pin from id in database
		String selectPin = "SELECT ID, disasterName, disasterType, latitude, longitude, severity, dateReported, timeReported, userReported, description from hazards WHERE id=?";

		PreparedStatement selectPinStatement = conn.prepareStatement(selectPin);
		selectPinStatement.setInt(1, mapPin.getId());
		ResultSet rs = selectPinStatement.executeQuery();

		String name = null;
		String type = null;
		int severity = 0;
		java.sql.Date date = null;
		java.sql.Time time = null;
		String userReported = null;
		String description = null;

		// Get info from pin and allocate each to variable
		if (rs.next()) {
			name = rs.getString(2);
			type = rs.getString(3);
			severity = rs.getInt(6);
			date = rs.getDate(7);
			time = rs.getTime(8);
			userReported = rs.getString(9);
			description = rs.getString(10);
		}

		// get panel template and allocate variables to elements (FOR loop)
		infoPanel.setVisible(false);
		infoPanel.titleLabel.setText(name);
		infoPanel.hazardType.setText(type);
		infoPanel.severitySlider.setValue(severity);
		infoPanel.InfoDesc.setText(description);
		infoPanel.reportInfo.setText("Reported On " + date + " at " + time + " by " + userReported);

		// show panel
		infoPanel.setVisible(true);
	}

	// add a pin once you click on the map
	public void addPin(MouseEvent e) throws IOException, FontFormatException {
		Point p = e.getPoint();
		int mouseX = p.x;
		int mouseY = p.y;
		double lat = map.getPosition(mouseX, mouseY).getLat();
		double lon = map.getPosition(mouseX, mouseY).getLon();

		// temporary pin before submission
		MapPin currentPin = new MapPin(new Coordinate(lat, lon), pinImg, 1);
		mapPins.add(currentPin);
		map.addMapMarker(mapPins.get(mapPins.size() - 1));
		map.setDisplayPosition(new Coordinate(lat, lon), 15);

	}

	// remove the temporary pin once you click off
	public void removePin() throws IOException, FontFormatException {
		map.removeMapMarker(mapPins.get(mapPins.size() - 1));
		mapPins.remove((mapPins.size() - 1));
		System.out.println(mapPins);
	}

	// constructor being called to setUp UI as soon as an object is made.
	public MapPage() throws IOException, FontFormatException, ClassNotFoundException, SQLException {

		setLayout(new BorderLayout());

		// Parent pane that holds everything
		MapPage = new JLayeredPane();
		MapPage.setLayout(null);
		MapPage.setSize(500, 500);
		MapPage.setBackground(Color.decode("#2c3e50"));
		MapPage.setOpaque(true);

		add(MapPage, BorderLayout.CENTER);

		// map pane that holds map from OpenStreetMaps
		MapPanel = new JPanel();
		MapPanel.setBackground(Color.decode("#f1f51f"));
		MapPanel.setLayout(new BorderLayout());
		MapPanel.setBounds(0, 0, 800, 500);

		map = new JMapViewer() {
			private static final long serialVersionUID = 1L;

			public void setZoom(int zoom, Point mapPoint) {
				if (zoom > tileController.getTileSource().getMaxZoom() || zoom < 5 || zoom == this.zoom)
					return;
				ICoordinate zoomPos = getPosition(mapPoint);
				tileController.cancelOutstandingJobs();
				// Clearing outstanding load requests
				setDisplayPosition(mapPoint, zoomPos, zoom);

			}
		};

		// loading the map
		TileSource mapSource = (TileSource) new BingAerialTileSource();
		mapSource.getMaxZoom();

		map.setTileSource(mapSource);
		School = new Coordinate(-33.70417, 150.89794);
		map.setDisplayPosition(School, 21);
		map.setVisible(true);
		map.setZoomControlsVisible(false);
		map.setMinimumSize(map.getSize());

		// get pins upon login
		getPins();

		MapPanel.add(map, BorderLayout.CENTER);

		MapPage.add(MapPanel, JLayeredPane.DEFAULT_LAYER);

		// Input Panel
		inputPanel = new InfoPanel(username);
		inputPanel.setBounds(500, 90, 256, 340);
		MapPage.add(inputPanel, JLayeredPane.PALETTE_LAYER);
		inputPanel.reportInfo.setVisible(false);
		inputPanel.submit.setVisible(true);
		inputPanel.setVisible(false);

		// Information Panel
		infoPanel = new InfoPanel(username);
		infoPanel.setBounds(500, 90, 256, 340);
		MapPage.add(infoPanel, JLayeredPane.PALETTE_LAYER);
		infoPanel.reportInfo.setVisible(true);
		infoPanel.submit.setVisible(false);
		infoPanel.setVisible(false);

		// Zoom buttons
		ZoomButtons = new JPanel();
		ZoomButtons.setOpaque(false);
		ZoomButtons.setLayout(null);
		ZoomButtons.setBounds(10, 430, 100, 100);

		ZoomOut = new JButton("-");
		ZoomOut.setForeground(Color.white);
		ZoomOut.setBorderPainted(false);
		ZoomOut.setContentAreaFilled(false);
		zoomOutPanel.setBackground(Color.decode("#2c3e50"));
		zoomOutPanel.setOpaque(true);
		zoomOutPanel.setBounds(0, 0, 40, 40);
		zoomOutPanel.add(ZoomOut);

		ZoomIn = new JButton("+");
		ZoomIn.setForeground(Color.white);
		ZoomIn.setBorderPainted(false);
		ZoomIn.setContentAreaFilled(false);
		zoomInPanel.setBackground(Color.decode("#2c3e50"));
		zoomInPanel.setOpaque(true);
		zoomInPanel.setBounds(50, 0, 40, 40);
		zoomInPanel.add(ZoomIn);

		ZoomButtons.add(zoomOutPanel);
		ZoomButtons.add(zoomInPanel);

		MapPage.add(ZoomButtons, JLayeredPane.PALETTE_LAYER);

		// Search Bar
		SearchBar = new JPanel();
		SearchBar.setOpaque(false);
		SearchBar.setLayout(new BoxLayout(SearchBar, BoxLayout.X_AXIS));
		SearchBar.setBounds(200, 10, 590, 50);
		SearchBar.setBorder(new RoundedBorder(5, 50, "#2c3e50"));

		searchResults = new JList<Object>(results);
		searchResults.setCellRenderer(new DefaultListCellRenderer());
		searchResults.setFixedCellHeight(45);
		searchResults.setVisibleRowCount(5);

		searchResults.setForeground(Color.decode("#ffffff"));
		searchResults.setBackground(Color.decode("#2c3e50"));
		searchResults.setOpaque(false);

		final JScrollPane scrollPane = new JScrollPane(searchResults);
		scrollPane.setBorder(new RoundedBorder(10, 20, "#2c3e50"));
		scrollPane.setBounds(240, 50, 520, 200);
		scrollPane.setOpaque(false);
		scrollPane.setBackground(Color.decode("#2c3e50"));
		scrollPane.setVisible(false);
		scrollPane.getVerticalScrollBar().setBackground(Color.BLACK);
		scrollPane.getViewport().setBackground(Color.PINK);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		SearchTextEntry = new JTextField("");
		SearchTextEntry.setOpaque(false);
		SearchTextEntry.setForeground(Color.decode("#ffffff"));
		SearchTextEntry.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		SearchTextEntry.setFont(new Font("Avenir", Font.PLAIN, 15));

		searchImageBuffered = ImageIO.read(new File(glasspathtoimage));
		Image searchResized = searchImageBuffered.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		searchImage = new ImageIcon(searchResized);
		searchLogo = new JButton(searchImage);
		searchLogo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

		SearchBar.add(searchLogo);
		SearchBar.add(SearchTextEntry);

		MapPage.add(scrollPane, JLayeredPane.PALETTE_LAYER);

		// Burger Menu Icon
		BurgerButton = new JPanel();
		BurgerButton.setBorder(new RoundedBorder(5, 200, "#2c3e50"));
		BurgerButton.setOpaque(false);
		BurgerButton.setBounds(10, 10, 50, 50);

		burgerImageBuffered = ImageIO.read(new File(burgerpathtoimage));
		Image burgerResized = burgerImageBuffered.getScaledInstance(15, 15, Image.SCALE_SMOOTH);
		BurgerImage = new ImageIcon(burgerResized);
		BurgerLogo = new JButton(BurgerImage);
		BurgerLogo.setBorderPainted(false);
		BurgerLogo.setContentAreaFilled(false);
		BurgerLogo.setBackground(null);
		BurgerButton.add(BurgerLogo);

		MapPage.add(BurgerButton, JLayeredPane.PALETTE_LAYER);

		// Burger Menu
		BurgerMenuFull = new JPanel();
		SpringLayout BurgerMenuLayout = new SpringLayout();
		BurgerMenuFull.setLayout(BurgerMenuLayout);
		BurgerMenuFull.setBackground(Color.decode("#2c3e50"));
		BurgerMenuFull.setVisible(false);
		BurgerMenuFull.setBounds(0, 0, 200, 500);

		BufferedImage closeImageBuffered = ImageIO.read(new File(closepathtoimage));
		Image closeResized = closeImageBuffered.getScaledInstance(15, 15, Image.SCALE_SMOOTH);
		ImageIcon closeImage = new ImageIcon(closeResized);

		closeMenu = new JButton(closeImage);
		closeMenu.setBorderPainted(false);
		closeMenu.setContentAreaFilled(false);
		closeMenu.setBackground(null);

		BurgerMenuLayout.putConstraint(SpringLayout.WEST, closeMenu, 5, SpringLayout.WEST, BurgerMenuFull);
		BurgerMenuLayout.putConstraint(SpringLayout.NORTH, closeMenu, 5, SpringLayout.NORTH, BurgerMenuFull);

		BurgerMenuFull.add(closeMenu);

		BufferedImage profileImageBuffered = ImageIO.read(new File(profilepathtoimage));
		Image profileResized = profileImageBuffered.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		ImageIcon profileImage = new ImageIcon(profileResized);

		profileIcon = new JLabel(profileImage);
		profileIcon.setOpaque(false);

		BurgerMenuLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, profileIcon, 1, SpringLayout.HORIZONTAL_CENTER,
				BurgerMenuFull);
		BurgerMenuLayout.putConstraint(SpringLayout.NORTH, profileIcon, 20, SpringLayout.SOUTH, closeMenu);

		BurgerMenuFull.add(profileIcon);

		NearYouBut = new JButton("Near You");
		NearYouBut.setBorderPainted(false);
		NearYouBut.setContentAreaFilled(false);
		NearYouBut.setBackground(null);
		NearYouBut.setForeground(Color.gray);

		BurgerMenuLayout.putConstraint(SpringLayout.NORTH, NearYouBut, menuPadding, SpringLayout.SOUTH, profileIcon);

		BurgerMenuFull.add(NearYouBut);

		LocInfoBut = new JButton("Location Settings");
		LocInfoBut.setBorderPainted(false);
		LocInfoBut.setContentAreaFilled(false);
		LocInfoBut.setBackground(null);
		LocInfoBut.setForeground(Color.gray);

		BurgerMenuLayout.putConstraint(SpringLayout.NORTH, LocInfoBut, menuPadding, SpringLayout.SOUTH, NearYouBut);

		BurgerMenuFull.add(LocInfoBut);

		HelpButton = new JButton("Help");
		HelpButton.setBorderPainted(false);
		HelpButton.setContentAreaFilled(false);
		HelpButton.setBackground(null);
		HelpButton.setForeground(Color.white);

		BurgerMenuLayout.putConstraint(SpringLayout.NORTH, HelpButton, menuPadding, SpringLayout.SOUTH, LocInfoBut);

		ProfileBut = new JButton("Your Profile");
		ProfileBut.setBorderPainted(false);
		ProfileBut.setContentAreaFilled(false);
		ProfileBut.setBackground(null);
		ProfileBut.setForeground(Color.WHITE);

		BurgerMenuLayout.putConstraint(SpringLayout.NORTH, ProfileBut, 130, SpringLayout.SOUTH, HelpButton);

		BurgerMenuFull.add(ProfileBut);

		BurgerMenuFull.add(HelpButton);

		// panel to dim map when viewing menu
		Dimmer = new JPanel() {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		Dimmer.setOpaque(false);
		Dimmer.setBackground(new Color(0, 0, 0, 70));
		Dimmer.setBounds(200, 0, 800, 500);
		Dimmer.setVisible(false);

		MapPage.add(Dimmer, JLayeredPane.MODAL_LAYER, 1);

		MapPage.add(BurgerMenuFull, JLayeredPane.MODAL_LAYER, 2);

		repaint();
		pack();


		// Button functionality
		ZoomOut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				map.zoomOut();
			}
		});

		ZoomIn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				map.zoomIn();
			}
		});

		// Unused function for search engine
		SearchTextEntry.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				scrollPane.setVisible(true);
			}

			@Override
			public void focusLost(FocusEvent e) {
				scrollPane.setVisible(false);
			}
		});

		// Unused function for search bar
		SearchTextEntry.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				scrollPane.setVisible(true);
			}
		});

		// close button for side menu
		closeMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BurgerMenuFull.setVisible(false);
				Dimmer.setVisible(false);
			}
		});

		// functionality for placing pins, removing unused pins, etc.
		map.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				scrollPane.setVisible(false);
				if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {

					try {
						if (inputPanel.isShowing()) {
							removePin();
							inputPanel.setVisible(false);

						}
					} catch (IOException | FontFormatException e1) {
						e1.printStackTrace();
					}
					inputPanel.setVisible(false);
					infoPanel.setVisible(false);

					Point p = e.getPoint();
					int X = p.x + 3;
					int Y = p.y + 3;
					List<MapMarker> ar = map.getMapMarkerList();
					Iterator<MapMarker> i = ar.iterator();
					while (i.hasNext()) {

						MapPin mapMarker = (MapPin) i.next();
						Point MarkerPosition = map.getMapPosition(mapMarker.getLat(), mapMarker.getLon());

						if (MarkerPosition != null) {

							int centerX = MarkerPosition.x;
							int centerY = MarkerPosition.y;

							// calculate the radius from the touch to the center of the dot
							double radCircle = Math
									.sqrt((((centerX - X) * (centerX - X)) + (centerY - Y) * (centerY - Y)));

							// if the radius is smaller then 23 (radius of a ball is 5), then it must be on
							// the dot
							if (radCircle < 35) {
								try {
									getInfoPanel(mapMarker);
								} catch (ClassNotFoundException | SQLException e1) {
									e1.printStackTrace();
								}
							}

						}
					}
				} else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

					try {
						if (!inputPanel.isShowing()) {
							addPin(e);
							inputPanel.setVisible(true);

						} else {
							removePin();
							inputPanel.setVisible(false);
						}
					} catch (IOException | FontFormatException e1) {
						e1.printStackTrace();
					}

				}
			}
		});

		// button to bring up side menu once clicked
		BurgerLogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BurgerMenuFull.setVisible(true);
				Dimmer.setVisible(true);
				try {
					getPins();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		HelpButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://singh-aryan.gitbook.io/curt-hazard-tracker/usage"));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
