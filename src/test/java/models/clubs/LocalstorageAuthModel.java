package models.clubs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LocalstorageAuthModel (BookClubAuth book_club_auth){ //book_club_auth взято из схемы в console

    public record BookClubAuth(
            User user,
            String accessToken,
            String refreshToken,
            @JsonProperty("isAuthenticated") // Указываем точное имя из JSON из console
            boolean isAuthenticated
    ) {}

    public record User(
            Integer id,
            String username,
            String firstName,
            String lastName,
            String email,
            String remoteAddr
    ) {}
}

