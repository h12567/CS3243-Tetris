import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLala {
	public static void main(String[] args) {
		try {
			Scanner scan;
			File file = new File("./log005.txt");
			scan = new Scanner(file);
			scan.next();

			FileWriter swarmWriter = new FileWriter("swarmInput.txt", true);
	        PrintWriter swarmPrintWriter = new PrintWriter(swarmWriter);

			while(scan.hasNext()) {
				String[] parts = scan.next().split(",");
				for (int j = 1; j <= Config.NO_OF_FEATURES; j++) {
					System.out.println(parts[j]);
		            swarmPrintWriter.print(parts[j] + " ");
	        	}
	        	swarmPrintWriter.print("\n");	
			}

	        swarmPrintWriter.flush();
	        swarmPrintWriter.close();

			
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}