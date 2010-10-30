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

import org.jboss.metadata.javaee.spec.ResourceAuthorityType;
import org.jboss.metadata.javaee.spec.ResourceReferenceMetaData;
import org.jboss.metadata.javaee.spec.ResourceSharingScopeType;
import org.jboss.switchboard.javaee.environment.ResourceAuthType;
import org.jboss.switchboard.javaee.environment.ResourceSharingScope;
import org.jboss.switchboard.javaee.jboss.environment.JBossResourceRefType;

/**
 * ResourceReference
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class JBossResourceReference extends JavaEEResource implements JBossResourceRefType
{

   private ResourceReferenceMetaData delegate;

   public JBossResourceReference(ResourceReferenceMetaData delegate)
   {
      super(delegate.getLookupName(), delegate.getMappedName(), InjectionTargetConverter.convert(delegate
            .getInjectionTargets()));
      this.delegate = delegate;
   }

   @Override
   public ResourceAuthType getResourceAuthType()
   {
      ResourceAuthorityType authType = this.delegate.getResAuth();
      if (authType == null)
      {
         return null;
      }
      return ResourceAuthType.valueOf(authType.name());
   }

   @Override
   public ResourceSharingScope getResourceSharingScope()
   {
      ResourceSharingScopeType sharingScope = this.delegate.getResSharingScope();
      if (sharingScope == null)
      {
         return null;
      }
      return ResourceSharingScope.valueOf(sharingScope.name());
   }

   @Override
   public String getResourceType()
   {
      return this.delegate.getType();
   }

   @Override
   public String getName()
   {
      return "env/" + this.delegate.getResourceRefName();
   }

   @Override
   public String getJNDIName()
   {
      return this.delegate.getJndiName();
   }

   @Override
   public String getResourceURL()
   {
      return this.delegate.getResUrl();
   }

   @Override
   public boolean isIgnoreDependency()
   {
      return this.delegate.isDependencyIgnored();
   }

}
