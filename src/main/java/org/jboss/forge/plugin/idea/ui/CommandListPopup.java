/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.popup.*;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.util.Function;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.plugin.idea.service.ServiceHelper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all UI commands.
 *
 * @author Adam Wyłuda
 */
public class CommandListPopup
{
    // TODO Design and implement CommandListPopup

    private static volatile boolean active;

    private final UIContext uiContext;

    public CommandListPopup(UIContext uiContext)
    {
        this.uiContext = uiContext;
    }

    public static boolean isActive()
    {
        return active;
    }

    public void show()
    {
        if (active)
            return;
        active = true;

        final JBList list = new JBList();
        DefaultListModel model = new DefaultListModel();

        final List<UICommand> allCandidates = getAllCandidates();
        model.setSize(allCandidates.size());

        list.setCellRenderer(new ListCellRendererWrapper<UICommand>()
        {
            @Override
            public void customize(JList list, UICommand data, int index,
                                  boolean selected, boolean hasFocus)
            {
                if (data != null)
                {
                    setIcon(AllIcons.Nodes.Plugin);

                    UICommandMetadata metadata = data.getMetadata(uiContext);

                    setText(metadata.getName());
                    setToolTipText(metadata.getDescription());
                }
            }
        });

        for (int i = 0; i < allCandidates.size(); i++)
        {
            model.set(i, allCandidates.get(i));
        }

        list.setModel(model);

        final PopupChooserBuilder listPopupBuilder = JBPopupFactory.getInstance().createListPopupBuilder(list);
        listPopupBuilder.setTitle("Run a Forge command");
        listPopupBuilder.setResizable(true);
        listPopupBuilder.addListener(new JBPopupAdapter()
        {
            @Override
            public void onClosed(LightweightWindowEvent event)
            {
                CommandListPopup.this.active = false;
            }
        });
        listPopupBuilder.setItemChoosenCallback(new Runnable()
        {
            @Override
            public void run()
            {
                int selectedIndex = list.getSelectedIndex();
                UICommand selectedCommand = allCandidates.get(selectedIndex);
                openWizard(selectedCommand);
            }
        });
        listPopupBuilder.setFilteringEnabled(new Function<Object, String>()
        {
            @Override
            public String fun(Object object)
            {
                UICommand command = (UICommand) object;
                UICommandMetadata metadata = command.getMetadata(uiContext);

                return metadata.getCategory().toString() + " " + metadata.getName();
            }
        });

        JBPopup popup = listPopupBuilder.createPopup();
        popup.showInFocusCenter();
    }

    private List<UICommand> getAllCandidates()
    {
        List<UICommand> commands = new ArrayList<UICommand>();
        CommandFactory commandFactory = ServiceHelper.getForgeService().getCommandFactory();

        for (UICommand command : commandFactory.getCommands())
        {
            if (isCandidate(command))
            {
                commands.add(command);
            }
        }

        return commands;
    }

    private boolean isCandidate(UICommand command)
    {
        return !(command instanceof UIWizardStep) && command.isEnabled(uiContext);
    }

    private void openWizard(UICommand command)
    {
        // TODO Use CommandController to obtain UICommand metadata
//        ForgeWizardModel model = new ForgeWizardModel(command.getMetadata().getName(), command, files);
//        ForgeWizardDialog dialog = new ForgeWizardDialog(model);
//        dialog.show();
    }
}
