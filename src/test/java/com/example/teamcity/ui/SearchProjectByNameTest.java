package com.example.teamcity.ui;

import com.codeborne.selenide.Selenide;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.checked.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.MainPage;
import io.qameta.allure.Allure;
import org.testng.annotations.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.example.teamcity.api.enums.Endpoint.PROJECTS;

@Test(groups = {"Regression"})
public class SearchProjectByNameTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "User should be able to search for a project by name", groups = {"Positive"})
    public void searchProjectByName() {
        String projectName = testData.getProject().getName();
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        Allure.step("Create project via API", () ->
                userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject())
        );

        Allure.step("Refresh page to update project list", Selenide::refresh);

        Allure.step("Search for project by name in UI and verify it appears", () ->
                new MainPage()
                        .enterTextToJumpToField(projectName)
                        .projects.shouldHave(size(1))
                        .first().shouldHave(text(projectName))
        );
    }
}
