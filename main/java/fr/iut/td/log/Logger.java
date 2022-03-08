package fr.iut.td.log;

import java.io.PrintStream;

public class Logger {
	private static Logger logger = null;
	PrintStream out;

	public Logger() {
		// param deu logg
		//System.out.println(System.getenv("LOGGERFILE"));
		out = System.out;
	}

	public static Logger getInstance() {
		if (logger == null) {
			logger = new Logger();
		}
		return logger;
	}

	public void println(String msg) {
		out.println(msg);
	}

	public static void info(String msg) {
		getInstance().println(msg);
	}
}
