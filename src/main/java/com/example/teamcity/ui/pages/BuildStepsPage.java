package com.example.teamcity.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class BuildStepsPage extends BasePage {
    private static final String BUILD_STEPS_PAGE_URL = "http://192.168.1.8:8111/admin/editBuildRunners.html?id=buildType:%s";
    private ElementsCollection steps = $$(".editBuildStepRow");
    public SelenideElement runButton = $("[class~='runFirstBuild']");
    public static SelenideElement BuildConfigurationCreatedSuccessfullyMessage = $("#unprocessed_objectsCreated.successMessage");

    public void editBuildStep(int stepNumber) {
        steps.get(stepNumber).click();
    }

    public static BuildStepsPage open(String buildTypeId) {
        return Selenide.open(BUILD_STEPS_PAGE_URL.formatted(buildTypeId), BuildStepsPage.class);
    }
}
