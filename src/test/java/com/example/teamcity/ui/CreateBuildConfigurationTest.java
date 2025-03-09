package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.checked.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.BuildStepsPage;
import com.example.teamcity.ui.pages.admin.CreateBuildPage;
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

        // создаем проект через API
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        // взаимодействие с UI
        CreateBuildPage.createWholeBuildConfiguration(testData.getProject().getId(), REPO_URL, testData.getBuildType().getName());

        //проверяем что билд тайп успешно создался на UI
        BuildStepsPage.BuildConfigurationCreatedSuccessfullyMessage.shouldHave(Condition.text("successfully created"));

        //проверяем, что билд тайп успешно создался через API
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getName());
        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User can't create a build configuration with an empty Build Configuration Name", groups = {"Negative"})
    public void userCanNotCreateBuildConfWithEmptyName() {

        // создаем проект через API
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        // взаимодействие с UI
        CreateBuildPage.createWholeBuildConfiguration(testData.getProject().getId(), REPO_URL, "");

        //проверяем, что билд тайп не создался через UI
        CreateBuildPage.buildTypeNameError.shouldHave(Condition.exactText("Build configuration name must not be empty"));

        //проверяем, что билд тайп не создался через API
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .read(testData.getBuildType().getName())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString("No build type or template is found by id"));
    }
}
