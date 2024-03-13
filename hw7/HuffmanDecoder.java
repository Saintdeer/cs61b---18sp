public class HuffmanDecoder {
    public static void main(String[] args) {
        if (args.length > 1) {
            ObjectReader or = new ObjectReader(args[0]);
            BinaryTrie bt = (BinaryTrie) or.readObject();
            int num = (int) or.readObject();
            BitSequence hugeBs = (BitSequence) or.readObject();
            char[] chars = new char[num];
            Match match;
            for (int i = 0; i < num; i++) {
                match = bt.longestPrefixMatch(hugeBs);
                chars[i] = match.getSymbol();
                hugeBs = hugeBs.allButFirstNBits(match.getSequence().length());
            }
            FileUtils.writeCharArray(args[1], chars);
        }
    }
}
