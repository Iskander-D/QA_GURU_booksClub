package clubPages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ClubPage extends BasePage {

    private final SelenideElement clubContent = element(".club-content");
    private final SelenideElement leaveButton = element(".leave-btn");
    private final SelenideElement errorMessage = element(".error");
    private final SelenideElement reviewerName = element(".reviewer-name");
    private final SelenideElement checkDeleteReview = element(".no-reviews");
    private final SelenideElement error = $(".error");

    @Step("Проверить, что контент клуба отображается")
    public ClubPage checkVisible() {
        clubContent.shouldBe(visible);
        return this;
    }

    @Step("Попытаться покинуть клуб")
    public ClubPage leaveClub() {
        leaveButton.click();
        confirm(); // клик в браузере на OK в alert
        return this;
    }

    @Step("Проверить текст ошибки: {expectedText}")
    public void checkError(String expectedText) {
        errorMessage.shouldHave(text(expectedText));
    }

    @Step("Проверить имя рецензента: {name}")
    public void checkReviewerName(String name) {
        reviewerName.shouldHave(text(name));
    }

    @Step("Проверить, что в карточке клуба виден фрагмент текста отзыва: {snippet}")
    public ClubPage checkReviewSnippetVisible(String snippet) {
        clubContent.shouldHave(text(snippet));
        return this;
    }

    @Step("Проверить, что отзыв удалён и отображается сообщение: {text}")
    public ClubPage checkReviewDeleted(String text) {
        checkDeleteReview.shouldHave(text(text));
        return this;
    }

    @Step("[UI] Открытие страницы клуба по id: {clubId}")
    public ClubPage openPageById(String clubId) {
        open("/clubs/" + clubId);

        return this;
    }


    @Step("[UI] Владелец не может покинуть клуб: подтверждение и сообщение об ошибке")
    public ClubPage assertOwnerCannotLeaveClub() {
        error.shouldHave(text("Не удалось покинуть клуб"));
        return this;
    }
}