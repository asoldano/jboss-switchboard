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
      Class<?> envEntryType = this.getEnvEntryType(cl, envEntry);

      if (envEntryType == null)
      {
         return value;
      }

      // Now, convert the string value to it's appropriate type and return
      
      if (envEntryType.equals(String.class))
      {
         return value;
      }
      else if (envEntryType.equals(Integer.class) || envEntryType.equals(int.class))
      {
         return new Integer(value);
      }
      else if (envEntryType.equals(Long.class) || envEntryType.equals(long.class))
      {
         return new Long(value);
      }
      else if (envEntryType.equals(Double.class) || envEntryType.equals(double.class))
      {
         return new Double(value);
      }
      else if (envEntryType.equals(Float.class) || envEntryType.equals(float.class))
      {
         return new Float(value);
      }
      else if (envEntryType.equals(Byte.class) || envEntryType.equals(byte.class))
      {
         return new Byte(value);
      }
      else if (envEntryType.equals(Character.class) || envEntryType.equals(char.class))
      {
         if (value == null || value.length() == 0)
         {
            return new Character((char) 0);
         }
         else
         {
            if (value.length() > 1)
            {
               logger.warn("Warning character env-entry is too long: binding=" + envEntry.getName() + " value=" + value);
            }
            return new Character(value.charAt(0));
         }
      }
      else if (envEntryType.equals(Short.class) || envEntryType.equals(short.class))
      {
         return new Short(value);
      }
      else if (envEntryType.equals(Boolean.class) || envEntryType.equals(boolean.class))
      {
         return new Boolean(value);
      }
      // The Java EE spec doesn't allow java.lang.Class or enum types to be env-entry.
      // But we (JBoss) will allow these 2 additional types for env-entry.
      else if (envEntryType.equals(Class.class))
      {
         try
         {
            return Class.forName(value, true, cl);
         }
         catch (ClassNotFoundException cnfe)
         {
            throw new RuntimeException("Could not load class: " + value + " for env-entry: " + envEntry.getName(), cnfe);
         }
      }
      else if (envEntryType.isEnum())
      {
         return Enum.valueOf((Class)envEntryType, value);
      }
      else
      {
         // TODO: Throw exception instead?
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
   private Class<?> getEnvEntryType(ClassLoader cl, SimpleEnvironmentEntryType envEntry)
   {
      // first check whether the type is explicitly specified
      String explicitType = envEntry.getType();
      if (explicitType != null && !explicitType.trim().isEmpty())
      {
         try
         {
            return this.loadClass(explicitType, cl);
         }
         catch (ClassNotFoundException cnfe)
         {
            throw new RuntimeException("Could not load class: " + explicitType + " for env-entry: " + envEntry.getName(), cnfe);
         }
      }
      Collection<InjectionTarget> injectionTargets = envEntry.getInjectionTargets();
      if (injectionTargets == null || injectionTargets.isEmpty())
      {
         return null;
      }
      InjectionTarget injectionTarget = injectionTargets.iterator().next();
      Class<?> type = InjectionTargetUtil.getInjectionTargetPropertyType(cl, injectionTarget);
      return type;
   }
   
   
   private Class<?> loadClass(String className, ClassLoader cl) throws ClassNotFoundException
   {
      if (className.equals(void.class.getName()))
      {
         return void.class;
      }
      if (className.equals(byte.class.getName()))
      {
         return byte.class;
      }
      if (className.equals(short.class.getName()))
      {
         return short.class;
      }
      if (className.equals(int.class.getName()))
      {
         return int.class;
      }
      if (className.equals(long.class.getName()))
      {
         return long.class;
      }
      if (className.equals(char.class.getName()))
      {
         return char.class;
      }
      if (className.equals(boolean.class.getName()))
      {
         return boolean.class;
      }
      if (className.equals(float.class.getName()))
      {
         return float.class;
      }
      if (className.equals(double.class.getName()))
      {
         return double.class;
      }
      // Now that we know its not a primitive, lets just allow
      // the passed classloader to handle the request.
      // Note that we are intentionally using Class.forName(name,boolean,cl)
      // to handle issues with loading array types in Java 6 http://bugs.sun.com/view_bug.do?bug_id=6434149
      return Class.forName(className, false, cl);
   }
   
}
