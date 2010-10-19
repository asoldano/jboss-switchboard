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
import java.lang.reflect.Method;
import java.util.Collection;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.switchboard.javaee.environment.InjectionTarget;
import org.jboss.switchboard.javaee.environment.SimpleEnvironmentEntryType;
import org.jboss.switchboard.mc.resource.IndependentResource;
import org.jboss.switchboard.spi.Resource;
import org.jboss.switchboard.spi.ResourceProvider;

/**
 * EnvEntryResourceProvider
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 * @param <C>
 */
public class EnvEntryResourceProvider implements ResourceProvider<DeploymentUnit, SimpleEnvironmentEntryType>
{

   private static Logger logger = Logger.getLogger(EnvEntryResourceProvider.class);

   @Override
   public Resource provide(DeploymentUnit deploymentUnit, SimpleEnvironmentEntryType envEntry)
   {
      ClassLoader cl = deploymentUnit.getClassLoader();
      Object targetValue = this.getEnvEntryValue(cl, envEntry);
      Resource resource = new IndependentResource(targetValue);
      return resource;
   }
   
   

   private Object getEnvEntryValue(ClassLoader cl, SimpleEnvironmentEntryType envEntry)
   {
      String envEntryType = this.getEnvEntryType(cl, envEntry);
      String value = envEntry.getValue();

      if (envEntryType == null)
      {
         return value;
      }

      if (envEntryType.equals(String.class.getName()))
      {
         return value;
      }
      else if (envEntryType.equals(Integer.class.getName()) || envEntryType.equals(int.class.getName()))
      {
         return new Integer(value);
      }
      else if (envEntryType.equals(Long.class.getName()) || envEntryType.equals(long.class.getName()))
      {
         return new Long(value);
      }
      else if (envEntryType.equals(Double.class.getName()) || envEntryType.equals(double.class.getName()))
      {
         return new Double(value);
      }
      else if (envEntryType.equals(Float.class.getName()) || envEntryType.equals(float.class.getName()))
      {
         return new Float(value);
      }
      else if (envEntryType.equals(Byte.class.getName()) || envEntryType.equals(byte.class.getName()))
      {
         return new Byte(value);
      }
      else if (envEntryType.equals(Character.class.getName()) || envEntryType.equals(char.class.getName()))
      {
         if (value == null || value.length() == 0)
         {
            return new Character((char) 0);
         }
         else
         {
            if (value.length() > 1)
            {
               logger
                     .warn("Warning character env-entry is too long: binding=" + envEntry.getName() + " value=" + value);
            }
            return new Character(value.charAt(0));
         }
      }
      else if (envEntryType.equals(Short.class.getName()) || envEntryType.equals(short.class.getName()))
      {
         return new Short(value);
      }
      else if (envEntryType.equals(Boolean.class.getName()) || envEntryType.equals(boolean.class.getName()))
      {
         return new Boolean(value);
      }
      else
      {
         return value;
      }
   }

   private String getEnvEntryType(ClassLoader cl, SimpleEnvironmentEntryType envEntry)
   {
      // first check whether the type is explicitly specified
      String explicitType = envEntry.getType();
      if (explicitType != null && !explicitType.isEmpty())
      {
         return explicitType;
      }
      Collection<InjectionTarget> injectionTargets = envEntry.getInjectionTargets();
      if (injectionTargets == null || injectionTargets.isEmpty())
      {
         return null;
      }
      InjectionTarget injectionTarget = injectionTargets.iterator().next();
      AccessibleObject accessibleObject = InjectionTargetUtil.findInjectionTarget(cl, injectionTarget);
      if (accessibleObject instanceof Field)
      {
         return ((Field) accessibleObject).getType().getName();
      }
      else if (accessibleObject instanceof Method)
      {
         return ((Method) accessibleObject).getParameterTypes()[0].getName();
      }
      return null;
   }
}
