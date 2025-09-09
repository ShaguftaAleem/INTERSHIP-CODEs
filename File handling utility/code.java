import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


//A simple file handling utility to create, read, write, and modify text files.

public class code {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- File Handling Menu ---");
            System.out.println("1. Write to a file (creates a new file or overwrites existing)");
            System.out.println("2. Read a file");
            System.out.println("3. Append to a file");
            System.out.println("4. Delete a file");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter filename: ");
                        String writeFile = scanner.nextLine();
                        System.out.print("Enter content: ");
                        String content = scanner.nextLine();
                        writeToFile(writeFile, content, false);
                        break;
                    case 2:
                        System.out.print("Enter filename to read: ");
                        String readFile = scanner.nextLine();
                        readFromFile(readFile);
                        break;
                    case 3:
                        System.out.print("Enter filename to append to: ");
                        String appendFile = scanner.nextLine();
                        System.out.print("Enter content to append: ");
                        String appendContent = scanner.nextLine();
                        writeToFile(appendFile, appendContent, true);
                        break;
                    case 4:
                        System.out.print("Enter filename to delete: ");
                        String deleteFile = scanner.nextLine();
                        deleteFile(deleteFile);
                        break;
                    case 5:
                        System.out.println("Exiting program.");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                }
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Writes or appends content to a file.
     * @param fileName The name of the file.
     * @param content The content to write.
     * @param append If true, content will be appended; otherwise, the file will be overwritten.
     */
    public static void writeToFile(String fileName, String content, boolean append) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, append))) {
            writer.write(content);
            writer.newLine();
            System.out.println("Successfully wrote to " + fileName);
        }
    }

    /**
     * Reads the content of a file and prints it to the console.
     * @param fileName The name of the file to read.
     */
    public static void readFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            System.out.println("\nContent of " + fileName + ":");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    /**
     * Deletes a file.
     * @param fileName The name of the file to delete.
     */
    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.delete()) {
            System.out.println("Successfully deleted " + fileName);
        } else {
            System.out.println("Failed to delete " + fileName + ". File might not exist.");
        }
    }
}