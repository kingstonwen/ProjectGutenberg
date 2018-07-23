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

    public String[] wordsByPrefix(String prefix) {
        return null;
    }
}
