/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.components;

import org.jboss.forge.ui.input.InputComponent;

/**
 * A factory for {@link ControlBuilder} instances.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public enum ComponentBuilderRegistry
{
   INSTANCE;

   private ComponentBuilder[] componentBuilders = {
            // new CheckboxControlBuilder(),
            // new ComboEnumControlBuilder(),
            // new ComboControlBuilder(),
            // new RadioControlBuilder(),
            // new FileChooserControlBuilder(),
            // new CheckboxTableControlBuilder(),
            new TextBoxComponentBuilder()
            // new PasswordTextBoxControlBuilder(),
            // new FallbackTextBoxControlBuilder()
   };

   public ComponentBuilder getBuilderFor(InputComponent<?, ?> input)
   {
      for (ComponentBuilder builder : componentBuilders)
      {
         if (builder.handles(input))
         {
            return builder;
         }
      }
      throw new IllegalArgumentException("No UI component found for input type of " + input.getValueType());
   }
}