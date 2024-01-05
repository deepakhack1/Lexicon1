package ie.atu.sw;

import java.io.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Perform sentiment analysis on multiple texts using a predefined lexicon.
 * Utilizes CompletableFuture for concurrent processing and writes the sentiment scores to an output file.
 */
public class SentimentAnalysis {

    static Map<String, Double> lexicon = new ConcurrentHashMap<>();

    /**
     * Loads the lexicon from the provided file path.
     *
     * @param file Path to the lexicon file.
     */
    public  void loadLexicon(File file, FileOutputStream fileOutputStream ) {
        loadLexicon(file.getPath());

        String[] texts = {
                "Text 1 for sentiment analysis.",
                "Text 2 for sentiment analysis.",
                "Text 3 for sentiment analysis."
        };

        CompletableFuture<Void>[] futures = new CompletableFuture[texts.length];

        for (int i = 0; i < texts.length; i++) {
            final String text = texts[i];
            futures[i] = CompletableFuture.supplyAsync(() -> computeSentimentScore(text))
                    .thenAccept(score -> writeScoreToFile(fileOutputStream, text, score));
        }

        // Wait for all CompletableFuture tasks to complete
        CompletableFuture.allOf(futures).join();
    }

    public static void loadLexicon(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    String word = parts[0];
                    double score = Double.parseDouble(parts[1]);
                    lexicon.put(word, score);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }


    /**
     * Computes the sentiment score for a given text using the loaded lexicon.
     *
     * @param text The text to analyze for sentiment.
     * @return The sentiment score of the text.
     */
    public static double computeSentimentScore(String text) {
        double score = 0.0;
        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

        for (String word : words) {
            if (lexicon.containsKey(word)) {
                score += lexicon.get(word);
            }
        }

        return score;
    }


    /**
     * Writes the sentiment score of a text to the specified file.
     *
     * @param filePath The file to write the sentiment score to.
     * @param text The text for which the sentiment score was calculated.
     * @param score The sentiment score of the text.
     */
    public static void writeScoreToFile(FileOutputStream filePath, String text, double score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString(), true))) {
            writer.write("Text: " + text + "\n");
            writer.write("Sentiment score: " + score + "\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

