package org.example;
// Defines the package name for organizing the code into a namespace.

/*
Program utilizes Vector DB and OpenAIAPI using the RAG framework to generate ouput. Documentation for functionality for each line of code is listend/
 */
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
// Apache HTTP client imports for sending HTTP requests to OpenAI API.

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Arrays;
// Swing imports for GUI creation, SQL for database operations, and Arrays for array manipulation.


/*
Here, we'll use our main class for GUI and application logic. Most of this code is self explanatory
 */
public class OpenAIWithVectorDBSwing {



    private static final String DB_URL = "jdbc:sqlite:src/main/resources/Vector.db";
    // Path to the SQLite database file. Vector DB is in resources folder of application

    private static final String OPENAI_API_KEY = "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";
    // OpenAI API key for authentication.

    /*
    Here we have our main method, we'll invoke our swing utilies thread to avoid deadlocking the application
     */
    public static void main(String[] args) {
        // Entry point of the application.
        SwingUtilities.invokeLater(OpenAIWithVectorDBSwing::createAndShowGUI);
        // Ensures GUI is created on the Event Dispatch Thread (thread-safe for Swing).
    }

    /*
    Here, we have our method to create and display the GUI
     */
    private static void createAndShowGUI() {
        // Method to create and display the GUI.

        JFrame frame = new JFrame("VectorDB with OpenAI");
        // Creates the main application window with the specified title.

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Ensures the application exits when the window is closed.

        frame.setSize(600, 400);
        // Sets the window size to 600x400 pixels.

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        // Main panel using BorderLayout with 10-pixel gaps.

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        // Panel for inputs and buttons, using a 3x2 grid layout with gaps.

        JLabel queryLabel = new JLabel("Query Vector:");
        // Label for the query vector input field.

        JTextField queryField = new JTextField();
        // Text field for entering a query vector.

        inputPanel.add(queryLabel);
        inputPanel.add(queryField);
        // Adds the label and text field to the input panel.

        JLabel promptLabel = new JLabel("OpenAI Prompt:");
        // Label for the OpenAI prompt input field.

        JTextField promptField = new JTextField();
        // Text field for entering the OpenAI prompt.

        inputPanel.add(promptLabel);
        inputPanel.add(promptField);
        // Adds the label and text field to the input panel.

        JButton findButton = new JButton("Find Nearest Neighbor");
        // Button to trigger the nearest neighbor search.

        JButton queryButton = new JButton("Query OpenAI");
        // Button to trigger the OpenAI API query.

        inputPanel.add(findButton);
        inputPanel.add(queryButton);
        // Adds the buttons to the input panel.

        JTextArea outputArea = new JTextArea();
        // Text area for displaying results and messages.

        outputArea.setEditable(false);
        // Makes the text area read-only.

        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        // Adds a scrollable view to the text area.

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        // Places the input panel at the top of the main panel.

        mainPanel.add(outputScrollPane, BorderLayout.CENTER);
        // Places the scrollable text area in the center of the main panel.

        frame.add(mainPanel);
        // Adds the main panel to the frame.

        frame.setVisible(true);
        // Makes the frame visible.

        findButton.addActionListener(e -> {
            // Adds an action listener to the "Find Nearest Neighbor" button.

            String queryText = queryField.getText();
            // Retrieves the text entered the query vector field.

            if (!queryText.isEmpty()) {
                // Ensures the query field is not empty.
                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    // Establishes a connection to the database.
                    double[] queryVector = parseVector(queryText);
                    // Parses the query text into a vector.
                    String nearestNeighbor = findNearestNeighbor(conn, queryVector);
                    // Finds the nearest neighbor in the database.
                    outputArea.append("Nearest Neighbor: " + nearestNeighbor + "\n");
                    // Displays the result in the output area.
                } catch (Exception ex) {
                    outputArea.append("Error: " + ex.getMessage() + "\n");
                    // Displays any errors encountered.
                }
            } else {
                outputArea.append("Error: Query vector is empty.\n");
                // Displays an error message if the query field is empty.
            }
        });

        queryButton.addActionListener(e -> {
            // Adds an action listener to the "Query OpenAI" button.

            String prompt = promptField.getText();
            // Retrieves the text entered the prompt field.

            if (!prompt.isEmpty()) {
                // Ensures the prompt field is not empty.
                String response = getOpenAIResponse(prompt);
                // Sends the prompt to OpenAI and retrieves the response.
                outputArea.append("OpenAI Response: " + response + "\n");
                // Displays the OpenAI response in the output area.
            } else {
                outputArea.append("Error: Prompt is empty.\n");
                // Displays an error message if the prompt field is empty.
            }
        });
    }

    private static double[] parseVector(String vectorString) {
        // Parses a comma-separated string into a double array.
        return Arrays.stream(vectorString.split(","))
                .map(String::trim)
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    private static String findNearestNeighbor(Connection conn, double[] queryVector) throws SQLException {
        // Finds the content of the nearest neighbor vector in the database.
        String selectSQL = "SELECT content, vector FROM vectors";
        // SQL query to retrieve all content and vectors from the table.

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            // Executes the query and retrieves the results.

            double maxSimilarity = -1.0;
            // Stores the maximum similarity found.

            String nearestNeighbor = null;
            // Stores the content of the nearest neighbor.

            while (rs.next()) {
                // Iterates over the result set.
                String content = rs.getString("content");
                // Retrieves the content of the current row.
                String vectorString = rs.getString("vector");
                // Retrieves the vector of the current row.

                double[] storedVector = parseVector(vectorString);
                // Parses the stored vector string into a double array.

                double similarity = cosineSimilarity(queryVector, storedVector);
                // Computes the cosine similarity between the query and stored vector.

                if (similarity > maxSimilarity) {
                    // Updates the nearest neighbor if a higher similarity is found.
                    maxSimilarity = similarity;
                    nearestNeighbor = content;
                }
            }
            return nearestNeighbor;
            // Returns the content of the nearest neighbor.
        }
    }

    /*
    Method to calculate similarity between two vectors, with param vec1 and vec2
     */
    private static double cosineSimilarity(double[] vec1, double[] vec2) {
        // Computes the cosine similarity between two vectors.
        double dotProduct = 0.0;
        double normVec1 = 0.0;
        double normVec2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            // Iterates through the vector elements.
            dotProduct += vec1[i] * vec2[i];
            // Computes the dot product.
            normVec1 += vec1[i] * vec1[i];
            // Computes the norm (magnitude) of the first vector.
            normVec2 += vec2[i] * vec2[i];
            // Computes the norm (magnitude) of the second vector.
        }

        return dotProduct / (Math.sqrt(normVec1) * Math.sqrt(normVec2));
        // Returns the cosine similarity.
    }

    
    private static String getOpenAIResponse(String prompt) {
        // Sends a prompt to the OpenAI API and returns the response.
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        // OpenAI API endpoint.

        /*
        try catch block to create out htttp client
         */
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

// was not sure what model name looked like so I defualted to 3.5
            String payload = """
                {
                    "model": "gpt-3.5-turbo", 
                    "messages": [
                        {"role": "user", "content": "%s"}
                    ],
                    "max_tokens": 100
                }
                """.formatted(prompt);
            // Constructs the request payload with the user prompt.

            HttpPost request = new HttpPost(apiUrl);
            // Creates an HTTP POST request.

            request.setHeader("Content-Type", "application/json");
            // Sets the content type to JSON.

            request.setHeader("Authorization", "Bearer " + OPENAI_API_KEY);
            // Adds the API key for authorization.

            request.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));
            // Sets the request body with the JSON payload.

            var response = httpClient.execute(request);
            // Sends the request and gets the response.

            String responseContent = new String(response.getEntity().getContent().readAllBytes());
            // Reads the response content as a string.

            return responseContent;
            // Returns the response content.
        } catch (Exception e) {
            e.printStackTrace();
            // Prints the error stack trace for debugging.
            return "Error: Unable to connect to OpenAI API.";
            // Returns an error message.
        }
    }
}
