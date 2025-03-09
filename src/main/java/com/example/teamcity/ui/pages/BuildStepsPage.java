package com.example.teamcity.ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class BuildStepsPage extends BasePage {
    public static SelenideElement BuildConfigurationCreatedSuccessfullyMessage = $("#unprocessed_objectsCreated.successMessage");

}
