/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.switchboard.mc.resource.provider;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import org.jboss.switchboard.javaee.environment.InjectionTarget;

/**
 * InjectionTargetUtil
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class InjectionTargetUtil
{

   public static AccessibleObject findInjectionTarget(ClassLoader loader, InjectionTarget target)
   {
      Class<?> clazz = null;
      try
      {
         clazz = loader.loadClass(target.getTargetClass());
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException("<injection-target> class: " + target.getTargetClass() + " was not found in deployment");
      }

      for (Field field : clazz.getDeclaredFields())
      {
         if (target.getTargetName().equals(field.getName())) return field;
      }

      for (java.lang.reflect.Method method : clazz.getDeclaredMethods())
      {
         if (method.getName().equals(target.getTargetName())) return method;
      }

      throw new RuntimeException("<injection-target> could not be found: " + target.getTargetClass() + "." + target.getTargetName());

   }
}
