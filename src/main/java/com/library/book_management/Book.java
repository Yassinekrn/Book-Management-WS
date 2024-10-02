package com.library.book_management;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented by MySQL
    @Getter
    @Setter
    private Long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String title;

    @Column(nullable = false)
    @Getter
    @Setter
    private String author;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String isbn;

    @Column(nullable = false)
    @Getter
    @Setter
    private String genre;

    @Column(nullable = false)
    @Getter
    @Setter
    private int publishedYear;

    @Column(nullable = false)
    @Getter
    @Setter
    private String publisher;

    @Column(columnDefinition = "TEXT", length = 1000)
    @Getter
    @Setter
    private String description;

    @Column(nullable = false)
    @Getter
    @Setter
    private int totalCopies;

    @Column(nullable = false)
    @Getter
    @Setter
    private int availableCopies;
}
