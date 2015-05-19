package launch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import pois.GPSTrace;
import checkIns.CheckIn;
import checkIns.CreateChkIn;
import checkIns.User;
import db.DBconnector;

public class Generator {

	private static List<User> users = new ArrayList<User>();

	public static void main(String[] args) {

		// Create command line parameters
		Options options = new Options();
		//Number of users created
		options.addOption("userNum", true, "Number of users created");
		//Gauss parameters for check-in's per day
		options.addOption("chkNumMean", true, "Mean of Gauss "
				+ "that determines the number of a user's check-in's per day");
		options.addOption("chkNumStDev", true, "Standard Deviation of Gauss "
				+ "that determines the number of a user's check-in's per day");
		//Max distance between check-in's
		options.addOption("dist", true,
				"Number of maximum diastance a user can walk between check-in's");
		//How many hours will the user stay at each poi?
		options.addOption("chkDurMean", true, "Mean of Gauss "
				+ "that determines the duration of each user's check-in per day");
		options.addOption("chkDurStDev", true, "Standard Deviation of Gauss "
				+ "that determines the duration of each user's check-in per day");
		//Start and end time of day
		options.addOption("startTime", true, "Time for the first check-in of the day ");
		options.addOption("endTime", true, "Time for the last check-in of the day ");
		
		// Parse command line parameters
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Integer userNum = Integer.parseInt(cmd.getOptionValue("userNum"));
		Integer chkNumMean = Integer.parseInt(cmd.getOptionValue("chkNumMean"));
		Integer chkNumStDev = Integer.parseInt(cmd
				.getOptionValue("chkNumStDev"));
		Double dist = Double.parseDouble(cmd.getOptionValue("dist"));
		Double chkDurMean = Double.parseDouble(cmd.getOptionValue("chkDurMean"));
		Double chkDurStDev = Double.parseDouble(cmd.getOptionValue("chkDurStDev"));
		Integer startTime = Integer.parseInt(cmd.getOptionValue("startTime"));
		Integer endTime = Integer.parseInt(cmd.getOptionValue("endTime"));

		// Number of pois in DB
		DBconnector db = new DBconnector();
		db.connect();
		int poisNum = db.getPoisNum();

		// For each user create their check-in's
		for (int i = 1; i <= userNum; i++) {

			User usr = new User(i);
			users.add(usr);
			System.out.println("-------------User no." + i + "-------------");
			CreateChkIn crChk = new CreateChkIn();
			// how many check-in's per day?
			int checkNum = crChk.createGaussianRandom(chkNumMean, chkNumStDev);
			// Create daily check-in
			crChk.createDailyCheckIn(usr, checkNum, poisNum, db, dist, chkDurMean, chkDurStDev, startTime, endTime);
			//Print check-in's
			for (CheckIn chk : usr.getCheckIns()) {
				chk.print();
			}
			//Print GPS traces
			for (GPSTrace tr: usr.getTraces()) {
				tr.print();
			}
		}
	}

}
