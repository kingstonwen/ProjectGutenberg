import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Trie {

    private TrieNode root;

    public Trie() {
        this.root = new TrieNode(' ');
    }

    public void addWord(String word) {
        char[] inputWord = word.toCharArray();
        TrieNode cur = root;
        TrieNode next = null;
        int index = 0;

        do {
            next = cur.children[inputWord[index] - 'a'];
            if (next != null) {
                cur = next;
                index++;
                if (index >= word.length()) {
                    cur.terminal = true;
                    cur.word = word;
                    return;
                }
            }
        } while (next != null);

        for(; index < inputWord.length;index++) {
            cur.children[inputWord[index]-'a'] = new TrieNode(inputWord[index]);
            cur = cur.children[inputWord[index] - 'a'];
        }

        cur.terminal = true;
        cur.word = word;
    }

    public List<String> wordsByPrefix(String prefix) {
        char[] prefixCharArray = prefix.toCharArray();
        TrieNode cur = root;
        TrieNode next = null;
        int index = 0;

        do {
            next = cur.children[prefixCharArray[index] - 'a'];
            if (next == null) {
                return null;
            }
            index++;
            cur = next;
        } while (index < prefix.length());

        List<String> words = new ArrayList<>();
        Deque<TrieNode> deque = new ArrayDeque<>();
        deque.addLast(cur);
        while (!deque.isEmpty()) {
            TrieNode first = deque.removeFirst();
            if (first.terminal) {
                words.add(first.word);
            }

            for(TrieNode node : first.children) {
                if (node != null) {
                    deque.add(node);
                }
            }
        }

        return words;
    }
}
