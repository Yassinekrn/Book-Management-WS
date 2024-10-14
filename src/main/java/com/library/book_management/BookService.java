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
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.WebApplicationException;

import java.util.List;
import java.util.Optional;

@Path("/books")
public class BookService {

    @Autowired
    BookRepository bookRepository;

    // Fetch all books
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return Response.ok(books).build(); // Return 200 OK with book list
    }

    // Find a book by ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookById(@PathParam("id") Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return Response.ok(book.get()).build(); // Return 200 OK
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Book with ID " + id + " not found.")
                    .build(); // Return 404 Not Found
        }
    }

    // Find a book by ISBN
    @GET
    @Path("/isbn/{isbn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookByIsbn(@PathParam("isbn") String isbn) {
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            return Response.ok(book.get()).build(); // Return 200 OK
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Book with ISBN " + isbn + " not found.")
                    .build(); // Return 404 Not Found
        }
    }

    // Add a new book
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBook(Book book) {
        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("A book with the ISBN " + book.getIsbn() + " already exists.")
                    .build(); // Return 400 Bad Request
        }
        Book savedBook = bookRepository.save(book);
        return Response.status(Response.Status.CREATED)
                .entity(savedBook)
                .build(); // Return 201 Created
    }

    // Update an existing book
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(@PathParam("id") Long id, Book updatedBook) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if (bookOpt.isPresent()) {
            Book existingBook = bookOpt.get();
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setIsbn(updatedBook.getIsbn());
            existingBook.setGenre(updatedBook.getGenre());
            existingBook.setPublishedYear(updatedBook.getPublishedYear());
            existingBook.setPublisher(updatedBook.getPublisher());
            existingBook.setDescription(updatedBook.getDescription());
            existingBook.setTotalCopies(updatedBook.getTotalCopies());
            existingBook.setAvailableCopies(updatedBook.getAvailableCopies());
            Book savedBook = bookRepository.save(existingBook);
            return Response.ok(savedBook).build(); // Return 200 OK
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Book with ID " + id + " not found.")
                    .build(); // Return 404 Not Found
        }
    }

    // Delete a book by ID
    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") Long id) {
        if (!bookRepository.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Book with ID " + id + " does not exist.")
                    .build(); // Return 404 Not Found
        }
        bookRepository.deleteById(id);
        return Response.noContent().build(); // Return 204 No Content
    }

    // Lend a book (reduce available copies)
    @PUT
    @Path("/lend/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lendBook(@PathParam("id") Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    if (book.getAvailableCopies() > 0) {
                        book.setAvailableCopies(book.getAvailableCopies() - 1);
                        return Response.ok(bookRepository.save(book)).build(); // Return 200 OK
                    } else {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("No available copies for book ID " + id)
                                .build(); // Return 400 Bad Request
                    }
                })
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("Book with ID " + id + " not found.")
                        .build()); // Return 404 Not Found
    }

    // Return a book (increase available copies)
    @PUT
    @Path("/return/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnBook(@PathParam("id") Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    if (book.getAvailableCopies() < book.getTotalCopies()) {
                        book.setAvailableCopies(book.getAvailableCopies() + 1);
                        return Response.ok(bookRepository.save(book)).build(); // Return 200 OK
                    } else {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("All copies of book ID " + id + " are already available.")
                                .build(); // Return 400 Bad Request
                    }
                })
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("Book with ID " + id + " not found.")
                        .build()); // Return 404 Not Found
    }

    // Check if a book is available for loan
    @GET
    @Path("/{id}/availability")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isBookAvailable(@PathParam("id") Long id) {
        return bookRepository.findById(id)
                .map(book -> Response.ok(book.getAvailableCopies() > 0).build()) // Return 200 OK with boolean
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("Book with ID " + id + " not found.")
                        .build()); // Return 404 Not Found
    }
}
