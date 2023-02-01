# Huffman-Coding

If you desire to read a more in-depth explanation as to how this algorithm works, how to run my code, etc. A more detailed word file is included on my GitHub in the "Huffman Files" folder which was my original submission for this project. The wikipedia page on Huffman Coding is also a fantastic resource.

LinkedIn Summary:

This was my final project for my data structures course in high school which was heavily inspired by the University of Washington course CSE 143.

In this project, I implemented the Huffman Coding algorithm which is a lossless compression algorithm that achieves roughly 40% compression on .txt files on average.

There are two steps for compression and one for decompression.

In the encoding section, we take in a .txt file and run an algorithm that analyzes individual characters and their associated frequency in the text. Once we have that, through recursive backtracking of our data structures we create a .code file which is used to encode and compress the original file into a .short file, as well as used to decompress a .short file.

In the decoding section we have a .short file and we use a scanner on the .code file to parse and expand the information from the .short file to get back our .txt file without any loss of character value or position, so that the text is returned in its original condition from a 40% smaller file.
