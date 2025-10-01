package com.innovationcenter.scholarapi.view;

import com.innovationcenter.scholarapi.model.Author;
import com.innovationcenter.scholarapi.model.AuthorSearchResult;

import java.util.List;
import java.util.Scanner;

/**
 * Interface for view operations following Interface Segregation Principle.
 * Defines contract for user interface operations.
 */
public interface UserInterface {
    
    // Display methods
    void showWelcomeMessage();
    void showGoodbyeMessage();
    void showSuccessMessage(String message);
    void showErrorMessage(String message);
    void showWarningMessage(String message);
    void showSearchingMessage(String query);
    void showNoResultsMessage(String query);
    
    // Interactive methods
    int showMainMenu(Scanner scanner);
    String promptForAuthorName(Scanner scanner);
    int promptForAuthorSelection(Scanner scanner, int maxOptions);
    
    // Data display methods
    void showSearchResults(List<AuthorSearchResult> results, String query);
    void showAuthorDetails(Author author);
    void showSearchHistory(int totalSearches);
}