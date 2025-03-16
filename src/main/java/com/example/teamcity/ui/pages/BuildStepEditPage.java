package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class BuildStepEditPage extends BasePage {
    private static final String BUILD_STEP_EDIT_URL = "http://192.168.1.8:8111/admin/editRunType.html?id=buildType:%s&runnerId=%s";
    private SelenideElement customScriptTextBox = $(".CodeMirror-lines");
    private SelenideElement saveButton = $("[name='submitButton']");
    SelenideElement codeMirrorTextField = $(".CodeMirror");


    public static BuildStepEditPage open(String buildTypeId) {
        return Selenide.open(BUILD_STEP_EDIT_URL.formatted(buildTypeId), BuildStepEditPage.class);
    }

    public BuildStepEditPage enterTextToCodeMirror(String text) {
        codeMirrorTextField.click();
        Selenide.actions().sendKeys(text).perform();
        return this;
    }

    public void clickSaveButton() {
        saveButton.click();
    }
}
