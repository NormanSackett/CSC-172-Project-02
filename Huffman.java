import java.io.IOException;

public interface Huffman {
	public void encode ( String inputFile , String outputFile , String freqFile ) throws IOException;
	public void decode ( String inputFile , String outputFile , String freqFile ) throws IOException;
}
