package textCompression;

import java.io.*;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

public class Huffman {

	private static InputStreamReader input_txt;
	private static OutputStream output_bin;

	private static BufferedReader input_bin; // File input.bin
	private static FileWriter output_txt; // File output.txt

	private static int[] frequencies = new int[257];

	private static String header;
	private static String encode;

	public static void doCompression(String file_in, String file_out, int bin)
			throws IOException {
		
		header = new String();
		encode = new String();

		if (bin == 0)
			input_txt = new InputStreamReader(new FileInputStream(file_in));
		else {
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

		// Init frequencies
		Arrays.fill(frequencies, 0);

		// Get frequencies
		int b = 0;
		while (true) {
			
			if(bin==0)
				b = input_txt.read();
			else
				b = input_bin.read();
			
			encode += (char) b;
			
			if (b == -1) {
				frequencies[256]++; // EOF symbol
				break;
			}
			frequencies[b]++;
		}
		if(bin==0)
			input_txt.close();
		else
			input_bin.close();

		 // Build Huffman Tree
		Node root = buildCodeTree();

		// Code table
		String[] st = new String[257];
		buildCode(st, root, "");

		output_bin.write("--huffman\n".getBytes());
		
		for (int i = 0; i < 257; i++) {
			if (frequencies[i] > 0) {
				String c = (char) i + " ";
				output_bin.write(c.getBytes());
				output_bin.write(st[i].getBytes());
				output_bin.write("\n".getBytes());
			}
		}
		
		if(bin==1)
			output_bin.write(header.getBytes());
		
		output_bin.write("---\n".getBytes());
		
		
		int i = 0; 
		while (i < encode.length()) {
			
			if (encode.charAt(i) > 256) {
				frequencies[256]++;
				break;
			}
			
			output_bin.write(st[(int) encode.charAt(i)].getBytes());
			i++;
		}
		

		output_bin.flush();
		output_bin.close(); 

	}

	public static void undoCompression(String file_in, String file_out)
			throws IOException {

		input_bin = new BufferedReader(new FileReader(file_in));
		output_txt = new FileWriter(new File(file_out));

		input_bin.readLine();

		Node root = new Node(-1, -1, null, null);
		readTree(root);

		decode(root);

		input_bin.close();
		output_txt.close();

	}

	private static class Node implements Comparable<Node> {
		private int symbol;
		private int freq;
		private Node left, right;

		Node(int symbol, int freq, Node left, Node right) {
			this.symbol = symbol;
			this.freq = freq;
			this.left = left;
			this.right = right;
		}

		// is the node a leaf node?
		private boolean isLeaf() {
			assert ((left == null) && (right == null))
					|| ((left != null) && (right != null));
			return (left == null) && (right == null);
		}

		// compare, based on frequency or lowest symbol
		public int compareTo(Node that) {
			if (freq < that.freq)
				return -1;
			else if (freq > that.freq)
				return 1;
			else if (symbol < that.symbol)
				return -1;
			else if (symbol > that.symbol)
				return 1;
			else
				return 0;
		}
	}

	private static Node buildCodeTree() {

		Queue<Node> pqueue = new PriorityQueue<Node>();

		// Add leaves for symbols with non-zero frequency
		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] > 0)
				pqueue.add(new Node(i, frequencies[i], null, null));
		}

		// Repeatedly tie together two nodes with the lowest frequency
		while (pqueue.size() > 1) {
			Node left = pqueue.remove();
			Node right = pqueue.remove();
			pqueue.add(new Node(Math.min(left.symbol, right.symbol), left.freq
					+ right.freq, left, right));
		}

		// Return the remaining node
		return pqueue.remove();
	}

	private static void buildCode(String[] st, Node n, String s) {
		if (!n.isLeaf()) {
			buildCode(st, n.left, s + '0');
			buildCode(st, n.right, s + '1');
		} else {
			st[n.symbol] = s;
		}
	}

	private static void readTree(Node root) throws IOException {
		while (true) {
			String s = input_bin.readLine();
			if ((s.contains("---")))
				break;
			
			if(s.contains("--bwt")){
				while ((input_bin.readLine()).contains("---") == false);
				break;
			}

			if (s.length() == 0)
				s += "\n" + input_bin.readLine();

			Node n = root;
			int i;
			for (i = 2; i < s.length() - 1; i++) {
				if (s.charAt(i) == '0') {
					if (n.left == null) {
						n.left = new Node(-1, -1, null, null);
						n = n.left;
					} else
						n = n.left;
				}
				if (s.charAt(i) == '1') {
					if (n.right == null) {
						n.right = new Node(-1, -1, null, null);
						n = n.right;
					} else
						n = n.right;
				}
			}

			if (s.charAt(i) == '0') {
				n.left = new Node(s.charAt(0), -1, null, null);
			} else {
				n.right = new Node(s.charAt(0), -1, null, null);
			}

		}
	}

	private static void decode(Node root) throws IOException {
		
		int b = input_bin.read();	
			
		while (true) {
			if (b == -1)
				break;

			Node n = root;
			while (n.symbol == -1) {
				if (b == '0')
					n = n.left;
				if (b == '1')
					n = n.right;
				b = input_bin.read();
			}
			
			output_txt.write((char) n.symbol);
		}
	}

}