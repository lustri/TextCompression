package textCompression;

import java.io.*;

public class TextCompression {

	public static void main(String[] args) throws IOException {

		if (args[0].equals("encode")) {

			if (args.length != 9) {
				System.out
						.println("Syntax: encode -i <file_in.txt> -o <file_out.bin> --bwt=<true/false> --txtblck=<size> --huffman=<true/false> --runl=<true/false>");
				return;
			}

			if (args[5].equals("--bwt=true"))
				BurrowsWheelerTransformed.doTransformed(args[2], args[4],
						args[6].substring(10, args[6].length()));
			
			if(args[8].equals("--runl=true")){
				if(args[5].equals("--bwt=false"))
					RunLength.doCompression(args[2],args[4]);
			}
				
		}

		if (args[0].equals("decode")) {

			if (args.length != 5) {
				System.out
						.println("Syntax: decode -i <file_in.bin> -o <file_out.txt>");
				return;
			}
			
			BufferedReader input = new BufferedReader(new FileReader(args[2]));
			String content;
			
			while((content = input.readLine()).contains("---") == false){
				
				if(content.contains("--bwt")){
					input.close();
					BurrowsWheelerTransformed.undoTransformed(args[2],args[4]);
					break;
				}
				
				if(content.contains("--rl")){
					input.close();
					RunLength.undoCompression(args[2],args[4]);
				}
			}
			
		}
	}

}
