package textCompression;

import java.io.*;

public class RunLength {

	private static InputStreamReader input_txt; // Arquivo entrada.txt
	private static OutputStream output_bin; // Arquivo saida.bin
	
	private static BufferedReader input_bin; // Arquivo entrada.bin
	private static FileWriter output_txt; //Arquivo saida.txt

	
	private static String encode;
	
	public static void doCompression(String file_in, String file_out) throws IOException{
		
		input_txt = new InputStreamReader(new FileInputStream(file_in)); // Abre o arquivo de entrada
		output_bin = new FileOutputStream(new File(file_out));
		
		encode = new String();
		
		int c=0,c2=0;
		c = input_txt.read();
		while(true){
			
			int count=1;
			
			while(c2!=-1 && c == (c2 = input_txt.read())){
				count++;
			}
			
			if(c2==-1)
				break;
			
			encode += "(" + count + "," + (char) c + ")"; 
			
			c = c2;
		}
		
		input_txt.close();

		output_bin.write("--rl\n".getBytes());
		output_bin.write("---\n".getBytes());
		output_bin.write(encode.getBytes());
		output_bin.flush();
		output_bin.close();
	}
	
	public static void undoCompression(String file_in, String file_out) throws IOException{
		
		input_bin = new BufferedReader(new FileReader(file_in));
		output_txt = new FileWriter(new File(file_out));
		
		while ((input_bin.readLine()).contains("---") == false)
			;
		
		int c = 0, count = 0, i;
		while(c!=-1){
			
			c = input_bin.read(); // (
			
			i = 0;
			while((c = input_bin.read())!=','){ // count
				
				if(i==0)
					count = Character.getNumericValue(c);
				else
					count = (int) (count*(Math.pow(10,i)) + (Character.getNumericValue(c)));
				
				i++;
			}
			
			c = input_bin.read(); // char
			
			for(i=0; i<count;i++){
				output_txt.write(c);
				output_txt.flush();
			}
			
			c = input_bin.read(); //)
		}
		
		input_bin.close();
		output_txt.close();

	}
}
