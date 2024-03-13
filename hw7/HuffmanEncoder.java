import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class HuffmanEncoder {
    private static final int R = 256;
    public static Map<Character, Integer> buildFrequencyTable(char[] inputSymbols) {
        Map<Character, Integer> table = new HashMap<>();
        int[] freq = new int[R];
        for (char ch : inputSymbols) {
            freq[ch]++;
        }

        for (int ch = 0; ch < R; ch++) {
            if (freq[ch] > 0) {
                table.put((char) ch, freq[ch]);
            }
        }
        return table;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            char[] rowFile = FileUtils.readFile(args[0]);
            Map<Character, Integer> freqTable = buildFrequencyTable(rowFile);
            BinaryTrie bt = new BinaryTrie(freqTable);
            ObjectWriter ow = new ObjectWriter(args[0] + ".huf");
            ow.writeObject(bt);
            ow.writeObject(rowFile.length);
            Map<Character, BitSequence> lookUpTable = bt.buildLookupTable();
            List<BitSequence> bsList = new LinkedList<>();
            for (char ch : rowFile) {
                bsList.add(new BitSequence(lookUpTable.get(ch)));
            }
            BitSequence hugeBs = BitSequence.assemble(bsList);
            ow.writeObject(hugeBs);
        }
    }
}
