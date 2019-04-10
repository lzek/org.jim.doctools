package org.jim.doctools.ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JFrame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SwingUI {
	static Logger log = LogManager.getLogger(SwingUI.class);
	private static int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width - 300;
	private static int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height - 200;
	private static int port = 8980;
	private static String host = "localhost";
	private static String startPage = "index.html";
	private static String url = "";
	private static String urlStart = "http://";

	private static void initParam(String[] args) {
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "--host":
				if ((i + 1) < args.length) {
					host = args[++i];
				}
				break;
			case "--port":
				if ((i + 1) < args.length) {
					port = Integer.parseInt(args[++i]);
				}
				break;
			case "--width":
				if ((i + 1) < args.length) {
					WIDTH = Integer.parseInt(args[++i]);
				}
				break;
			case "--height":
				if ((i + 1) < args.length) {
					HEIGHT = Integer.parseInt(args[++i]);
				}
				break;
			case "--url":
				if ((i + 1) < args.length) {
					url = args[++i];
				}
				break;
			case "--startPage":
				if ((i + 1) < args.length) {
					startPage = args[++i];
				}
				break;
			case "--urlStart":
				if ((i + 1) < args.length) {
					urlStart = args[++i];
				}
				break;
			case "-h":
				System.out.println(
						"myswing   COMMAND Format:\n--width xxx\n--height xxx\n--url xxx\n--startPage xxx\n--urlStart xxx");
				break;
			default:
				break;
			}
		}
		if (!(url.toLowerCase().trim().startsWith("http://") || url.toLowerCase().trim().startsWith("https://"))) {
			url = urlStart + host + ":" + port + "/" + startPage;
		}
	}

	/**
	 * m
	 * 
	 * @param args
	 */
	@SuppressWarnings("restriction")
	public static void launch(String[] args) {
		initParam(args);

		JFrame frame = new JFrame("试题管理，也可以通过浏览器访问：" + url);
		final JFXPanel webBrowser = new JFXPanel();
		frame.setLayout(new BorderLayout());
		frame.add(webBrowser, BorderLayout.CENTER);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Group root = new Group();
				Scene scene = new Scene(root, WIDTH, HEIGHT);
				webBrowser.setScene(scene);
				Double widthDouble = new Integer(WIDTH).doubleValue();
				Double heightDouble = new Integer(HEIGHT).doubleValue();

				VBox box = new VBox(10);
				// HBox urlBox = new HBox(10);
				final TextField urlTextField = new TextField();
				urlTextField.setText(url);
				// Button go = new Button("go");
				urlTextField.setPrefWidth(WIDTH - 80);
				// urlBox.getChildren().addAll(urlTextField, go);

				WebView view = new WebView();
				view.setMinSize(widthDouble, heightDouble);
				view.setPrefSize(widthDouble, heightDouble);
				final WebEngine eng = view.getEngine();
				eng.load(url);
				root.getChildren().add(view);

				// urlBox.setVisible(false);

				// box.getChildren().add(urlBox);
				box.getChildren().add(view);
				root.getChildren().add(box);

				/*
				 * go.setOnAction(new EventHandler<ActionEvent>() {
				 * 
				 * @Override public void handle(ActionEvent event) { if
				 * (!urlTextField.getText().startsWith(urlStart)) { eng.load(urlStart +
				 * urlTextField.getText()); } else { eng.load(urlTextField.getText()); } } });
				 */
			}
		});

		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocation((screenWidth - WIDTH) / 2, (screenHeight - HEIGHT) / 2);
		frame.setVisible(true);
	}

}
