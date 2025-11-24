name: Norman Sackett
email: nsackett@u.rochester.edu

lab partner: Julien Diamond

In this porject we were tasked with writing a Huffman coding algorithm. For this project, I used the previously made URHeap data structure as well as custom Node and Code classes that store nodes of byte values and frequencies and Huffman codes respectively. The encode method takes an input file and constructs a frequency file which is then used to make a Huffman tree from a min heap. This Huffman tree is then converted into a dictionary for easy lookup, using an array with byte values as indicies and Codes at those indicies. This creates the encoded file that, along with the frequency file, can be sent to the decode method. This decode method uses the frequency file to recreate the Huffman tree (here, the frequency file is sorted in byte order rather than frequency order to ensure the insertion order for the min heap is the same in both the encoding and decoding steps). Once the Huffman tree is recreated, the tree is traversed character by character from the data in the encoded file, converting Huffman codes back to byte values, which are then written into the decoded file.
