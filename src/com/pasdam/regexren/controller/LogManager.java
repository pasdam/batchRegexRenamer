package com.pasdam.regexren.controller;

import com.pasdam.utils.log.FileLogger;
import com.pasdam.utils.log.Logger;
import com.pasdam.utils.log.StandardInputErrorLogger;

/**
 * {@link Logger} manager, it forward event messages to the logger
 * 
 * @author paco
 * @version 0.1
 */
@SuppressWarnings("unused")
public class LogManager {

	/** Indicates whether the logger is enabled or not */
	public static final boolean ENABLED = true;

	// Log levels
	private static final int TRACE 	 = 0;
	private static final int DEBUG 	 = TRACE + 1;
	private static final int INFO 	 = DEBUG + 1;
	private static final int WARNING = INFO + 1;
	private static final int ERROR   = WARNING + 1;
	private static final int FATAL 	 = ERROR + 1;
	private static final int NONE 	 = FATAL + 1;
	
	/** Current log level */
	private static final int CURRENT_LEVEL = TRACE;
	
	/** Log file used by {@link FileLogger} */
	private static final String LOG_FILE_PATH = "app.log";
	
	/** Logger instance */
	private static final Logger LOG = new StandardInputErrorLogger();
	
	/** Private constructor: it prevent class instantiation */
	private LogManager() {}
	
	/**
	 * Log a fatal event
	 * 
	 * @param message
	 *            message to log
	 */
	public static void fatal(String message) {
		if (ENABLED && CURRENT_LEVEL <= FATAL) {
			LOG.fatal(message);
		}
	}
	
	/**
	 * Log an error event
	 * 
	 * @param message
	 *            message to log
	 */
	public static void error(String message) {
		if (ENABLED && CURRENT_LEVEL <= ERROR) {
			LOG.error(message);
		}
	}
	
	/**
	 * Log a warning event
	 * 
	 * @param message
	 *            message to log
	 */
	public static void warning(String message) {
		if (ENABLED && CURRENT_LEVEL <= WARNING) {
			LOG.warning(message);
		}
	}
	
	/**
	 * Log an info event
	 * 
	 * @param message
	 *            message to log
	 */
	public static void info(String message) {
		if (ENABLED && CURRENT_LEVEL <= INFO) {
			LOG.info(message);
		}
	}
	
	/**
	 * Log an debug event
	 * 
	 * @param message
	 *            message to log
	 */
	public static void debug(String message) {
		if (ENABLED && CURRENT_LEVEL <= DEBUG) {
			LOG.debug(message);
		}
	}
	
	/**
	 * Log a trace event
	 * 
	 * @param message
	 *            message to log
	 */
	public static void trace(String message) {
		if (ENABLED && CURRENT_LEVEL <= TRACE) {
			LOG.trace(message);
		}
	}
}
