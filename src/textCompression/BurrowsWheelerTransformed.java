package textCompression;

import java.io.*;
import java.util.Arrays;

public class BurrowsWheelerTransformed {

	private static InputStreamReader input_txt; // Arquivo entrada.txt
	private static OutputStream output_bin; // Arquivo saida.bin
	private static BufferedReader input_bin; // Arquivo entrada.bin
	private static FileWriter output_txt; // Arquivo saida.txt

	private static int size;

	private static char matrix[][];

	private static char[] block;
	private static char[] block_aux;
	private static char[] original;
	private static int[] position_vector;

	private static String indexes;
	private static String encode;

	public static void doTransformed(String file_in, String file_out,
			String block_size) throws IOException {

		size = Integer.parseInt(block_size);
		input_txt = new InputStreamReader(new FileInputStream(file_in)); // Abre
																			// o
																			// arquivo
																			// de
																			// entrada
		output_bin = new FileOutputStream(new File(file_out));
		indexes = new String();
		encode = new String();

		block = new char[size];
		block_aux = new char[size];

		int c = 0, i = 0, j = 0;
		while (c != -1) { // Lê o arquivo

			for (i = 0; i < size; i++)
				block[i] = '\0';

			// Separa os blocos
			for (i = 0; i < size; i++) {

				c = input_txt.read();

				if (c == -1)
					break;

				block[i] = (char) c;
			}

			// Se for o último bloco, muda para o seu respectivo tamanho
			if (c == -1) {
				size = i;
				matrix = new char[size][size];
			}

			// Método BWT
			if (size > 0) {

				matrix = new char[size][size];

				matrix[0] = block;

				for (i = 1; i < size; i++)
					for (j = 0; j < size - 1; j++)
						matrix[i][j] = matrix[i - 1][j + 1];

				for (i = 1; i < size; i++)
					for (j = 0; j < i; j++)
						matrix[i][size - i + j] = matrix[0][j];

				for (i = 0; i < size; i++)
					for (j = 0; j < size; j++) {
						if (Arrays.toString(matrix[i]).compareTo(
								Arrays.toString(matrix[j])) < 0) {
							block_aux = matrix[i];
							matrix[i] = matrix[j];
							matrix[j] = block_aux;
						}
					}

				for (i = 0; i < size; i++)
					if (Arrays.toString(matrix[i]).compareTo(
							Arrays.toString(block)) == 0) {
						indexes += i + " ";
						break;
					}

				for (i = 0; i < size; i++)
					encode += matrix[i][size - 1];

			}
		}

		input_txt.close();

		output_bin.write("--bwt\n".getBytes());
		output_bin.write(indexes.getBytes());
		output_bin.write("\n".getBytes());
		output_bin.write("--txtblck\n".getBytes());
		output_bin.write(block_size.getBytes());
		output_bin.write("\n".getBytes());
		output_bin.write("---\n".getBytes());
		output_bin.write(encode.getBytes());
		output_bin.flush();
		output_bin.close();

	}

	public static void undoTransformed(String file_in, String file_out, int txt)
			throws IOException {

		input_bin = new BufferedReader(new FileReader(file_in)); // Abre o
																	// arquivo
																	// de
																	// entrada
																	// bin
		while ((input_bin.readLine()).contains("--bwt") == false)
			;

		indexes = input_bin.readLine();

		while ((input_bin.readLine()).contains("--txtblck") == false)
			;

		size = Integer.parseInt(input_bin.readLine());

		while ((input_bin.readLine()).contains("---") == false)
			;

		if (txt == 1)
			input_txt = new InputStreamReader(new FileInputStream(file_out)); // Abre
																				// arquivo
																				// de
																				// entrada
																				// txt

		block = new char[size]; // inicial block
		block_aux = new char[size]; // changed block
		original = new char[size]; // decode block
		position_vector = new int[size];
		encode = new String();

		int c = 0, i, j;
		int index, id_position = 0;
		char aux;
		while (c != -1) {

			block = new char[size];
			block_aux = new char[size];
			original = new char[size];
			position_vector = new int[size];

			for (i = 0; i < size; i++)
				block[i] = '\0';

			// Separa os blocos
			for (i = 0; i < size; i++) {

				if(txt==0)
					c = input_bin.read();
				else
					c = input_txt.read();

				if (c == -1)
					break;

				block[i] = (char) c;
				block_aux[i] = (char) c;
			}

			// Se for o último bloco, muda para o seu respectivo tamanho
			if (c == -1)
				size = i;

			if (size > 0) {

				for (i = 0; i < size; i++) {
					position_vector[i] = -1;
				}

				// Reordenação
				for (i = 0; i < size; i++)
					for (j = 0; j < size - 1; j++) {
						if (block_aux[j] > block_aux[j + 1]) {
							aux = block_aux[j];
							block_aux[j] = block_aux[j + 1];
							block_aux[j + 1] = aux;
						}
					}

				// Construção do vetor transição

				for (i = 0; i < size; i++)
					for (j = 0; j < size; j++) {
						if (block_aux[i] == block[j]) {

							if (position_vector[j] == -1) {
								position_vector[j] = i;
								break;
							}
						}
					}

				i = 0;
				index = 0;
				while (indexes.charAt(id_position) != ' ') {

					if (i == 0)
						index = Character.getNumericValue((indexes
								.charAt(id_position)));
					else {
						index = (int) (index * (Math.pow(10, i)) + Character
								.getNumericValue((indexes.charAt(id_position))));
						;
					}

					i++;
					id_position++;
				}

				id_position++;

				int t = 0;
				for (i = 0; i < size; i++) {

					if (i == 0)
						t = index;
					else
						t = position_vector[t];

					original[size - 1 - i] = block[t];
				}

				for (i = 0; i < size; i++)
					encode += original[i];

			}
		}

		if(txt==0)
			input_bin.close();
		else
			input_txt.close();

		output_txt = new FileWriter(new File(file_out));
		output_txt.write(encode);
		output_txt.flush();
		output_txt.close();
	}
}