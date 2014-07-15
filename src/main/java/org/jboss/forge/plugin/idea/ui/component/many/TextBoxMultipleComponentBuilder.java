/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component.many;

import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.plugin.idea.ui.component.ComponentBuilder;
import org.jboss.forge.plugin.idea.ui.component.ForgeComponent;
import org.jboss.forge.plugin.idea.util.ForgeProxies;
import org.jboss.forge.plugin.idea.util.IDEUtil;

/**
 * @author Adam Wyłuda
 */
public class TextBoxMultipleComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(final InputComponent<?, Object> input)
    {
        return new ListComponent(ForgeProxies.proxyTo(UIInputMany.class, input))
        {
            @Override
            protected String editSelectedItem(String item)
            {
                return showEditDialog("Edit item", item);
            }

            @Override
            protected String findItemToAdd()
            {
                return showEditDialog("Add item", "");
            }

            private String showEditDialog(String title, final String initialValue) {
                return Messages.showInputDialog(IDEUtil.projectFromContext(context),
                        "", title, Messages.getQuestionIcon(), initialValue,
                        new InputValidator()
                {
                    @Override
                    public boolean checkInput(String inputString)
                    {
                        return true;
                    }

                    @Override
                    public boolean canClose(String inputString)
                    {
                        return true;
                    }
                });
            }
        };
    }

    @Override
    protected Class<String> getProducedType()
    {
        return String.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.TEXTBOX;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInputMany.class};
    }
}