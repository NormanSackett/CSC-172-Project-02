import java.lang.Comparable;
import java.lang.Cloneable;
import java.io.*;

public class HuffmanSubmit implements Huffman {
	public static void main(String[] args) {
		Huffman huffman = new HuffmanSubmit();
		try {
			huffman.encode("ur.jpg", "ur.enc", "freq.txt");
			huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void encode(String inputFile, String outputFile, String freqFile) throws IOException {
		BinaryOut writer = new BinaryOut(freqFile);
		BinaryIn input = new BinaryIn(inputFile);
		int[] freqArr = new int[256];

		while(!input.isEmpty()) {
			Byte b = input.readByte();
			freqArr[Byte.toUnsignedInt(b)] += 1;
		}
		URHeap<Node> heap = new URHeap<Node>(256);
		
		for(int i = 0; i < 256; i++) {
			if (freqArr[i] > 0) {
				heap.insert(new Node((byte) i, freqArr[i]));
				writer.write(i + ":" + freqArr[i] + "\n");
			}
		}
		writer.close();
		
		Code[] bitDictionary = makeByteDictionary(heap);
		
		BinaryIn readOriginal = new BinaryIn(inputFile);
		BinaryOut writeEncoded = new BinaryOut(outputFile);
		
		while(!readOriginal.isEmpty()) {
			Byte val = readOriginal.readByte();
			Code currCode = bitDictionary[Byte.toUnsignedInt(val)];
			
			for(int i = 0; i < currCode.length; i++)
				writeEncoded.write(currCode.get(i));
		}
		writeEncoded.close();
	}
	
	//creates a huffman tree to then be turned into a dictionary for encoding
	private Code[] makeByteDictionary(URHeap<Node> h) throws IOException {
		Node n = new Node();
		
		while (h.size() > 1) {
			Node left = h.deleteMin();
			Node right = h.deleteMin();
			n = new Node(null, left.frequency + right.frequency, left, right);
			h.insert(n);
		}

		return traverseHuffmanTree(n, new Code(), new Code[256]);
	}
	
	//preorder traversal of the huffman tree, constructing an array of codes as it reaches leaf nodes
	private Code[] traverseHuffmanTree(Node root, Code currCode, Code[] codeArr) {
		if (root.left == null && root.right == null) {
			codeArr[Byte.toUnsignedInt(root.val)] = currCode;
		} else {
			Code leftCode = (Code) currCode.clone();
			leftCode.setNext(false);
			Code rightCode = (Code) currCode.clone();
			rightCode.setNext(true);
			
			traverseHuffmanTree(root.left, leftCode, codeArr);
			traverseHuffmanTree(root.right, rightCode, codeArr);
		}
		return codeArr;
	}
	
	//returns the root node of the huffman tree
	private Node makeHuffmanTree(URHeap<Node> h) throws IOException {
		Node n = new Node();
		
		while (h.size() > 1) {
			Node left = h.deleteMin();
			Node right = h.deleteMin();
			n = new Node(null, left.frequency + right.frequency, left, right);
			h.insert(n);
		}
		
		return n;
	}

	@Override
	public void decode(String inputFile, String outputFile, String freqFile) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(freqFile));
			String[] line = new String[2];
			String tempLine = "";
			URHeap<Node> heap = new URHeap<Node>(256);
		
			while((tempLine = reader.readLine()) != null) {
				line = tempLine.split(":");
				Node n = new Node((byte) Integer.parseInt(line[0]), Integer.parseInt(line[1]));
				heap.insert(n);
			}
			reader.close();
			Node root = makeHuffmanTree(heap);
			writeToFile(inputFile, outputFile, root);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void writeToFile(String inputFile, String outputFile, Node root) throws IOException {
		BinaryIn input = new BinaryIn(inputFile);
		BinaryOut output = new BinaryOut(outputFile);
		Node n = root;
		
		while(!input.isEmpty()) {
			if (input.readBoolean()) 
				n = n.right;
			else n = n.left;
			
			if (n.left == null && n.right == null) {
				output.write(n.val);
				n = root;
			}
		}
		output.close();
	}

	public static class Node implements Comparable<Node> {
		Integer frequency = 0;
		Byte val;
		Node left, right;
		
		public Node() {
			val = null;
			left = null;
			right = null;
		}
		
		public Node(Byte v) {
			val = v;
			left = null;
			right = null;
		}
		
		public Node(Byte v, Integer freq) {
			val = v;
			frequency = freq;
			left = null;
			right = null;
		}
		
		public Node(Byte v, Integer freq, Node l, Node r) {
			val = v;
			frequency = freq;
			left = l;
			right = r;
		}

		@Override
		public int compareTo(Node o) {
			return frequency.compareTo(o.frequency);
		}		
	}
	
	public static class Code implements Cloneable {
		int length;
		boolean[] bits;
		
		public Code() {
			length = 0;
			bits = new boolean[length];
		}
		
		public Code(boolean[] inputBits) {
			length = inputBits.length;
			bits = inputBits;
		}
		
		public void setNext(boolean bit) {
			length++;
			boolean[] tempArr = bits.clone();
			bits = new boolean[length];
			
			for (int i = 0; i < tempArr.length; i++) {
				bits[i] = tempArr[i];
			}
			bits[length - 1] = bit;
		}
		
		public boolean get(int index) {
			return bits[index];
		}
		
		@Override
		public Object clone() {
			boolean[] inputBits = bits.clone();
			return new Code(inputBits);
		}
	}
}

