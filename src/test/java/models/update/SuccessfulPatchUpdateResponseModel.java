package models.update;

public record SuccessfulPatchUpdateResponseModel(int id, String username, String firstName, String lastName,
                                                 String email, String remoteAddr) {
}
