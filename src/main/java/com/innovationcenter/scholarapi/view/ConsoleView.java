package com.innovationcenter.scholarapi.view;

import com.innovationcenter.scholarapi.model.Author;
import com.innovationcenter.scholarapi.model.Publication;
import com.innovationcenter.scholarapi.model.SearchResult;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based view implementation for displaying author search results.
 * Provides user interaction through command line interface.
 */
public class ConsoleView {
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Display the application header and welcome message.
     */
    public void displayWelcome() {
        System.out.println("=========================================");
        System.out.println("  Google Scholar Author Search System  ");
        System.out.println("  Innovation Center - University       ");
        System.out.println("=========================================");
        System.out.println();
    }

    /**
     * Display the main menu options.
     */
    public void displayMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Search for Authors");
        System.out.println("2. View Search History");
        System.out.println("3. Exit");
        System.out.print("Select an option (1-3): ");
    }

    /**
     * Get search query input from user.
     * @return The search query string
     */
    public String getSearchQuery() {
        System.out.print("\nEnter author name to search: ");
        return scanner.nextLine().trim();
    }

    /**
     * Get menu choice from user.
     * @return The selected menu option
     */
    public int getMenuChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // Invalid choice
        }
    }

    /**
     * Display search results in a formatted manner.
     * @param searchResult The search results to display
     */
    public void displaySearchResults(SearchResult searchResult) {
        if (searchResult == null || !searchResult.hasResults()) {
            displayNoResults();
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.printf("Search Results for: '%s'\n", searchResult.getSearchQuery());
        System.out.printf("Found %d author(s)\n", searchResult.getResultCount());
        System.out.println("=".repeat(60));

        List<Author> authors = searchResult.getAuthors();
        for (int i = 0; i < authors.size(); i++) {
            displayAuthorSummary(authors.get(i), i + 1);
        }

        if (searchResult.hasNextPage()) {
            System.out.println("\n[More results available on next page]");
        }
    }

    /**
     * Display detailed information about a specific author.
     * @param author The author to display
     */
    public void displayAuthorDetails(Author author) {
        if (author == null) {
            System.out.println("No author information available.");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("AUTHOR DETAILS");
        System.out.println("=".repeat(50));
        
        System.out.printf("Name: %s\n", author.getName() != null ? author.getName() : "N/A");
        System.out.printf("ID: %s\n", author.getAuthorId() != null ? author.getAuthorId() : "N/A");
        System.out.printf("Affiliation: %s\n", author.getAffiliation() != null ? author.getAffiliation() : "N/A");
        System.out.printf("Email: %s\n", author.getEmail() != null ? author.getEmail() : "N/A");
        System.out.printf("Research Interests: %s\n", author.getInterests() != null ? author.getInterests() : "N/A");
        
        System.out.println("\n--- Citation Metrics ---");
        System.out.printf("Total Citations: %,d\n", author.getCitedBy());
        System.out.printf("h-index: %d\n", author.getHIndex());
        System.out.printf("i10-index: %d\n", author.getI10Index());
        
        if (author.getPublications() != null && !author.getPublications().isEmpty()) {
            System.out.printf("\nRecent Publications (%d total):\n", author.getPublications().size());
            displayPublications(author.getPublications(), 5); // Show first 5
        }
    }

    /**
     * Display a summary of an author (used in search results list).
     * @param author The author to display
     * @param index The position in the results list
     */
    private void displayAuthorSummary(Author author, int index) {
        System.out.printf("\n%d. %s\n", index, author.getName() != null ? author.getName() : "Unknown Author");
        System.out.printf("   Affiliation: %s\n", author.getAffiliation() != null ? author.getAffiliation() : "N/A");
        System.out.printf("   Citations: %,d | h-index: %d | Publications: %d\n", 
            author.getCitedBy(), author.getHIndex(), 
            author.getPublications() != null ? author.getPublications().size() : 0);
        
        if (author.getInterests() != null && !author.getInterests().isEmpty()) {
            String interests = author.getInterests().length() > 60 ? 
                author.getInterests().substring(0, 57) + "..." : author.getInterests();
            System.out.printf("   Research Areas: %s\n", interests);
        }
    }

    /**
     * Display a list of publications.
     * @param publications The publications to display
     * @param limit Maximum number of publications to show
     */
    private void displayPublications(List<Publication> publications, int limit) {
        int count = Math.min(publications.size(), limit);
        for (int i = 0; i < count; i++) {
            Publication pub = publications.get(i);
            System.out.printf("  %d. %s\n", i + 1, pub.getTitle() != null ? pub.getTitle() : "Untitled");
            System.out.printf("     Authors: %s\n", pub.getAuthors() != null ? pub.getAuthors() : "N/A");
            System.out.printf("     Year: %d | Citations: %,d\n", pub.getYear(), pub.getCitedBy());
            
            if (pub.getVenue() != null && !pub.getVenue().isEmpty()) {
                System.out.printf("     Published in: %s\n", pub.getVenue());
            }
            System.out.println();
        }
        
        if (publications.size() > limit) {
            System.out.printf("  ... and %d more publications\n", publications.size() - limit);
        }
    }

    /**
     * Display message when no search results are found.
     */
    public void displayNoResults() {
        System.out.println("\nNo authors found matching your search criteria.");
        System.out.println("Try using different keywords or check the spelling.");
    }

    /**
     * Display error messages.
     * @param message The error message to display
     */
    public void displayError(String message) {
        System.out.println("\n❌ ERROR: " + message);
    }

    /**
     * Display success messages.
     * @param message The success message to display
     */
    public void displaySuccess(String message) {
        System.out.println("\n✅ " + message);
    }

    /**
     * Display loading message during API requests.
     * @param message The loading message
     */
    public void displayLoading(String message) {
        System.out.println("\n🔄 " + message + "...");
    }

    /**
     * Ask user if they want to continue with another search.
     * @return true if user wants to continue, false otherwise
     */
    public boolean askToContinue() {
        System.out.print("\nWould you like to perform another search? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    /**
     * Display goodbye message.
     */
    public void displayGoodbye() {
        System.out.println("\nThank you for using Google Scholar Author Search!");
        System.out.println("Innovation Center - University of Northern Mexico");
        System.out.println("=========================================");
    }

    /**
     * Wait for user to press Enter before continuing.
     */
    public void waitForUser() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Clear the console screen (simulation).
     */
    public void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
     * Close the scanner resource.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}