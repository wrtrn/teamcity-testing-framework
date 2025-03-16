package com.example.teamcity.api;

import com.example.teamcity.BaseTest;
import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.ServerAuthRequest;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.List;

import static com.example.teamcity.api.enums.Endpoint.USERS;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class BaseApiTest extends BaseTest {
    private final ServerAuthRequest serverAuthRequest = new ServerAuthRequest(Specifications.superUserSpec());
    private AuthModules authModules;
    private boolean perProjectPermissions;

    @BeforeSuite(alwaysRun = true)
    public void setUpServerAuthSettings() {
        // Получаем текущие настройки perProjectPermissions
        perProjectPermissions = serverAuthRequest.read().getPerProjectPermissions();

        authModules = generate(AuthModules.class);
        // Обновляем значение perProjectPermissions на true
        serverAuthRequest.update(ServerAuthSettings.builder()
                .perProjectPermissions(true)
                .modules(authModules)
                .build());
    }

    @AfterSuite(alwaysRun = true)
    public void cleanUpServerAuthSettings() {
        // Возвращаем настройке perProjectPermissions исходное значение
        serverAuthRequest.update(ServerAuthSettings.builder()
                .perProjectPermissions(perProjectPermissions)
                .modules(authModules)
                .build());
    }

    protected void createProjectAndUser(String role, String scope) {
        var user = getUserWithSpecificRoleAndScope(role, scope);

        superUserCheckRequests.getRequest(USERS).create(user);
    }

    protected User getUserWithSpecificRoleAndScope(String role, String scope) {
        return testData.getUser().toBuilder()
                .roles(Roles.builder()
                        .role(List.of(Role.builder().roleId(role).scope(scope).build()))
                        .build())
                .build();
    }
}