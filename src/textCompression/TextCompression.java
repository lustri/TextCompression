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
					RunLength.doCompression(args[2],args[4],0);
				else
					RunLength.doCompression(args[4],args[4],1);
			}

			if(args[7].equals("--huffman=true")){
				if(args[5].equals("--bwt=false"))
					Huffman.doCompression(args[2],args[4],0);
				else
					Huffman.doCompression(args[4],args[4],1);
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
			
			int rl=0, huffman=0;
			while((content = input.readLine()).contains("---") == false){
				
				if(content.contains("--bwt")){
					if(rl==0 && huffman==0)
						BurrowsWheelerTransformed.undoTransformed(args[2],args[4],0);
					else
						BurrowsWheelerTransformed.undoTransformed(args[2],args[4],1);
				}
				
				if(content.contains("--rl")){
					RunLength.undoCompression(args[2],args[4]);
					rl = 1;
				}

				if(content.contains("--huffman")){
					Huffman.undoCompression(args[2],args[4]);
					huffman = 1;
				}
			}
			
			input.close();
		}
	}

}
