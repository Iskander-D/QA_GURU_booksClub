package models.clubs;

import java.util.List;

public record ClubsListResponseModel(
        int count,
        String next,
        String previous,
        List<ClubModel> results
) {
}
