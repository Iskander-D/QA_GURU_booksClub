package models.clubs;

import java.util.List;

public record ClubModel(
        int id,
        String bookTitle,
        String bookAuthors,
        int publicationYear,
        String description,
        String telegramChatLink,
        int owner,
        List<Integer> members,
        List<ClubReviewModel> reviews,
        String created,
        String modified
) {
}
