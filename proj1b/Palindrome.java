public class Palindrome {
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> d = new ArrayDeque<>();
        int length = word.length(), index = 0;
        for (; index < length; index++) {
            d.addLast(word.charAt(index));
        }
        return d;
    }

    public boolean isPalindrome(String word) {
        return isPalindromeHelper(wordToDeque(word));
    }

    private boolean isPalindromeHelper(Deque<Character> d) {
        if (d.size() <= 1) {
            return true;
        } else if ((d.removeFirst()).equals(d.removeLast())) {
            return isPalindromeHelper(d);
        }
        return false;
    }

    public boolean isPalindrome(String word, CharacterComparator cc) {
        return isPalindromeHelper(wordToDeque(word), cc);
    }

    private boolean isPalindromeHelper(Deque<Character> d, CharacterComparator cc) {
        if (d.size() <= 1) {
            return true;
        } else if (cc.equalChars(d.removeFirst(), d.removeLast())) {
            return isPalindromeHelper(d, cc);
        }
        return false;
    }
}
