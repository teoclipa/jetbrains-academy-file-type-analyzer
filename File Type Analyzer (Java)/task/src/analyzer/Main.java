package analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please provide the directory and pattern database as arguments.");
            return;
        }

        String directoryPath = args[0];
        String patternDbPath = args[1];

        try {
            List<String> filePaths = getAllFilePaths(directoryPath);
            List<Pattern> patterns = parsePatternDatabase(patternDbPath);

            ExecutorService executor = Executors.newFixedThreadPool(filePaths.size() * patterns.size());
            List<Future<String>> futures = new ArrayList<>();

            for (String filePath : filePaths) {
                for (Pattern pattern : patterns) {
                    futures.add(executor.submit(new FileTypeChecker(filePath, pattern.pattern(), pattern.fileType())));
                }
            }

            for (Future<String> future : futures) {
                try {
                    System.out.println(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("An error occurred while checking a file: " + e.getMessage());
                }
            }

            executor.shutdown();
        } catch (IOException e) {
            System.out.println("An error occurred while reading the directory: " + e.getMessage());
        }
    }

    private static List<String> getAllFilePaths(String directoryPath) throws IOException {
        List<String> filePaths = new ArrayList<>();
        Files.walk(Paths.get(directoryPath)).filter(Files::isRegularFile).forEach(path -> filePaths.add(path.toString()));
        return filePaths;
    }

    private static List<Pattern> parsePatternDatabase(String patternDbPath) throws IOException {
        List<Pattern> patterns = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(patternDbPath));

        for (String line : lines) {
            String[] parts = line.split(";");
            int priority = Integer.parseInt(parts[0]);
            String pattern = parts[1].replace("\"", "");
            String fileType = parts[2].replace("\"", "");
            patterns.add(new Pattern(priority, pattern, fileType));
        }

        return patterns;
    }
}

