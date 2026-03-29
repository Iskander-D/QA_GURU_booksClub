package clubPages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.confirm;
import static com.codeborne.selenide.Selenide.element;

public class ClubPage {

    private final SelenideElement clubContent = element(".club-content");
    private final SelenideElement leaveButton = element(".leave-btn");
    private final SelenideElement errorMessage = element(".error");
    private final SelenideElement reviewerName = element(".reviewer-name");

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
}