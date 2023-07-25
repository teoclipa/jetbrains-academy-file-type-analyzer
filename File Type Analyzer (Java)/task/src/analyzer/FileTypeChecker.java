package analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class FileTypeChecker implements Callable<String> {
    private final String filePath;
    private final String pattern;
    private final String fileType;

    public FileTypeChecker(String filePath, String pattern, String fileType) {
        this.filePath = filePath;
        this.pattern = pattern;
        this.fileType = fileType;
    }

    @Override
    public String call() {
        try {
            if (isPatternPresentInFileRabinKarp(filePath, pattern)) {
                return filePath + ": " + fileType;
            } else {
                return filePath + ": Unknown file type";
            }
        } catch (IOException e) {
            return "An error occurred while reading the file: " + e.getMessage();
        }
    }

    private boolean isPatternPresentInFileRabinKarp(String filePath, String pattern) throws IOException {
        Path path = Paths.get(filePath);
        byte[] fileBytes = Files.readAllBytes(path);

        int patternHash = pattern.hashCode();
        int patternLength = pattern.length();

        String text = new String(fileBytes);

        for (int i = 0; i <= text.length() - patternLength; i++) {
            String substr = text.substring(i, i + patternLength);
            int substrHash = substr.hashCode();

            if (substrHash == patternHash && substr.equals(pattern)) {
                return true;
            }
        }

        return false;
    }

    // KMP search algorithm
    private boolean isPatternPresentInFileKMP(String filePath, String pattern) throws IOException {
        Path path = Paths.get(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        byte[] patternBytes = pattern.getBytes();

        int[] lps = computeLPSArray(patternBytes);

        int i = 0;  // index for fileBytes[]
        int j = 0;  // index for patternBytes[]
        while (i < fileBytes.length) {
            if (patternBytes[j] == fileBytes[i]) {
                j++;
                i++;
            }
            if (j == patternBytes.length) {
                return true;
            } else if (i < fileBytes.length && patternBytes[j] != fileBytes[i]) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i = i + 1;
                }
            }
        }
        return false;
    }

    // Compute temporary array to maintain size of suffix which is same as prefix
    // in pattern[].
    private static int[] computeLPSArray(byte[] patternBytes) {
        int[] lps = new int[patternBytes.length];
        int length = 0;  // length of the previous longest prefix suffix
        int i = 1;
        lps[0] = 0;  // lps[0] is always 0

        // the loop calculates lps[i] for i = 1 to M-1
        while (i < patternBytes.length) {
            if (patternBytes[i] == patternBytes[length]) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = length;
                    i++;
                }
            }
        }
        return lps;
    }
}
