package com.example.teamcity.ui;

import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.checked.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.BuildResultsPage;
import com.example.teamcity.ui.pages.BuildStepEditPage;
import com.example.teamcity.ui.pages.BuildStepsPage;
import com.example.teamcity.ui.pages.LogPage;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.text;
import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.example.teamcity.api.enums.Endpoint.PROJECTS;

@Test(groups = "Regression")
public class BuildRunTest extends BaseUiTest {
    @Test(description = "User should be able to run a build and get hello world in a console", groups = {"Positive"})
    public void userRunsBuildAndGetsHelloWorld() {
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        //создаем проект и билд тайп
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        //Открываем редактор билд степа
        BuildStepsPage.open(testData.getBuildType().getId())
                .editBuildStep(0);

        //Заполняем билд степ
        new BuildStepEditPage()
                .enterTextToCodeMirror("echo \"Hello, world!\"")
                .clickSaveButton();

        //Запускаем билд
        new BuildStepsPage().runButton.click();

        //Проверяем что в логе есть "Hello world"
        new BuildResultsPage().showFullLogButton.click();
        new LogPage().expandAllButton.click();
        new LogPage().logContainer.shouldHave(text("Hello, world!"));
    }
}
