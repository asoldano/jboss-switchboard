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
package org.jboss.switchboard.jbmeta.javaee.environment;

import java.util.Collection;

import org.jboss.switchboard.javaee.environment.InjectionTarget;
import org.jboss.switchboard.javaee.environment.JavaEEResourceType;

/**
 * AbstractJavaEEResource
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class JavaEEResource implements JavaEEResourceType
{

   private String lookupName;
   
   private String mappedName;
   
   private Collection<InjectionTarget> injectionTargets;
   
   public JavaEEResource()
   {
      
   }
   
   public JavaEEResource(String lookupName, String mappedName, Collection<InjectionTarget> injectionTargets)
   {
      this.lookupName = lookupName;
      this.mappedName = mappedName;
      this.injectionTargets = injectionTargets;
   }
   
   @Override
   public Collection<InjectionTarget> getInjectionTargets()
   {
      return this.injectionTargets;
   }

   @Override
   public String getLookupName()
   {
      return this.lookupName;
   }

   @Override
   public String getMappedName()
   {
      return this.mappedName;
   }

}
