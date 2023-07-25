# File Type Analyzer

This project involves creating a tool to determine a file type based on its content, not its extension. Many file types contain special byte sequences that make it easy to identify them. This approach is widely used in applications like Unix “file” tool that relies on a sophisticated “magic” database, antivirus and malware-detection tools that search the malicious signatures inside user’s files, and firewalls that do the same with a system’s network traffic.

The file type analyzer can search for specific patterns inside a single file or hundreds and thousands of files against a huge pattern set. Each pattern corresponds to a specific file type. For example, the pattern "%PDF-" corresponds to a "PDF document". 

The program takes as input a file to check (relative path), the pattern string (P), and the result string (R). If the pattern matched at least once the program reports its type. If there were no matches, it prints “Unknown file type”.

## Enhancements

- The program uses efficient pattern searching algorithms like the Knuth-Morris-Pratt and the Rabin-Karp algorithm to improve search performance.
- It can handle multiple files concurrently, by dividing the work between multiple workers. Each worker is equivalent to a single-threaded pattern matcher: it takes several files and matches them consequently using the searching algorithm.
- The program can match several patterns against each file. In case of multiple matches, it implements a prioritizing scheme for patterns to prevent ambiguity.

## Usage

```shell
java Main --naive test_file.pdf "%PDF-" "PDF document"
java Main --KMP test_file.pdf "%PDF-" "PDF document"
java Main test_files "-----BEGIN\ CERTIFICATE-----" "PEM certificate"
java Main test_files patterns.db
```

The examples above show the different ways to use the tool. 
