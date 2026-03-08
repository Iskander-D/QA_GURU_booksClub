package models.logout;

import java.util.List;

public record EmptyOrNullLogoutResponseModel (List<String> refresh) {
}
