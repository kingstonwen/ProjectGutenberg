import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextAnalysis {

    public static final String WORD_ONLY_REGEX_PATTERN = "[a-zA-Z]+";
    public static final String MOST_COMMON_WORDS_FILE_PATH = "book/1-1000.txt";
    public static final String TEST_FILE_PATH = "book/test.txt";
    public static final String BOOK_FILE_PATH = "book/1342.txt";

    public Integer getTotalNumberOfWords(String textFile) {
        String fileName = "" + textFile;
        Integer total = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    total += getNumberOfWordsInLine(line.trim());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return total;
    }

    public static Integer getTotalNumberOfWords2(String textFile) {
        Integer total = 0;
        try (Stream<String> stream = Files.lines(Paths.get(textFile))) {
            total = stream
                    .map(TextAnalysis::getNumberOfWordsInLine)
                    .reduce(
                            0,
                            (a, b) -> a + b);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return total;
    }

    private static Integer getNumberOfWordsInLine(String line) {
        List<String> words = new LinkedList<>();
        Pattern wordPattern = Pattern.compile(WORD_ONLY_REGEX_PATTERN);
        Matcher matcher = wordPattern.matcher(line);
        while (matcher.find()) {
            words.add(matcher.group(0));
        }
        return words.size();
    }

    public Integer getUniqueWordsInBook(String textFile) {
        int count = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    count += getUniqueWordsInLine(line.trim());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return count;
    }

    private int getUniqueWordsInLine(String line) {
        Set<String> wordSet = new HashSet<>();
        Pattern wordPattern = Pattern.compile(WORD_ONLY_REGEX_PATTERN);
        Matcher matcher = wordPattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group(0).toLowerCase();
            wordSet.add(word);
        }
        return wordSet.size();
    }

    public Object[] get20MostFrequentWords(String fileName) {
        Map<String, Integer> wordCount = getWordCount(fileName);
        Map<String, Integer> top20 = getTopKStringCountMap(wordCount, 20);
        Object[] res = new Object[20];
        int i = 0;
        for(Map.Entry<String,Integer> entry : top20.entrySet()) {
            Object[] temp = new Object[2];
            temp[0] = entry.getKey();
            temp[1] = entry.getValue();
            res[i++] = temp;
        }
        return res;
    }

    private Map<String, Integer> getTopKStringCountMap(Map<String, Integer> wordCount, int k) {
        return wordCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(k)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private void updateMapFromLine(String line, Map<String, Integer> map) {
        Pattern words = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = words.matcher(line);
        while (matcher.find()) {
            String word = matcher.group(0).toLowerCase();
            map.put(word, map.getOrDefault(word,0) + 1);
        }
    }

    private Set<String> getMostCommon100Words() {
        Set<String> commonWords = new HashSet<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(MOST_COMMON_WORDS_FILE_PATH))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null && i < 100) {
                commonWords.add(line.trim().toLowerCase());
                i++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return commonWords;
    }

    public Object[] getKMostInterestingFrequentWords(String filePath, int k) {
        Set<String> most100CommonWords = getMostCommon100Words();
        Map<String, Integer> wordCount = getWordCount(filePath);
        Map<String, Integer> topKInteresting = getTopKMapHelper(most100CommonWords, wordCount, k);
        Object[] res = new Object[k];
        int i = k-1;
        for(Map.Entry<String,Integer> entry : topKInteresting.entrySet()) {
            Object[] temp = new Object[2];
            temp[0] = entry.getKey();
            temp[1] = entry.getValue();
            res[i--] = temp;
        }
        return res;
    }

    private Map<String, Integer> getWordCount(String filePath) {
        Map<String, Integer> wordCount = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    updateMapFromLine(line, wordCount);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return wordCount;
    }

    private Map<String, Integer> getTopKMapHelper(Set<String> most100Common, Map<String, Integer> wordCount, int k) {
        PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>((e1, e2) -> e1.getValue()-e2.getValue());
        for(Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (most100Common.contains(entry.getKey())) continue;
            if (minHeap.size() == k) {
                if (entry.getValue() > minHeap.peek().getValue()) {
                    minHeap.poll();
                    minHeap.offer(entry);
                }
            } else {
                minHeap.offer(entry);
            }
        }
        Map<String, Integer> resMap = new LinkedHashMap<>();
        while (!minHeap.isEmpty()) {
            Map.Entry<String, Integer> entry = minHeap.poll();
            resMap.put(entry.getKey(), entry.getValue());
        }
        return resMap;
    }

    public Object[] get20LeastFrequentWords(String filePath) {
        Map<String, Integer> wordCount = getWordCount(filePath);
        Map<String,Integer> least20 =
                wordCount.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                        .limit(20)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        Object[] res = new Object[20];
        int i = 0;
        for(Map.Entry<String,Integer> entry : least20.entrySet()) {
            Object[] temp = new Object[2];
            temp[0] = entry.getKey();
            temp[1] = entry.getValue();
            res[i++] = temp;
        }
        return res;
    }

    public int[] getFrequencyOfWord(String word) {
        List<Integer> frequencyPerChapter = new ArrayList<>();
        int chapterIndex = -1;
        try(BufferedReader reader = new BufferedReader(new FileReader(BOOK_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    chapterIndex = getCountInLine(line, word, frequencyPerChapter, chapterIndex);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        int i = 0;
        int[] res = new int[frequencyPerChapter.size()];
        for(Integer num : frequencyPerChapter) {
            res[i++] = num;
        }
        return res;
    }

    private int getCountInLine(String line, String target, List<Integer> freqPerChapter, int index) {
        if (line.startsWith("Chapter")) {
            index++;
            freqPerChapter.add(0);
        }
        if (index == -1) return index;
        Pattern words = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = words.matcher(line);
        while (matcher.find()) {
            String word = matcher.group(0).toLowerCase();
            if (word.equals(target.toLowerCase())) {
                int freq = freqPerChapter.get(index);
                freqPerChapter.set(index, freq+1);
            }
        }
        return index;
    }

    public int getChapterQuoteAppears(String quote) {
        int currChapterNum = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(BOOK_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    if (line.startsWith("Chapter")) {
                        String[] tokens = line.split(" ");
                        currChapterNum = Integer.parseInt(tokens[1]);
                    }
                    if (line.contains(quote)) {
                        return currChapterNum;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return currChapterNum;
    }

    public String generateSentence() {
        String startWord = "We";
        String sentence = startWord;
        String currentWord = startWord;
        for(int i = 0; i < 20; i++) {
            Map<String,Integer> afterMap = getWordsAfter(currentWord);
            Map<String, Integer> top1 = getTopKStringCountMap(afterMap,1);
            currentWord = top1.keySet().toArray(new String[1])[0];
            sentence += " " + currentWord;
        }
        return sentence + ".";
    }

    private Map<String, Integer> getWordsAfter(String word) {
        Map<String, Integer> afterMap = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(BOOK_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    updateMap(afterMap, line, word);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return afterMap;
    }

    private void updateMap(Map<String, Integer> map, String line, String target) {
        Pattern words = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = words.matcher(line);
        while (matcher.find()) {
            String word = matcher.group(0);
            if (word.equals(target)) {
                if (matcher.find()) {
                    String afterWord = matcher.group(0);
                    map.put(afterWord, map.getOrDefault(word,0) + 1);
                }
            }
        }
    }

    public List<String> getAutocompleteSentence(String startOfSentence) {
        return null;
    }




    public static void main(String[] args) {
        TextAnalysis analysis = new TextAnalysis();
        int res = analysis.getTotalNumberOfWords(BOOK_FILE_PATH);
        System.out.println("Total words in novel: " + res);
        System.out.println("Total unique words in novel : " + analysis.getUniqueWordsInBook(BOOK_FILE_PATH));
        Object[] top20 = analysis.get20MostFrequentWords(BOOK_FILE_PATH);
        System.out.println("Most frequent words:");
        for(int i = 0; i < 20; i++) {
            System.out.print((i+1) + ". ");
            Object[] temp = (Object[])top20[i];
            System.out.println(temp[0] + " " + temp[1]);
        }
        System.out.println("Most interesting frequent words:");
        int k = 30;
        Object[] topKMostInteresting = analysis.getKMostInterestingFrequentWords(BOOK_FILE_PATH, k);
        for(int i = 0; i < k; i++) {
            System.out.print(i+1 + ". ");
            Object[] temp = (Object[])topKMostInteresting[i];
            System.out.println(temp[0] + " " + temp[1]);
        }
        Object[] least20 = analysis.get20LeastFrequentWords(BOOK_FILE_PATH);
        System.out.println("20 Least Frequent words:");
        for(int i = 0; i < 20; i++) {
            System.out.print(i+1 + ". ");
            Object[] temp = (Object[])top20[i];
            System.out.println(temp[0] + " " + temp[1]);
        }
        int[] freqOfWord = analysis.getFrequencyOfWord("Darcy");
        System.out.print("[" + freqOfWord[0]);
        for(int i = 1; i < freqOfWord.length; i++) {
            System.out.print("," + freqOfWord[i]);
        }
        System.out.println("]");

        System.out.println(analysis.getChapterQuoteAppears("\"I declare after all there is no enjoyment like reading! " +
                "How much sooner one tires of any thing than of a book! — When I have a house of my own, " +
                "I shall be miserable if I have not an excellent library.\""));
        String generateSentence = analysis.generateSentence();
        System.out.println(generateSentence);
    }
}
