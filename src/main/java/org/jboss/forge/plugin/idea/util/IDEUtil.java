/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.util;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeJavaClassChooserDialog;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.ClassUtil;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.plugin.idea.context.UIContextImpl;

import java.io.File;

/**
 * @author Adam Wyłuda
 */
public class IDEUtil
{
    public static void refreshProject(UIContext context)
    {
        refreshProject(projectFromContext(context));
    }

    public static void refreshProject(Project project)
    {
        project.getBaseDir().refresh(true, true);
    }

    public static void openSelection(UIContext context)
    {
        Project project = projectFromContext(context);

        FileResource resource = ForgeProxies.proxyTo(FileResource.class, context.getSelection());

        if (resource != null)
        {
            File file = new File(resource.getFullyQualifiedName());
            openFile(project, file);
        }
    }

    public static void openFile(Project project, File file)
    {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }

    public static Project projectFromContext(UIContext context)
    {
        context = Proxies.unwrap(context);
        Assert.isTrue(context instanceof UIContextImpl, "UIContext must be an instance of UIContextImpl");

        return ((UIContextImpl) context).getProject();
    }

    public static String chooseFile(UIContext context, FileChooserDescriptor descriptor, String initialValue)
    {
        return chooseFile(projectFromContext(context), descriptor, initialValue);
    }

    public static String chooseFile(Project project, FileChooserDescriptor descriptor, String initialValue)
    {
        VirtualFile initialFile = LocalFileSystem.getInstance().findFileByIoFile(new File(initialValue));
        VirtualFile choosenFile = FileChooser.chooseFile(descriptor, project, initialFile);
        return choosenFile != null ? choosenFile.getCanonicalPath() : (initialValue.isEmpty() ? null : initialValue);
    }

    public static String chooseClass(UIContext context, String initialValue)
    {
        return chooseClass(projectFromContext(context), initialValue);
    }

    public static String chooseClass(Project project, String initialValue)
    {
        PsiManager psiManager = PsiManager.getInstance(project);

        TreeClassChooser chooser = new TreeJavaClassChooserDialog(
                "Select a Java class",
                project,
                GlobalSearchScope.allScope(project),
                null,
                ClassUtil.findPsiClass(psiManager, initialValue));
        chooser.showDialog();

        PsiClass psiClass = chooser.getSelected();

        return psiClass != null ? psiClass.getQualifiedName() : (initialValue.isEmpty() ? null : initialValue);
    }

    public static String choosePackage(UIContext context, String initialValue)
    {
        return choosePackage(projectFromContext(context), initialValue);
    }

    public static String choosePackage(Project project, String initialValue)
    {
        PackageChooserDialog dialog = new PackageChooserDialog("Select a Java package", project);
        dialog.selectPackage(initialValue);
        dialog.show();

        PsiPackage psiPackage = dialog.getSelectedPackage();

        return psiPackage != null ? psiPackage.getQualifiedName() : (initialValue.isEmpty() ? null : initialValue);
    }
}
