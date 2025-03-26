package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.checked.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.BuildStepsPage;
import com.example.teamcity.ui.pages.admin.CreateBuildPage;
import io.qameta.allure.Allure;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.example.teamcity.api.enums.Endpoint.PROJECTS;

@Test(groups = {"Regression"})
public class CreateBuildConfigurationTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "User should be able to create build configuration", groups = {"Positive"})
    public void userCreatesBuildConfiguration() {

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        Allure.step("Create project via API", () ->
                userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject())
        );

        Allure.step("Create build configuration via UI", () ->
                CreateBuildPage.createWholeBuildConfiguration(testData.getProject().getId(), REPO_URL, testData.getBuildType().getName())
        );

        Allure.step("Verify build configuration creation message in UI", () ->
                BuildStepsPage.BuildConfigurationCreatedSuccessfullyMessage.shouldHave(Condition.text("successfully created"))
        );

        var createdBuildType = Allure.step("Verify build configuration was created via API", () ->
                userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getName())
        );

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User can't create a build configuration with an empty Build Configuration Name", groups = {"Negative"})
    public void userCanNotCreateBuildConfWithEmptyName() {

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        Allure.step("Create project via API", () ->
                userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject())
        );

        Allure.step("Attempt to create build configuration with empty name via UI", () ->
                CreateBuildPage.createWholeBuildConfiguration(testData.getProject().getId(), REPO_URL, "")
        );

        Allure.step("Verify error message in UI", () ->
                CreateBuildPage.buildTypeNameError.shouldHave(Condition.exactText("Build configuration name must not be empty"))
        );

        Allure.step("Verify build configuration was not created via API", () ->
                new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                        .read(testData.getBuildType().getName())
                        .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                        .body(Matchers.containsString("No build type or template is found by id"))
        );
    }
}
