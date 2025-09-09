import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI; // <-- IMPORT THIS
import java.net.URL;

/**
 * A simple REST API client that fetches data from a public API
 * and displays it in a structured format.
 */
public class code {

    public static void main(String[] args) {
        try {
            // Using a public API for weather data (replace with a valid endpoint if this one is deprecated)
            // FIXED: Use URI.toURL() which is the modern, recommended way
            URL url = new URI("https://catfact.ninja/fact").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Print the JSON response in a structured format
                System.out.println("Response from API:");
                System.out.println(formatJson(response.toString()));
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            // The new method can throw URISyntaxException, which is covered by the general Exception catch
            e.printStackTrace();
        }
    }

    /**
     * A simple method to format a JSON string for better readability.
     * @param jsonString The raw JSON string.
     * @return A formatted JSON string.
     */
    public static String formatJson(String jsonString) {
        // This is a basic formatter. For complex JSON, a library like Gson or Jackson is recommended.
        return jsonString.replace(",", ",\n\t")
                         .replace("{", "{\n\t")
                         .replace("}", "\n}");
    }
}