/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.wizard;

import com.intellij.openapi.project.Project;
import com.intellij.ui.wizard.WizardDialog;
import org.jboss.forge.addon.ui.controller.CommandController;

import java.awt.*;

/**
 * Forge wizard (and single command) dialog.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Adam Wyłuda
 */
public class ForgeWizardDialog extends WizardDialog<ForgeWizardModel>
{
    public ForgeWizardDialog(CommandController originalController)
    {
        super((Project) null, false, new ForgeWizardModel(originalController));

        myModel.setDialog(this);
    }

    @Override
    public String getTitle()
    {
        return super.getTitle();
    }

    @Override
    protected Dimension getWindowPreferredSize()
    {
        return new Dimension(500, 500);
    }

    public void setErrorMessage(String text)
    {
        super.setErrorText(text);
    }
}