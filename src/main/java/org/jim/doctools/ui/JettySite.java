package org.jim.doctools.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;

public class JettySite implements Runnable {
	private Thread t;
	private String threadName;
	private static Logger log = LogManager.getLogger(JettySite.class);

	private static String resourceBase = ".";
	private static String serviceBase = "/api";

	private static int WebPort = 8980;
	private static int IdleTimeout = 720000;
	// private static String DEFAULT_COMMAND_URL = "/commands";
	// private static String DEFAULT_ADDRESS = "0.0.0.0";

	public void start() {
		log.info("Starting " + threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public void start(String[] args) {
		initParam(args);
		log.info("Starting " + threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	private static void initParam(String[] args) {
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "--port":
				if ((i + 1) < args.length) {
					WebPort = Integer.parseInt(args[++i]);
				}
				break;
			case "--timeout":
				if ((i + 1) < args.length) {
					IdleTimeout = Integer.parseInt(args[++i]);
				}
				break;
			case "--base":
				if ((i + 1) < args.length) {
					resourceBase = args[++i];
				}
				break;
			case "--serviceBase":
				if ((i + 1) < args.length) {
					serviceBase = args[++i];
				}
				break;
			case "-h":
				System.out.println("jettyGo   COMMAND Format:\n--port  xxx\n--base  xxx\n--serviceBase  xxx");
				break;
			default:
				break;
			}
		}
	}

	public void run() {
		body();
	}

	public JettySite(String name) {
		threadName = name;
		log.info("Creating " + threadName);
	}

	private void body() {
		try {
			Go();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void Go() throws Exception {
		Server server = new Server(WebPort);
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		// resource_handler.setWelcomeFiles(new String[]{ "nihao.html" });
		// resource_handler.setWelcomeFiles(new String[]{ "/hello" });
		resource_handler.setResourceBase(resourceBase);

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(ServicesHandler.class, serviceBase);

		// HandlerList handlers = new HandlerList();
		resource_handler.setHandler(handler);
		// handlers.setHandlers(new Handler[] { resource_handler,new DefaultHandler()
		// });
		// server.setHandler(handlers);
		server.setHandler(resource_handler);

		server.setStopAtShutdown(true);
		server.start();
		server.join();
	}

}
