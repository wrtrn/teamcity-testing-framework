package com.example.teamcity.api;

import com.example.teamcity.BaseTest;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.models.User;

import java.util.List;

import static com.example.teamcity.api.enums.Endpoint.USERS;

public class BaseApiTest extends BaseTest {

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
