package org.example;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        DocumentManager manager = new DocumentManager();

        DocumentManager.Author author1 = DocumentManager.Author.builder()
                .id("author-1")
                .name("John Doe")
                .build();

        DocumentManager.Author author2 = DocumentManager.Author.builder()
                .id("author-2")
                .name("Jane Smith")
                .build();

        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .title("Java Basics")
                .content("This document covers Java fundamentals.")
                .author(author1)
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .title("Advanced Java")
                .content("This document is about Java Streams and concurrency.")
                .author(author1)
                .build();

        DocumentManager.Document doc3 = DocumentManager.Document.builder()
                .title("Python Intro")
                .content("This document is for Python beginners.")
                .author(author2)
                .build();

        manager.save(doc1);
        Thread.sleep(10);
        manager.save(doc2);
        Thread.sleep(10);
        manager.save(doc3);

        DocumentManager.SearchRequest searchByTitle = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Java"))
                .build();

        System.out.println("Пошук за назвою 'Java':");
        List<DocumentManager.Document> titleResults = manager.search(searchByTitle);
        titleResults.forEach(System.out::println);

        DocumentManager.SearchRequest searchByAuthor = DocumentManager.SearchRequest.builder()
                .authorIds(List.of("author-2"))
                .build();

        System.out.println("\nПошук за автором 'author-2':");
        List<DocumentManager.Document> authorResults = manager.search(searchByAuthor);
        authorResults.forEach(System.out::println);

        String idToFind = doc2.getId();
        Optional<DocumentManager.Document> foundById = manager.findById(idToFind);

        System.out.println("\nПошук за ID '" + idToFind + "':");
        foundById.ifPresent(System.out::println);

        Instant from = doc1.getCreated().minusSeconds(1);
        Instant to = doc3.getCreated().plusSeconds(1);
        DocumentManager.SearchRequest searchByDate = DocumentManager.SearchRequest.builder()
                .createdFrom(from)
                .createdTo(to)
                .build();

        System.out.println("\nПошук за датою створення (всі документи):");
        List<DocumentManager.Document> dateResults = manager.search(searchByDate);
        dateResults.forEach(System.out::println);
    }
}
