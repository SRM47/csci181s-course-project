import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVHandler{ 

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
    
    // Method to append data to a CSV file
    // ChatGPT
    public static boolean appendToCSV(String filePath, String[] data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            StringBuilder sb = new StringBuilder();
            for (String value : data) {
                sb.append(value).append(",");
            }
            sb.deleteCharAt(sb.length() - 1); // Remove the last comma
            writer.write(sb.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ChatGPT
    public static ArrayList<String> readColumnValue(String filePath, int col){
    	if (col < 0) {
    		return null;
    	}
    	ArrayList<String> values = new ArrayList<String>();
    	try (BufferedReader csvReader = new BufferedReader(new FileReader(filePath))) {
            String csvLine;
            while ((csvLine = csvReader.readLine()) != null) {
                String[] parts = csvLine.split(",");
                if (col < parts.length) {
                    values.add(parts[col]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values;
    }
    
    public static String readAll(String filePath){
    	StringBuilder sb = new StringBuilder();
    	try (BufferedReader csvReader = new BufferedReader(new FileReader(filePath))) {
            String csvLine;
            while ((csvLine = csvReader.readLine()) != null) {
                sb.append(csvLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

	public static String readRowByIndex(String filePath, int row) {
		String csvLine = null;
		try (BufferedReader csvReader = new BufferedReader(new FileReader(filePath))) {

			for (int r = 0; r < row; r++) {
				if ((csvLine = csvReader.readLine()) == null) {
					return null;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return csvLine;
	}
    
    
}

