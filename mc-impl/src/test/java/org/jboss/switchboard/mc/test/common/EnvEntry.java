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
package org.jboss.switchboard.mc.test.common;

import java.util.Collection;

import org.jboss.switchboard.javaee.environment.InjectionTarget;
import org.jboss.switchboard.javaee.environment.SimpleEnvironmentEntryType;

/**
 * EnvEntry
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class EnvEntry implements SimpleEnvironmentEntryType
{

   private String type;
   
   private String value;
   
   private String encName;
   
   public EnvEntry(String encName, String type, String value)
   {
      this.type = type;
      this.value = value;
      this.encName = encName;
   }
   
   @Override
   public String getType()
   {
      return this.type;
   }

   @Override
   public String getValue()
   {
      return this.value;
   }

   @Override
   public Collection<InjectionTarget> getInjectionTargets()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getLookupName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getMappedName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getName()
   {
      return this.encName;
   }

}
