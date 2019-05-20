package io.ecisys.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import io.ecisys.idea.Annotations;
import io.ecisys.idea.testRail.TestRailClient;
import io.ecisys.idea.util.PsiUtils;

import java.util.Arrays;
import java.util.Optional;

public class TestRailExportIdAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        final PsiElement psiElement = event.getData(PlatformDataKeys.PSI_ELEMENT);
        if (psiElement instanceof PsiClass) {
            final PsiClass psiClass = (PsiClass) psiElement;
            Arrays.stream(psiClass.getMethods())
                    .filter(m -> m.hasAnnotation(Annotations.TESTNG_TEST_ANNOTATION))
                    .forEach(this::createTestRailCaseAnnotation);
        }
    }

    private void createTestRailCaseAnnotation(final PsiMethod method) {
        final String caseName = Optional.ofNullable(method.getAnnotation(Annotations.TESTNG_TEST_ANNOTATION))
                .map(a -> a.findDeclaredAttributeValue("testName"))
                .map(PsiElement::getText)
                .get()
                .replace("\"", "");

        final String id = TestRailClient.getCaseId(caseName);

        final Project project = method.getProject();
        CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
            PsiUtils.addImport(method.getContainingFile(), Annotations.TESTRAIL_CASE_ANNOTATION);

            Optional.ofNullable(method.getAnnotation(Annotations.TESTRAIL_CASE_ANNOTATION)).ifPresent(PsiAnnotation::delete);
            PsiAnnotation tmsLinks = createTmsIssueAnnotation(method, id);
            method.getModifierList().addAfter(tmsLinks, method.getAnnotation(Annotations.TESTNG_TEST_ANNOTATION));

            PsiUtils.optimizeImports(method.getContainingFile());
        }), "Import TestRail case id", null);
    }

    private PsiAnnotation createTmsIssueAnnotation(final PsiMethod method, final String id) {
        return PsiUtils.createAnnotation(String.format("@%s(\"%s\")", Annotations.TESTRAIL_CASE_ANNOTATION, id), method);
    }
}
