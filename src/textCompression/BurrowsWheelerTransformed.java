package textCompression;

import java.io.*;
import java.util.Arrays;

public class BurrowsWheelerTransformed {

	private static InputStreamReader input;
	private static OutputStream output;
	private static BufferedReader input_decode;

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
		input = new InputStreamReader(new FileInputStream(file_in)); // Abre o
																		// arquivo
																		// de
																		// entrada
		output = new FileOutputStream(new File(file_out));
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

				c = input.read();

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
							Arrays.toString(block)) == 0)
						indexes += i + " ";

				for (i = 0; i < size; i++)
					encode += matrix[i][size - 1];

			}
		}

		input.close();

		output.write("--bwt\n".getBytes());
		output.write(indexes.getBytes());
		output.write("\n".getBytes());
		output.write("--txtblck\n".getBytes());
		output.write(block_size.getBytes());
		output.write("\n".getBytes());
		output.write("---\n".getBytes());
		output.write(encode.getBytes());
		output.flush();
		output.close();
	}

	public static void undoTransformed(String file_in, String file_out)
			throws IOException {

		input_decode = new BufferedReader(new FileReader(file_in));

		while ((input_decode.readLine()).contains("--bwt") == false)
			;

		indexes = input_decode.readLine();

		while ((input_decode.readLine()).contains("--txtblck") == false)
			;

		size = Integer.parseInt(input_decode.readLine());

		while ((input_decode.readLine()).contains("---") == false)
			;

		block = new char[size]; // inicial block
		block_aux = new char[size]; // changed block
		original = new char[size]; // decode block
		position_vector = new int[size];
		encode = new String();

		int c = 0, i, j;
		int index, id_position = 0;
		char aux;
		while (c != -1) {

			for (i = 0; i < size; i++)
				block[i] = '\0';

			// Separa os blocos
			for (i = 0; i < size; i++) {

				c = input_decode.read();

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
						if (block_aux[j] >= block_aux[j + 1]) {
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
				while(indexes.charAt(id_position)!= ' '){
					
					if(i==0)
						index = Character
						.getNumericValue((indexes.charAt(id_position)));
					else{
						index = (int) (index*(Math.pow(10,i)) + Character
								.getNumericValue((indexes.charAt(id_position))));;
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

		System.out.println(encode);
	}
}
