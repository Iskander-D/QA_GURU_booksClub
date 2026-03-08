package models.update;

public record SuccessfulPutUpdateResponseModel(int id, String username, String firstName, String lastName,
                                               String email, String remoteAddr) {}
