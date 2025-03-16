package com.example.teamcity.ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class BuildResultsPage extends BasePage {
    public SelenideElement showFullLogButton = $("[data-test-build-log-overview='show-full-log']");
}
