package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    /**
     * Storage of documents.
     */
    private final Map<String, Document> database = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (isNewDocument(document)) {
            return saveNewDocument(document);
        }
        else {
            return updateExistingDocument(document);
        }
    }

    /**
     * Determines if the document is new (has no ID or empty ID)
     *
     * @param document The document to check
     * @return true if the document is new, false otherwise
     */
    private boolean isNewDocument(Document document) {
        return document.getId() == null || document.getId().trim().isEmpty();
    }

    /**
     * Saves a new document by generating a unique ID and setting creation time if needed
     *
     * @param document The new document to save
     * @return The document with generated ID and creation timestamp
     */
    private Document saveNewDocument(Document document) {
        document.setId(UUID.randomUUID().toString());

        if (document.getCreated() == null) {
            document.setCreated(Instant.now());
        }

        database.put(document.getId(), document);
        return document;
    }

    /**
     * Updates an existing document while preserving its original creation timestamp
     *
     * @param document The document to update
     * @return The updated document
     */
    private Document updateExistingDocument(Document document) {
        Document existingDocument = database.get(document.getId());

        if (existingDocument != null && document.getCreated() == null) {
            document.setCreated(existingDocument.getCreated());
        }
        else if (document.getCreated() == null) {
            document.setCreated(Instant.now());
        }

        database.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }

        List<Document> result = new ArrayList<>();

        for (Document doc : database.values()) {
            if (matchesAllCriteria(doc, request)) {
                result.add(doc);
            }
        }

        return result;
    }

    /**
     * Checks if a document matches all the criteria specified in the search request
     *
     * @param document the document to check
     * @param request the search request containing filter criteria
     * @return true if the document matches all criteria, false otherwise
     */
    private boolean matchesAllCriteria(Document document, SearchRequest request) {
        return matchesTitle(document, request.getTitlePrefixes()) &&
                matchesContent(document, request.getContainsContents()) &&
                matchesAuthor(document, request.getAuthorIds()) &&
                matchesCreationTime(document, request.getCreatedFrom(), request.getCreatedTo());
    }

    /**
     * Checks if the document's title starts with any of the specified prefixes
     *
     * @param doc The document to check
     * @param titlePrefixes List of title prefixes to match against
     * @return true if no prefixes specified or if title matches any prefix, false otherwise
     */
    private boolean matchesTitle(Document doc, List<String> titlePrefixes) {
        if (titlePrefixes == null || titlePrefixes.isEmpty()) {
            return true;
        }

        if (doc.getTitle() == null) {
            return false;
        }

        for (String prefix : titlePrefixes) {
            if (doc.getTitle().startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the document's content contains any of the specified strings
     *
     * @param doc The document to check
     * @param containsContents List of strings to search for in the content
     * @return true if no content filter specified or if content contains any of the strings, false otherwise
     */
    private boolean matchesContent(Document doc, List<String> containsContents) {
        if (containsContents == null || containsContents.isEmpty()) {
            return true;
        }

        if (doc.getContent() == null) {
            return false;
        }

        for (String content : containsContents) {
            if (doc.getContent().contains(content)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the document's author ID matches any of the specified author IDs
     *
     * @param doc The document to check
     * @param authorIds List of author IDs to match against
     * @return true if no author filter specified or if author matches any ID, false otherwise
     */
    private boolean matchesAuthor(Document doc, List<String> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return true;
        }

        if (doc.getAuthor() == null || doc.getAuthor().getId() == null) {
            return false;
        }

        return authorIds.contains(doc.getAuthor().getId());
    }

    /**
     * Checks if the document's creation time falls within the specified time range
     *
     * @param doc The document to check
     * @param createdFrom The start of the time range (inclusive), or null for no lower bound
     * @param createdTo The end of the time range (inclusive), or null for no upper bound
     * @return true if the creation time is within the specified range, false otherwise
     */
    private boolean matchesCreationTime(Document doc, Instant createdFrom, Instant createdTo) {
        if (doc.getCreated() == null) {
            return false;
        }

        if (createdFrom != null && doc.getCreated().isBefore(createdFrom)) {
            return false;
        }

        if (createdTo != null && doc.getCreated().isAfter(createdTo)) {
            return false;
        }

        return true;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(database.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}