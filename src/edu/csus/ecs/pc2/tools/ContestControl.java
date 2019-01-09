package edu.csus.ecs.pc2.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.ClientUtility;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.Plugin;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Contest Control
 * 
 */
public class ContestControl {

	private static final String START_AT_OPTION_STRING = "--startat";

	private static final String START_IN_OPTION_STRING = "--startin";

	// TODO move these general options to AppConstants
	public static final String HELP_OPTION_STRING = "--help";

	public static final String DEBUG_OPTION_STRING = "--debug";

	public static final String LOGIN_OPTION_STRING = "--login";

	public static final String PASSWORD_OPTION_STRING = "--password";

	private static void usage() {

		String[] usage = {
				//
				"Usage: ContestControl [--help] --login LOGIN [--password PASSWORD] --startnow | --shutdown | --startat DATETIME | --startin TIMESTR ", //
				"Purpose: to perform various pc2 operations/controles", "", //

				"--login LOGIN ", //
				"--password PASSWORD ", //
				"", //

				"--startat DATETIME - DATETIME can be a date/time in one of these forms: ", //
				"                      HH:mm:ss, HH:mm, MM MM MM yyyy-MM-dd HH:mm:ss, yyyy-MM-dd HH:mm", //
				"--startin TIMESTR  - start in seconds or minutes, TIMESTR = #### | ####min", //
				"--shutdown         - shtudown all servers on all sites now.", //
				"--startnow         - start contest now", //
				"", //
		};

		for (String s : usage) {
			System.out.println(s);
		}

		String[] multiLineVersion = new VersionInfo().getSystemVersionInfoMultiLine();
		for (String string : multiLineVersion) {
			System.out.println(string);
		}
	}

	// TODO REFACTOR move to Utilities class
	public static void printArray(List<String> list) {
		for (String string : list) {
			System.out.println(string);
		}
	}

	public static void main(String[] args) {

		String[] requiredArgs = { LOGIN_OPTION_STRING, PASSWORD_OPTION_STRING, START_IN_OPTION_STRING,
				START_AT_OPTION_STRING };

		ParseArguments parseArguments = new ParseArguments(args, requiredArgs);

		if (args.length == 0 || parseArguments.isOptPresent("--help")) {
			usage();

		} else if (parseArguments.isOptPresent("--startnow")) {

			checkForMissingOptionAndValue(parseArguments, LOGIN_OPTION_STRING);

			new ContestControl().startNow(parseArguments);

		} else if (parseArguments.isOptPresent("--shutdown")) {

			checkForMissingOptionAndValue(parseArguments, LOGIN_OPTION_STRING);

			new ContestControl().shutdownAllSites(parseArguments);

		} else if (parseArguments.isOptPresent(START_IN_OPTION_STRING)) {

			checkForMissingOptionAndValue(parseArguments, LOGIN_OPTION_STRING);
			checkForMissingOptionAndValue(parseArguments, START_IN_OPTION_STRING);

			String timeString = parseArguments.getOptValue(START_IN_OPTION_STRING);

			new ContestControl().startIn(parseArguments, timeString);

		} else if (parseArguments.isOptPresent(START_AT_OPTION_STRING)) {

			checkForMissingOptionAndValue(parseArguments, LOGIN_OPTION_STRING);
			checkForMissingOptionAndValue(parseArguments, START_IN_OPTION_STRING);

			String timeString = parseArguments.getOptValue(START_IN_OPTION_STRING);

			new ContestControl().startAt(parseArguments, timeString);

		} else {
			System.out.println("Missing command line options " + String.join(" ", args));
			System.exit(4);
		}
	}

	private void shutdownAllSites(ParseArguments parseArguments) {

		Plugin info = logInToContest(parseArguments);
		info.getController().sendShutdownAllSites();

		sleep(500);
		System.exit(0);

	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	private void startNow(ParseArguments parseArguments) {

		try {
			Plugin info = logInToContest(parseArguments);
			info.getController().startAllContestTimes();
		} catch (Exception e) {
			System.out.println("Error --startnow, " + e.getMessage());
			Utilities.printStackTrace(System.err, e, "csus");
		}

		sleep(500);
		System.exit(0);

	}

	/**
	 * Start contest clock at in a given time
	 * 
	 * @param parseArguments
	 * @param timeString
	 */
	private void startIn(ParseArguments parseArguments, String timeString) {

		try {

			Plugin info = logInToContest(parseArguments);

			IInternalContest contest = info.getContest();
			ContestTime time = contest.getContestTime();

			if (time.isContestRunning()) {
				System.out.println("Contest clock already running.");
			} else {

				Date date = createDateInFuture(timeString);

				if (date == null) {
					fatalError("Invalid start at time description '" + timeString + "'");
				} else {

				}

				Date now = new Date();

				if (now.getTime() < date.getTime()) {
					System.out.println("Start at time " + date + " is not in the future (current time=" + now + ")");
					fatalError("Start at time not in future");
				}

				ContestInformation contestInformation = contest.getContestInformation();
				contestInformation.setScheduledStartDate(date);
				contestInformation.setAutoStartContest(true);
				info.getController().updateContestInformation(contestInformation);

			}

		} catch (Exception e) {
			System.out.println("Error --startin, " + e.getMessage());
			Utilities.printStackTrace(System.err, e, "csus");
		}

		sleep(500);
		System.exit(0);

	}

	/**
	 * DAte in future using relative timeString
	 * 
	 * @param relativeTimeString
	 *            either seconds in digits or ###min for minutes
	 * @return
	 */
	protected Date createDateInFuture(String relativeTimeString) {

		Date date = null;
		long curSeconds = new Date().getTime();

		if (relativeTimeString.matches("\\d+")) {
			// digits, assume seconds
			curSeconds += Integer.parseInt(relativeTimeString);
			date = new Date(curSeconds);
		} else if (relativeTimeString.matches("(\\d+)min")) {
			curSeconds += (60 * Integer.parseInt(relativeTimeString.substring(0, relativeTimeString.length() - 3)));
			date = new Date(curSeconds);
		}

		return date;
	}

	/**
	 * Start Contest At time
	 * 
	 * @param parseArguments
	 * @param timeString
	 */
	private void startAt(ParseArguments parseArguments, String timeString) {

		try {

			Date date = parseDatetime(timeString);

			if (date == null) {
				fatalError("Unable to parse date/time '" + timeString + "', try form: HH:mm or yyyy-MM-dd HH:mm");
			}

			Plugin info = logInToContest(parseArguments);

			IInternalContest contest = info.getContest();

			ContestInformation contestInformation = contest.getContestInformation();

			ContestTime conTime = contest.getContestTime();

			if (conTime.isContestRunning()) {
				System.err.println("Contest already started, no need to start contest again.");
				System.exit(4);

			} else {

				contestInformation.setScheduledStartDate(date);
				contestInformation.setAutoStartContest(true);
				info.getController().updateContestInformation(contestInformation);

				System.out.println("Contest will be automatically started at: " + date);

				sleep(500);
				System.exit(0);
			}
		} catch (Exception e) {
			System.err.println("Potential problem setting contest start time");
			e.printStackTrace();
		}

	}

	/**
	 * Return date if string matches a date/time pattern.
	 * 
	 * The following patterns are tried to parse the input time string.
	 * 
	 * <code>HH:mm:ss, HH:mm, MM MM MM yyyy-MM-dd HH:mm:ss, yyyy-MM-dd HH:mm </code>
	 * 
	 * @param timeString
	 * @return null if cannot be parsed.
	 */
	private Date parseDatetime(String timeString) {

		String[] patterns = {
				//
				"HH:mm:ss", // 24HH:MM:ss
				"HH:mm", //

				"MM/dd/yyyy HH:mm:ss", //
				"MM/dd/yyyy HH:mm", //
				"MM/dd/yy HH:mm", //

				"yyyy-MM-dd HH:mm:ss", //
				"yyyy-MM-dd HH:mm", //

		};

		for (String pattern : patterns) {
			try {
				SimpleDateFormat parser = new SimpleDateFormat(pattern);
				parser.setLenient(false);
				Date date = parser.parse(timeString);
				if (date != null) {
					return date;
				}
			} catch (ParseException e) {
				// ignore, try next pattern
			}
		}

		return null;

	}

	/**
	 * Attempt to login into a contest.
	 * 
	 * @param parseArguments
	 * @return plugin which has contest and controller.
	 */
	private Plugin logInToContest(ParseArguments parseArguments) {
		try {
			String login = parseArguments.getOptValue(LOGIN_OPTION_STRING);
			String password = parseArguments.getOptValue(PASSWORD_OPTION_STRING);

			ClientId clientId = InternalController.loginShortcutExpansion(1, login);
			login = clientId.getName();
			if (password == null) {
				password = login;
			}

			Plugin loginInfo = ClientUtility.logInToContest(login, password);
			return loginInfo;

		} catch (Exception e) {
			fatalError("Unable to login into server " + e.getMessage(), e);
		}
		return null;
	}

	private void fatalError(String message, Exception e) {
		Utilities.printStackTrace(System.err, e, "csus");
		fatalError(message);
	}

	private static void fatalError(String message) {

		// TODO output to log

		System.err.println("Program halted: " + message);
		System.exit(12);
	}

	/**
	 * Checks for missing option and value, fatal error is either missing.
	 * 
	 * @param parseArguments
	 * @param option
	 */
	private static void checkForMissingOptionAndValue(ParseArguments parseArguments, String option) {

		if (!parseArguments.isOptPresent(option)) {
			fatalError("Missing required option: " + option);
		} else if (StringUtilities.isEmpty(parseArguments.getOptValue(option))) {
			fatalError("Missing expected value after option " + option);
		}
	}
}
