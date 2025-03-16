package com.example.teamcity.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage extends BasePage {
    private SelenideElement jumpToTextField = $("[id='search-projects']");
    public ElementsCollection projects = $$("[data-test-itemtype='project']");

    public MainPage enterTextToJumpToField(String text) {
        jumpToTextField.setValue(text);
        return this;
    }
}
