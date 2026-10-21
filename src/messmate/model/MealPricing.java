package messmate.model;

/**
 * Utility class holding fixed mess pricing constants.
 * Demonstrates Syllabus Module 2:
 * - Static Members (accessible without creating an instance)
 * - Final Variables (constant values that cannot be reassigned)
 */
public class MealPricing {
    // Fixed meal rates in Rupees
    public static final double BREAKFAST_PRICE = 80.0;
    public static final double LUNCH_PRICE = 130.0;
    public static final double SNACKS_PRICE = 40.0;
    public static final double DINNER_PRICE = 180.0;

    // Private constructor prevents instantiation
    private MealPricing() {
    }

    /**
     * Static helper method to calculate total cost
     */
    public static double calculateCost(boolean breakfast, boolean lunch, boolean snacks, boolean dinner) {
        double total = 0.0;
        if (breakfast) total += BREAKFAST_PRICE;
        if (lunch) total += LUNCH_PRICE;
        if (snacks) total += SNACKS_PRICE;
        if (dinner) total += DINNER_PRICE;
        return total;
    }
}
