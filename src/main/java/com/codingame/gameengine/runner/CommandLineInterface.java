package com.codingame.gameengine.runner;

import com.codingame.gameengine.runner.MultiplayerGameRunner;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.codingame.gameengine.runner.dto.GameResult;
import com.google.common.io.Files;

public class CommandLineInterface {
	public static void main(String[] args) {
		try {
			Options options = new Options();

			options.addOption("h", false, "Print the help")
			       .addOption("p1", true, "Required. Player 1 command line.")
			       .addOption("p2", true, "Required. Player 2 command line.")
			       .addOption("s", false, "Server mode")
			       .addOption("l", true, "File output for logs")
			       .addOption("d", false, "Referee initial data");

			CommandLine cmd = new DefaultParser().parse(options, args);

			if (cmd.hasOption("h") || !cmd.hasOption("p1") || !cmd.hasOption("p2")) {
				new HelpFormatter().printHelp(
						"-p1 <player1 command line> -p2 <player2 command line> [-s -l <File output for logs>]",
						options);
				System.exit(0);
			}

	        MultiplayerGameRunner runner = new MultiplayerGameRunner();

			Field getGameResult = GameRunner.class.getDeclaredField("gameResult");
			getGameResult.setAccessible(true);
			GameResult result = (GameResult) getGameResult.get(runner);

			if (cmd.hasOption("d")) {
    			runner.setSeed(Long.valueOf(cmd.getOptionValue("d")));
			}

			int playerCount = 0;

            /*
			for (int i = 1; i <= 2; ++i) {
				if (cmd.hasOption("p" + i)) {
					runner.addAgent(cmd.getOptionValue("p" + i),cmd.getOptionValue("p" + i), "https://static.codingame.com/servlet/fileservlet?id=43829808065962");
					playerCount += 1;
				}
			}*/
		    runner.addAgent(cmd.getOptionValue("p1"),cmd.getOptionValue("p1"), "https://static.codingame.com/servlet/fileservlet?id=43829808065962");
		    runner.addAgent(cmd.getOptionValue("p2"),cmd.getOptionValue("p2"), "https://static.codingame.com/servlet/fileservlet?id=43829821541064");

            Properties params = new Properties();
            runner.setGameParameters(params);
            runner.setLeagueLevel(3);

			if (cmd.hasOption("s")) {
				runner.start();
			} else {
				Method initialize = GameRunner.class.getDeclaredMethod("initialize", Properties.class);
				initialize.setAccessible(true);
				initialize.invoke(runner, new Properties());

				Method runAgents = GameRunner.class.getDeclaredMethod("runAgents");
				runAgents.setAccessible(true);
				runAgents.invoke(runner);

				if (cmd.hasOption("l")) {
					Method getJSONResult = GameRunner.class.getDeclaredMethod("getJSONResult");
					getJSONResult.setAccessible(true);

					Files.asCharSink(Paths.get(cmd.getOptionValue("l")).toFile(), Charset.defaultCharset())
							.write((String) getJSONResult.invoke(runner));
				}

				for (int i = 0; i < playerCount; ++i) {
					System.out.println(result.scores.get(i));
				}

				for (String line : result.uinput) {
					System.out.println(line);
				}
			}

			// We have to clean players process properly
			Field getPlayers = GameRunner.class.getDeclaredField("players");
			getPlayers.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<Agent> players = (List<Agent>) getPlayers.get(runner);

			if (players != null) {
				for (Agent player : players) {
					Field getProcess = CommandLinePlayerAgent.class.getDeclaredField("process");
					getProcess.setAccessible(true);
					Process process = (Process) getProcess.get(player);

					process.destroy();
				}
			}
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

}