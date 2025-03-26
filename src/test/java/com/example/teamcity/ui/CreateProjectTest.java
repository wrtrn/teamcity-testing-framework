package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import io.qameta.allure.Allure;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "User should be able to create project", groups = {"Positive"})
    public void userCreatesProject() {

        Allure.step("Open project creation page and create project", () ->
                CreateProjectPage.open("_Root")
                        .createForm(REPO_URL)
                        .setupProject(testData.getProject().getName(), testData.getBuildType().getName())
        );

        var createdProject = Allure.step("Verify project was created via API", () ->
                superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).read("name:" + testData.getProject().getName())
        );
        softy.assertNotNull(createdProject, "Project should be created");

        Allure.step("Verify project title in UI", () ->
                ProjectPage.open(createdProject.getId())
                        .title.shouldHave(Condition.exactText(testData.getProject().getName()))
        );

        var foundProjects = Allure.step("Verify project appears in project list", () ->
                ProjectsPage.open()
                        .getProjects().stream()
                        .anyMatch(project -> project.getName().text().equals(testData.getProject().getName()))
        );

        softy.assertTrue(foundProjects, "Project should be present in the list");
    }

    @Test(description = "User should not be able to craete project without name", groups = {"Negative"})
    public void userCreatesProjectWithoutName() {
        // подготовка окружения
        step("Login as user");
        step("Check number of projects");

        // взаимодействие с UI
        step("Open `Create Project Page` (http://localhost:8111/admin/createObjectMenu.html)");
        step("Send all project parameters (repository URL)");
        step("Click `Proceed`");
        step("Set Project Name");
        step("Click `Proceed`");

        // проверка состояния API
        // (корректность отправки данных с UI на API)
        step("Check that number of projects did not change");

        // проверка состояния UI
        // (корректность считывания данных и отображение данных на UI)
        step("Check that error appears `Project name must not be empty`");
    }
}