package com.library.book_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Custom query to find a book by its ISBN
    Optional<Book> findByIsbn(String isbn);

    // Custom query to find books by author
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Custom query to search for books by title or genre
    List<Book> findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(String title, String genre);

    // Custom query to find all books by a particular genre
    List<Book> findByGenreIgnoreCase(String genre);
}
