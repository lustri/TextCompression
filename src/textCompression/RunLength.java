package textCompression;

import java.io.*;

public class RunLength {

	private static InputStreamReader input_txt; // File input.txt
	private static OutputStream output_bin; // File output.bin

	private static BufferedReader input_bin; // File input.bin
	private static FileWriter output_txt; // File output.txt

	private static String encode;
	private static String header;

	public static void doCompression(String file_in, String file_out, int bin)
			throws IOException {

		header = new String();

		if (bin == 0) {
			input_txt = new InputStreamReader(new FileInputStream(file_in)); // Open
																				// the
																				// input
																				// file
																				// txt
		} else {
			input_bin = new BufferedReader(new FileReader(file_in)); // Open the
																		// input
																		// file
																		// bin
			String aux = new String();
			while ((aux = input_bin.readLine() + "\n").contains("---") == false) {
				header += aux;
			}

		}

		output_bin = new FileOutputStream(new File(file_out));
		encode = new String();

		int c = 0, c2 = 0;

		if (bin == 0)
			c = input_txt.read();
		else
			c = input_bin.read();

		while (c2 != -1) { // Read the file

			int count = 1;

			if (bin == 0)
				c2 = input_txt.read();
			else
				c2 = input_bin.read();

			while (c2 != -1 && c == c2) { // RL
				count++;

				if (bin == 0)
					c2 = input_txt.read();
				else
					c2 = input_bin.read();
			}

			encode += "(" + count + "," + (char) c + ")";

			c = c2;
		}

		if (bin == 0)
			input_txt.close();
		else
			input_bin.close();

		// Write on output file

		output_bin.write("--rl\n".getBytes());

		if (bin == 1)
			output_bin.write(header.getBytes());

		output_bin.write("---\n".getBytes());
		output_bin.write(encode.getBytes());
		output_bin.flush();
		output_bin.close();
	}

	public static void undoCompression(String file_in, String file_out)
			throws IOException {

		input_bin = new BufferedReader(new FileReader(file_in)); // Open the
																	// input
																	// file bin
		output_txt = new FileWriter(new File(file_out));

		while ((input_bin.readLine()).contains("---") == false)
			;

		int c = 0, count = 0, i;
		while (c != -1) { // Read the file

			c = input_bin.read(); // (

			if (c == -1)
				break;

			i = 0;
			while ((c = input_bin.read()) != ',') { // count

				if (i == 0)
					count = Character.getNumericValue(c);
				else
					count = (int) (count * (Math.pow(10, i)) + (Character
							.getNumericValue(c)));

				i++;
			}

			c = input_bin.read(); // char

			for (i = 0; i < count; i++) {
				output_txt.write(c);
				output_txt.flush();
			}

			c = input_bin.read(); // )
		}

		input_bin.close();
		output_txt.close();
	}
}
