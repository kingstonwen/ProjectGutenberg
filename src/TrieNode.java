public class TrieNode {
    TrieNode[] children;
    char label;
    boolean terminal;
    String word;

    int ALPHABET_SIZE = 26;

    public TrieNode() {
        this.children = new TrieNode[ALPHABET_SIZE];
    }

    public TrieNode(char label) {
        this();
        this.label = label;
    }
}

