package com.library.book_management;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Optional;

// @Service
@Path("/books")
public class BookService {

    @Autowired
    BookRepository bookRepository;

    // Fetch all books
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Find a book by ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book getBookById(@PathParam("id") Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return book.get();
        } else {
            throw new IllegalArgumentException("Book with ID " + id + " not found.");
        }
    }

    // Find a book by ISBN
    @GET
    @Path("/isbn/{isbn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book getBookByIsbn(@PathParam("isbn") String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book with ISBN " + isbn + " not found."));
    }

    // Search books by title or genre
    @GET
    @Path("/search/{keyword}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> searchBooks(@PathParam("keyword") String keyword) {
        return bookRepository.findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(keyword, keyword);
    }

    // Find books by author
    @GET
    @Path("/author/{author}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> getBooksByAuthor(@PathParam("author") String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    // Add a new book
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Book addBook(Book book) {
        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("A book with the ISBN " + book.getIsbn() + " already exists.");
        }
        return bookRepository.save(book);
    }

    // Update an existing book
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Book updateBook(@PathParam("id") Long id, Book updatedBook) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(updatedBook.getTitle());
                    existingBook.setAuthor(updatedBook.getAuthor());
                    existingBook.setIsbn(updatedBook.getIsbn());
                    existingBook.setGenre(updatedBook.getGenre());
                    existingBook.setPublishedYear(updatedBook.getPublishedYear());
                    existingBook.setPublisher(updatedBook.getPublisher());
                    existingBook.setDescription(updatedBook.getDescription());
                    existingBook.setTotalCopies(updatedBook.getTotalCopies());
                    existingBook.setAvailableCopies(updatedBook.getAvailableCopies());
                    return bookRepository.save(existingBook);
                })
                .orElseThrow(() -> new IllegalArgumentException("Book with ID " + id + " not found."));
    }

    // Delete a book by ID
    @DELETE
    @Path("/{id}")
    public void deleteBook(@PathParam("id") Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book with ID " + id + " does not exist.");
        }
        bookRepository.deleteById(id);
    }

    // Lend a book (reduce available copies)
    @PUT
    @Path("/lend/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book lendBook(@PathParam("id") Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    if (book.getAvailableCopies() > 0) {
                        book.setAvailableCopies(book.getAvailableCopies() - 1);
                        return bookRepository.save(book);
                    } else {
                        throw new IllegalArgumentException("No available copies for book ID " + id);
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Book with ID " + id + " not found."));
    }

    // Return a book (increase available copies)
    @PUT
    @Path("/return/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book returnBook(@PathParam("id") Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    if (book.getAvailableCopies() < book.getTotalCopies()) {
                        book.setAvailableCopies(book.getAvailableCopies() + 1);
                        return bookRepository.save(book);
                    } else {
                        throw new IllegalArgumentException("All copies of book ID " + id + " are already available.");
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Book with ID " + id + " not found."));
    }
}