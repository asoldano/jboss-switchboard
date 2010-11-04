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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.metadata.javaee.jboss.CallPropertyMetaData;
import org.jboss.metadata.javaee.jboss.JBossPortComponentRef;
import org.jboss.metadata.javaee.jboss.StubPropertyMetaData;
import org.jboss.switchboard.javaee.jboss.environment.JBossPortComponentRefType;

/**
 * JBossServiceReference
 *
 * @author alessio.soldano@jboss.com
 * @since 04-Nov-2010
 * @version $Revision: $
 */
public class JBossPortComponentReference extends ServicePortComponent implements JBossPortComponentRefType
{
   private JBossPortComponentRef delegate;
   
   public JBossPortComponentReference(JBossPortComponentRef delegate)
   {
      super(delegate);
      this.delegate = delegate;
   }
   
   @Override
   public QName getPortQName()
   {
      return delegate.getPortQname();
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
   public Map<String, String> getStubProperties()
   {
      List<StubPropertyMetaData> stubProperties = delegate.getStubProperties();
      if (stubProperties == null)
      {
         return null;
      }
      Map<String, String> result = new HashMap<String, String>();
      for (StubPropertyMetaData prop : stubProperties)
      {
         result.put(prop.getPropName(), prop.getPropValue());
      }
      return result;
   }

   @Override
   public Map<String, String> getCallProperties()
   {
      List<CallPropertyMetaData> callroperties = delegate.getCallProperties();
      if (callroperties == null)
      {
         return null;
      }
      Map<String, String> result = new HashMap<String, String>();
      for (CallPropertyMetaData prop : callroperties)
      {
         result.put(prop.getPropName(), prop.getPropValue());
      }
      return result;
   }

}
