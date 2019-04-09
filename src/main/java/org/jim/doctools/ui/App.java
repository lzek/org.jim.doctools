package org.jim.doctools.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class App {
	static Logger log = LogManager.getLogger(App.class);
	
	public static void main(String [] args) throws Exception {
		JettySite server=new JettySite("Jetty Thread-1");
		server.start(args);

		SwingUI.launch(args);
		//myswt.go(args);
	}
	
	
	
}
