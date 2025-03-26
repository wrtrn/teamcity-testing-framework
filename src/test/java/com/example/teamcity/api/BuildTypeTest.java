package com.example.teamcity.api;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.checked.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import io.qameta.allure.Allure;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD", "Regression"})
    public void userCreatesBuildTypeTest() {

        Allure.step("Create super user", () ->
                superUserCheckRequests.getRequest(USERS).create(testData.getUser())
        );
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));


        Allure.step("Creating project", () ->
                userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject())
        );

        Allure.step("Creating build type", () ->
                userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType())
        );

        var createdBuildType = Allure.step("Read created build type", () ->
                userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId())
        );

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }


    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD", "Regression"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        Allure.step("Create super user", () ->
                superUserCheckRequests.getRequest(USERS).create(testData.getUser())
        );

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        Allure.step("Create project", () ->
                userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject())
        );

        Allure.step("Create first build type", () ->
                userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType())
        );

        Allure.step("Attempt to create second build type with the same ID and verify error", () ->
                new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                        .create(buildTypeWithSameId)
                        .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                        .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())))
        );
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles", "Regression"})
    public void projectAdminCreatesBuildTypeTest() {
        String projectId = Allure.step("Create project and get ID", () ->
                superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject()).getId()
        );

        Allure.step("Create project admin user", () ->
                createProjectAndUser("PROJECT_ADMIN", "p:" + projectId)
        );

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        Allure.step("Create build type", () ->
                userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType())
        );

        var createdBuildType = Allure.step("Read created build type", () ->
                userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId())
        );

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles", "Regression"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        String project1Id = Allure.step("Create first project and get ID", () ->
                superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject()).getId()
        );

        Allure.step("Create first project admin user", () ->
                createProjectAndUser("PROJECT_ADMIN", "p:" + project1Id)
        );

        testData = generate();

        String project2Id = Allure.step("Create second project and get ID", () ->
                superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject()).getId()
        );

        Allure.step("Create second project admin user", () ->
                createProjectAndUser("PROJECT_ADMIN", "p:" + project2Id)
        );

        var buildType2WithBuildType1Id = testData.getBuildType().toBuilder().project(Project.builder().id(project1Id).build()).build();

        Allure.step("Attempt to create build type in another user's project and verify error", () ->
                new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                        .create(buildType2WithBuildType1Id)
                        .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                        .body(Matchers.containsString("You do not have enough permissions to edit project with id: " + project1Id + "\nAccess denied. Check the user has enough permissions to perform the operation."))
        );
    }
}
