/*
* JBoss, Home of Professional Open Source
* Copyright 2010, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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

import javax.xml.namespace.QName;

import org.jboss.metadata.javaee.spec.ServiceReferenceHandlerChainMetaData;
import org.jboss.metadata.javaee.spec.ServiceReferenceHandlerMetaData;
import org.jboss.switchboard.javaee.environment.Handler;
import org.jboss.switchboard.javaee.environment.HandlerChainType;

/**
 * 
 * @author alessio.soldano@jboss.com
 * @since 04-Nov-2010
 *
 */
public class HandlerChain implements HandlerChainType
{
   private ServiceReferenceHandlerChainMetaData delegate;
   private List<Handler> handlers;
   
   public HandlerChain(ServiceReferenceHandlerChainMetaData delegate)
   {
      this.delegate = delegate;
      List<ServiceReferenceHandlerMetaData> srhList = delegate.getHandler();
      handlers = new ArrayList<Handler>(srhList.size());
      for (ServiceReferenceHandlerMetaData srh : srhList)
      {
         handlers.add(new ServiceHandler(srh));
      }
   }
   
   @Override
   public QName getServiceNamePattern()
   {
      return this.delegate.getServiceNamePattern();
   }

   @Override
   public QName getPortNamePattern()
   {
      return this.delegate.getPortNamePattern();
   }

   @Override
   public String getProtocolBindings()
   {
      return this.delegate.getProtocolBindings();
   }

   @Override
   public List<Handler> getHandlers()
   {
      return handlers;
   }

}
