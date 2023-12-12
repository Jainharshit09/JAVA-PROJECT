package review;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Review {
    private static String[] userIds = new String[100];  
    private static String[] reviews = new String[100];  
    private static float[] ratings = new float[100];    
    private static int reviewCount = 0;


    public static void addReviewAndRating(String username) {
    Scanner scanner = new Scanner(System.in);
    
    do {
        try {
            System.out.print("Please give the review (characters only): ");
            String userReview = scanner.nextLine();
            if (userReview.matches("[a-zA-Z]+")) {
                System.out.print("Rating (1-5): ");
                float userRating = scanner.nextFloat();
                if (userRating < 1 || userRating > 5) {
                    System.out.println("Invalid rating. Please enter a number between 1 and 5.");
                  
                } else {
                    userIds[reviewCount] = username;
                    reviews[reviewCount] = userReview;
                    ratings[reviewCount] = userRating;
                    reviewCount++;
                    break;
                }
            } else {
                System.out.println("Invalid review. Please enter characters only.");
              
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid numeric rating.");
            scanner.nextLine(); // Consume the invalid input
         
        }
    } while (true);
}
    public static  String getReview() {
        return reviews[reviewCount - 1];
    }
    public static  float getRating() {
        return ratings[reviewCount - 1];
    }
}
