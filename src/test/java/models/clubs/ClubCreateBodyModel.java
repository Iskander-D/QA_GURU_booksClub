package models.clubs;

public record ClubCreateBodyModel(
        String bookTitle,
        String bookAuthors,
        int publicationYear,
        String description,
        String telegramChatLink
) {
}
