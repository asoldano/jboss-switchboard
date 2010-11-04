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

import java.util.ArrayList;
import java.util.List;

import org.jboss.metadata.javaee.jboss.JBossPortComponentRef;
import org.jboss.metadata.javaee.jboss.JBossServiceReferenceMetaData;
import org.jboss.switchboard.javaee.jboss.environment.JBossPortComponentRefType;
import org.jboss.switchboard.javaee.jboss.environment.JBossServiceRefType;

/**
 * JBossServiceReference
 *
 * @author alessio.soldano@jboss.com
 * @since 04-Nov-2010
 * @version $Revision: $
 */
public class JBossServiceReference extends ServiceReference implements JBossServiceRefType
{

   private JBossServiceReferenceMetaData delegate;

   public JBossServiceReference(JBossServiceReferenceMetaData delegate)
   {
      super(delegate);
      this.delegate = delegate;
   }

   @Override
   public String getServiceClass()
   {
      return delegate.getServiceClass();
   }

   @Override
   public String getConfigName()
   {
      return delegate.getConfigName();
   }

   @Override
   public String getConfigFile()
   {
      return delegate.getConfigFile();
   }

   @Override
   public String getHandlerChain()
   {
      return delegate.getHandlerChain();
   }

   @Override
   public String getWsdlOverride()
   {
      return delegate.getWsdlOverride();
   }

   @Override
   public List<JBossPortComponentRefType> getJBossPortComponentRefs()
   {
      List<JBossPortComponentRef> jbpcrList = this.delegate.getJBossPortComponentRef();
      if (jbpcrList == null)
      {
         return null;
      }
      List<JBossPortComponentRefType> portComponentRefs = new ArrayList<JBossPortComponentRefType>(jbpcrList.size());
      for (JBossPortComponentRef jbpcr : jbpcrList)
      {
         portComponentRefs.add(new JBossPortComponentReference(jbpcr));
      }
      return portComponentRefs;
   }

}
