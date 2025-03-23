package com.example.teamcity.ui;

import com.codeborne.selenide.Selenide;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.checked.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.MainPage;
import org.testng.annotations.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.example.teamcity.api.enums.Endpoint.PROJECTS;

@Test(groups = {"Regression"})
public class SearchProjectByNameTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "", groups = {"Positive"})
    public void searchProjectByName() {
        String projectName = testData.getProject().getName();
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        //создаем проект
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        //обновляем страницу, чтобы увидеть созданный через АПИ проект
        Selenide.refresh();

        //проверяем, что у нас найден только один элемент и что его текст совпадает с сгенерированным именем.
        new MainPage()
                .enterTextToJumpToField(projectName)
                .projects.shouldHave(size(1))
                .first().shouldHave(text(projectName));
    }
}
