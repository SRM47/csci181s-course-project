import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CSVAppend{ 

    public void appendToCSV1(String newData) {
        String csvFilePath = "db.csv";
        
        try {
            appendToCSV2(csvFilePath, newData);
            System.out.println("Data appended to CSV successfully.");
        } catch (IOException e) {
            System.err.println("Error appending data to CSV: " + e.getMessage());
        }
    }

    private static void appendToCSV2(String filePath, String newData) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Append the new data to the end of the CSV file
            writer.write(newData); 
            writer.newLine();
        }
    }
}

