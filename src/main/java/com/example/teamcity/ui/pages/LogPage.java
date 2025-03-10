package com.example.teamcity.ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LogPage extends BasePage {
    public SelenideElement expandAllButton = $("[data-test-full-build-log='expand']");
    public SelenideElement logContainer = $("[data-hint-container-id='buildlog-keyboard-navigation']");
}
