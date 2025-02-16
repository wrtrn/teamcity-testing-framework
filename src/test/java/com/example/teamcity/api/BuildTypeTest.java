package com.example.teamcity.api;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.requests.checked.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        String projectId = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject()).getId();

        //подумать как тут сократить
        var user = testData.getUser().toBuilder()
                .roles(Roles.builder()
                        .role(List.of(Role.builder().roleId("PROJECT_ADMIN").scope("p:" + projectId).build()))
                        .build())
                .build();

        superUserCheckRequests.getRequest(USERS).create(user);

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        //последние 3 строки совпадают с первым тестом
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        String project1Id = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject()).getId();

        var user1 = testData.getUser().toBuilder()
                .roles(Roles.builder()
                        .role(List.of(Role.builder().roleId("PROJECT_ADMIN").scope("p:" + project1Id).build()))
                        .build())
                .build();

        superUserCheckRequests.getRequest(USERS).create(user1);

        testData = generate();
        //дублирование создания проекта и юзера
        String projectId2 = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject()).getId();

        //подумать как тут сократить аналогично третьему тесту. Также код дублируется с третьим тестом
        var user2 = testData.getUser().toBuilder()
                .roles(Roles.builder()
                        .role(List.of(Role.builder().roleId("PROJECT_ADMIN").scope("p:" + projectId2).build()))
                        .build())
                .build();

        superUserCheckRequests.getRequest(USERS).create(user2);

        var buildType2WithBuildType1Id = testData.getBuildType().toBuilder().project(Project.builder().id(project1Id).build()).build();

        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildType2WithBuildType1Id)
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("You do not have enough permissions to edit project with id: " + project1Id + "\nAccess denied. Check the user has enough permissions to perform the operation."));
    }
}
