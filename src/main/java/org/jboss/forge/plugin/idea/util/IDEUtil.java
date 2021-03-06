/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeJavaClassChooserDialog;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.FileEditor;
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
import org.jboss.forge.addon.ui.context.UIRegion;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.plugin.idea.context.UIContextImpl;

/**
 * @author Adam Wyłuda
 */
public class IDEUtil
{
   private IDEUtil()
   {
   }

   public static void refreshProject(UIContext context)
   {
      Project project = projectFromContext(context);
      refreshProject(project);
      refreshSelection(project, context);
   }

   public static void refreshProject(Project project)
   {
      if (!project.isDisposed())
      {
         project.getBaseDir().refresh(true, true);
      }
   }

   private static void refreshSelection(Project project, UIContext context)
   {
      if (!project.isDisposed())
      {
         java.util.List<File> files = new ArrayList<>();
         for (Object selection : context.getSelection())
         {
            if (selection instanceof FileResource)
            {
               FileResource<?> resource = (FileResource<?>) selection;
               File file = resource.getUnderlyingResourceObject();
               files.add(file);
            }
         }
         LocalFileSystem.getInstance().refreshIoFiles(files);
      }
   }

   public static void openSelection(UIContext context)
   {
      Project project = projectFromContext(context);

      if (!project.isDisposed())
      {
         UISelection<Object> selection = context.getSelection();
         Optional<UIRegion<Object>> region = selection.getRegion();
         // selection.getRegion() returns only the first region.
         boolean first = true;
         for (Object singleSelection : selection)
         {
            openSingleSelection(project, singleSelection, first ? region : Optional.empty());
            first = false;
         }
      }
   }

   private static void openSingleSelection(Project project, Object selection, Optional<UIRegion<Object>> region)
   {
      if (selection instanceof FileResource)
      {
         FileResource<?> resource = (FileResource<?>) selection;
         File file = resource.getUnderlyingResourceObject();
         openFile(project, file);
         region.ifPresent(r ->
         {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor == null)
            {
               return;
            }
            SelectionModel selectionModel = editor.getSelectionModel();
            ScrollingModel scrollingModel = editor.getScrollingModel();

            LogicalPosition from = new LogicalPosition(r.getStartLine() - 1, r.getStartPosition());
            LogicalPosition to = new LogicalPosition(r.getEndLine() - 1, r.getEndPosition(), true);

            selectionModel.setBlockSelection(from, to);
            scrollingModel.scrollTo(from, ScrollType.CENTER);
         });
      }
   }

   private static FileEditor[] openFile(Project project, File file)
   {
      VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
      if (virtualFile != null)
      {
         return FileEditorManager.getInstance(project).openFile(virtualFile, true);
      }
      return null;
   }

   public static void openProject(String path)
   {
      try
      {
         ProjectUtil.openOrImport(path, null, false);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public static Project projectFromContext(UIContext context)
   {
      context = Proxies.unwrap(context);
      Assert.isTrue(context instanceof UIContextImpl, "UIContext must be an instance of UIContextImpl");

      return ((UIContextImpl) context).getProject();
   }

   public static UIProgressMonitor progressMonitorFromContext(UIContext context)
   {
      context = Proxies.unwrap(context);
      Assert.isTrue(context instanceof UIContextImpl, "UIContext must be an instance of UIContextImpl");

      return ((UIContextImpl) context).getProgressMonitor();
   }

   public static String chooseFile(UIContext context, FileChooserDescriptor descriptor, String initialValue)
   {
      return chooseFile(projectFromContext(context), descriptor, initialValue);
   }

   public static String chooseFile(Project project, FileChooserDescriptor descriptor, String initialValue)
   {
      VirtualFile initialFile = LocalFileSystem.getInstance().findFileByIoFile(new File(initialValue));
      VirtualFile chosenFile = FileChooser.chooseFile(descriptor, project, initialFile);
      return chosenFile != null ? chosenFile.getCanonicalPath() : (initialValue.isEmpty() ? null : initialValue);
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
