package models.clubs;

public record ClubReviewModel(
        int id,
        int club,
        ClubReviewUserModel user,
        String review,
        int assessment,
        int readPages,
        String created,
        String modified
) {
}
