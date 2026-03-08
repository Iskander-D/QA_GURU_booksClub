package models.registration;


public record SuccessfulRegistrationResponseModel(int id, String username, String firstName,
                                                  String lastName, String email, String remoteAddr) {}
