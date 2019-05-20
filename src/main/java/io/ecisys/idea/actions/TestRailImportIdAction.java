package io.ecisys.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import io.ecisys.idea.Annotations;
import io.ecisys.idea.testRail.TestRailClient;

import java.util.Arrays;
import java.util.Optional;

public class TestRailImportIdAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        final PsiElement psiElement = event.getData(PlatformDataKeys.PSI_ELEMENT);
        if (psiElement instanceof PsiClass) {
            final PsiClass psiClass = (PsiClass) psiElement;
            Arrays.stream(psiClass.getMethods())
                    .filter(m -> m.hasAnnotation(Annotations.TESTRAIL_CASE_ANNOTATION))
                    .forEach(this::importCaseIdToAnnotationRefField);
        }
    }

    public void importCaseIdToAnnotationRefField(final PsiMethod method) {

        final String caseId = Optional.ofNullable(method.getAnnotation(Annotations.TESTRAIL_CASE_ANNOTATION))
                .map(a -> a.findDeclaredAttributeValue("value"))
                .map(PsiElement::getText)
                .get()
                .replace("\"", "");

        TestRailClient.updateAnnotationRefField(caseId, caseId);

    }

}
