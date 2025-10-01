package com.innovationcenter.scholarapi.view;

import com.innovationcenter.scholarapi.model.Author;
import com.innovationcenter.scholarapi.model.AuthorSearchResult;
import com.innovationcenter.scholarapi.model.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Adapter to make existing ConsoleView compatible with new UserInterface.
 * Follows Adapter Pattern and Open/Closed Principle.
 */
public class ConsoleViewAdapter implements UserInterface {
    
    private final ConsoleView consoleView;
    
    public ConsoleViewAdapter() {
        this.consoleView = new ConsoleView();
    }
    
    @Override
    public void showWelcomeMessage() {
        consoleView.displayWelcome();
    }
    
    @Override
    public void showGoodbyeMessage() {
        System.out.println("\nThank you for using Google Scholar Author Search!");
        System.out.println("Innovation Center - University of Northern Mexico");
        System.out.println("=========================================");
    }
    
    @Override
    public void showSuccessMessage(String message) {
        System.out.println("\n✓ " + message);
        System.out.println();
    }
    
    @Override
    public void showErrorMessage(String message) {
        System.out.println("\n✗ Error: " + message);
        System.out.println();
    }
    
    @Override
    public void showWarningMessage(String message) {
        System.out.println("\n⚠ Warning: " + message);
        System.out.println();
    }
    
    @Override
    public void showSearchingMessage(String query) {
        System.out.println("\n🔍 Searching for authors matching: " + query + "...");
    }
    
    @Override
    public void showNoResultsMessage(String query) {
        System.out.println("\nNo authors found matching: '" + query + "'");
        System.out.println("Try a different search term or check spelling.");
    }
    
    @Override
    public int showMainMenu(Scanner scanner) {
        consoleView.displayMenu();
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // Invalid input
        }
    }
    
    @Override
    public String promptForAuthorName(Scanner scanner) {
        return consoleView.getSearchQuery();
    }
    
    @Override
    public int promptForAuthorSelection(Scanner scanner, int maxOptions) {
        System.out.print("\nEnter author number for details (or 0 to continue): ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0; // Invalid input, continue
        }
    }
    
    @Override
    public void showSearchResults(List<AuthorSearchResult> results, String query) {
        // Convert AuthorSearchResult list to SearchResult for compatibility
        SearchResult searchResult = new SearchResult(query);
        List<Author> authors = new ArrayList<>();
        
        for (AuthorSearchResult result : results) {
            if (result.getAuthor() != null) {
                authors.add(result.getAuthor());
            }
        }
        
        searchResult.setAuthors(authors);
        consoleView.displaySearchResults(searchResult);
    }
    
    @Override
    public void showAuthorDetails(Author author) {
        consoleView.displayAuthorDetails(author);
    }
    
    @Override
    public void showSearchHistory(int totalSearches) {
        System.out.println("\n--- Search History ---");
        System.out.println("Total searches performed: " + totalSearches);
        System.out.println("History tracking is available in future versions.");
    }
}