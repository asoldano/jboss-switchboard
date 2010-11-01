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

import java.util.Collection;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.switchboard.impl.resource.IndependentResource;
import org.jboss.switchboard.javaee.environment.InjectionTarget;
import org.jboss.switchboard.javaee.environment.SimpleEnvironmentEntryType;
import org.jboss.switchboard.javaee.util.InjectionTargetUtil;
import org.jboss.switchboard.mc.spi.MCBasedResourceProvider;
import org.jboss.switchboard.spi.Resource;
import org.jboss.switchboard.spi.ResourceProvider;

/**
 * A {@link ResourceProvider} for env-entry in a Java EE environment
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 * 
 */
public class EnvEntryResourceProvider implements MCBasedResourceProvider<SimpleEnvironmentEntryType>
{

   /**
    * Logger
    */
   private static Logger logger = Logger.getLogger(EnvEntryResourceProvider.class);

   /**
    * 
    * Processes the passed {@link SimpleEnvironmentEntryType} and creates a {@link Resource}
    * out of it 
    */
   @Override
   public Resource provide(DeploymentUnit deploymentUnit, SimpleEnvironmentEntryType envEntry)
   {
      ClassLoader cl = deploymentUnit.getClassLoader();
      // get the env-entry value
      Object targetValue = this.getEnvEntryValue(cl, envEntry);
      // create a resource for this target value
      Resource resource = new IndependentResource(targetValue);
      
      // return it!
      return resource;
   }
   
   
   @Override
   public Class<SimpleEnvironmentEntryType> getEnvironmentEntryType()
   {
      return SimpleEnvironmentEntryType.class;
   }
   /**
    * Creates and returns the env-entry-value represented by the {@link SimpleEnvironmentEntryType#getValue()}
    * 
    * @param cl The {@link ClassLoader} to use while processing the metadata
    * @param envEntry The Java EE env-entry
    * @return
    */
   private Object getEnvEntryValue(ClassLoader cl, SimpleEnvironmentEntryType envEntry)
   {
      // get the (string) value
      String value = envEntry.getValue();
      if (value == null)
      {
         return null;
      }
      // find out the type of the env-entry
      String envEntryType = this.getEnvEntryType(cl, envEntry);

      if (envEntryType == null)
      {
         return value;
      }

      // Now, convert the string value to it's appropriate type and return
      
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

   /**
    * Returns the env-entry-type for the passed {@link SimpleEnvironmentEntryType}.
    * <p>
    *   If the passed env-entry has the env-entry-type explicitly specified, then
    *   that value is returned. Else, this method checks for the presence of any
    *   injection targets for this env-entry. If there's a injection target, then
    *   the env-entry-type is deduced based on the field/method of the injection target. 
    * </p>
    * <p>
    *   This method returns null if the env-entry-type isn't explicitly specified and 
    *   if the env-entry-type could not be deduced from the injection targets of this
    *   env-entry.
    * </p>
    * 
    * @param cl The {@link ClassLoader} to be used during processing the metadata
    * @param envEntry The Java EE env-entry
    * @return
    */
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
      Class<?> type = InjectionTargetUtil.getInjectionTargetPropertyType(cl, injectionTarget);
      return type == null ? null : type.getName();
   }
}
