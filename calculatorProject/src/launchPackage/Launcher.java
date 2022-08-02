package launchPackage;

import frontendPackage.*;
import serverPackage.*;

public class Launcher {

	public static void main(String[] args) {
		if (args.length == 0) {
			new CalcGUICenter(null, 0);
		} else {
			if (args[0].strip().equals("-s")) {
				int port = 0;
				try {
					port = Integer.parseInt(args[1].strip());
				} catch (Exception e) {
				}
				new CalcServer(port);
			}
			if (args[0].strip().equals("-l")) {
				String ip = "localhost";
				int port = 0;
				String portString = null;
				if (args.length == 2) {
					portString = args[1].strip();
				}
				if (args.length == 3) {
					ip = args[1].strip();
					portString = args[2].strip();
				}
				try {
					port = Integer.parseInt(portString);
				} catch (Exception e) {
				}
				new CalcGUICenter(ip, port);
			}
		}

	}

}
