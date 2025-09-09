import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A basic AI-based recommendation system using user-based collaborative filtering.
 * This system recommends items to a user based on the preferences of similar users.
 */
public class RecommendationSystem {
    
    public static void main(String[] args) {
        // Sample user-item ratings data. Ratings are on a scale of 1 to 5.
        Map<String, Map<String, Double>> userRatings = new HashMap<>();
        userRatings.put("User1", Map.of("ItemA", 5.0, "ItemB", 3.0, "ItemC", 2.0));
        userRatings.put("User2", Map.of("ItemA", 4.0, "ItemB", 4.0, "ItemD", 5.0));
        userRatings.put("User3", Map.of("ItemB", 2.0, "ItemC", 5.0, "ItemD", 1.0));
        userRatings.put("User4", Map.of("ItemA", 1.0, "ItemC", 4.0, "ItemD", 4.0));
        userRatings.put("User5", Map.of("ItemA", 5.0, "ItemB", 4.0, "ItemD", 1.0));


        // Get recommendations for a target user
        String targetUser = "User1";
        System.out.println("Recommendations for " + targetUser + ":");
        Map<String, Double> recommendations = getRecommendations(targetUser, userRatings);

        // Check if recommendations are available
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations found for " + targetUser);
            return;
        }

        // Sort recommendations by score in descending order and print them
        recommendations.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> System.out.println(
                        entry.getKey() + " (Predicted Rating: " + String.format("%.2f", entry.getValue()) + ")"
                ));
    }

    /**
     * Generates item recommendations for a target user by finding similar users.
     *
     * @param targetUser  The user for whom to generate recommendations.
     * @param userRatings A map containing all user ratings data.
     * @return A map of recommended items and their predicted rating scores.
     */
    public static Map<String, Double> getRecommendations(String targetUser, Map<String, Map<String, Double>> userRatings) {
        Map<String, Double> targetUserRatings = userRatings.get(targetUser);
        if (targetUserRatings == null) {
            return new HashMap<>(); // Return empty map if target user doesn't exist
        }

        Map<String, Double> recommendationScores = new HashMap<>();
        Map<String, Double> totalSimilarity = new HashMap<>();

        // Iterate through all other users to find similarities
        for (String otherUser : userRatings.keySet()) {
            if (!otherUser.equals(targetUser)) {
                double similarity = pearsonCorrelation(targetUserRatings, userRatings.get(otherUser));

                // Consider only users with positive similarity
                if (similarity > 0) {
                    // Iterate through items rated by the similar user
                    for (Map.Entry<String, Double> entry : userRatings.get(otherUser).entrySet()) {
                        String item = entry.getKey();
                        double rating = entry.getValue();

                        // Recommend only items that the target user has NOT rated
                        if (!targetUserRatings.containsKey(item)) {
                            // Accumulate weighted ratings
                            recommendationScores.merge(item, rating * similarity, Double::sum);
                            // Accumulate total similarity for normalization
                            totalSimilarity.merge(item, similarity, Double::sum);
                        }
                    }
                }
            }
        }

        // Calculate the final predicted rating for each recommended item
        Map<String, Double> recommendations = new HashMap<>();
        for (String item : recommendationScores.keySet()) {
            // Avoid division by zero
            if (totalSimilarity.get(item) > 0) {
                recommendations.put(item, recommendationScores.get(item) / totalSimilarity.get(item));
            }
        }
        return recommendations;
    }

    /**
     * Calculates the Pearson correlation coefficient between two users.
     * This measures the linear relationship between their ratings on commonly rated items.
     *
     * @param user1Ratings Ratings of the first user.
     * @param user2Ratings Ratings of the second user.
     * @return A similarity score between -1 (perfectly dissimilar) and 1 (perfectly similar).
     */
    private static double pearsonCorrelation(Map<String, Double> user1Ratings, Map<String, Double> user2Ratings) {
        // Find items that both users have rated
        Map<String, Double> commonItemsUser1 = user1Ratings.entrySet().stream()
                .filter(entry -> user2Ratings.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Double> commonItemsUser2 = user2Ratings.entrySet().stream()
                .filter(entry -> user1Ratings.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        int commonItemsCount = commonItemsUser1.size();
        if (commonItemsCount == 0) {
            return 0; // No common items, no correlation
        }

        // Calculate the average rating for each user on the common items
        double avg1 = commonItemsUser1.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avg2 = commonItemsUser2.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double numerator = 0;
        double denominatorPart1 = 0;
        double denominatorPart2 = 0;

        // Calculate the terms for the Pearson formula
        for (String item : commonItemsUser1.keySet()) {
            double rating1 = commonItemsUser1.get(item);
            double rating2 = commonItemsUser2.get(item);

            numerator += (rating1 - avg1) * (rating2 - avg2);
            denominatorPart1 += Math.pow(rating1 - avg1, 2);
            denominatorPart2 += Math.pow(rating2 - avg2, 2);
        }

        double denominator = Math.sqrt(denominatorPart1) * Math.sqrt(denominatorPart2);

        // Avoid division by zero
        return denominator == 0 ? 0 : numerator / denominator;
    }
}