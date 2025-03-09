package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildPage extends CreateBasePage{
    private static final String PROJECT_SHOW_MODE = "createBuildTypeMenu";

    private SelenideElement buildConfigurationNameInput = $("#buildTypeName");
    public static SelenideElement buildTypeNameError = $("#error_buildTypeName");

    public static CreateBuildPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, PROJECT_SHOW_MODE), CreateBuildPage.class);
    }

    public CreateBuildPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public void setupBuildConfiguration(String buildConfigurationName) {
        buildConfigurationNameInput.val(buildConfigurationName);
        submitButton.click();
    }

    public static void createWholeBuildConfiguration(String projectId, String repoUrl, String buildConfigurationName)
    {
        CreateBuildPage.open(projectId)
                .createForm(repoUrl)
                .setupBuildConfiguration(buildConfigurationName);
    }
}
