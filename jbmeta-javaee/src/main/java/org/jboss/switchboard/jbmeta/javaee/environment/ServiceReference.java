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
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.metadata.javaee.spec.PortComponentRef;
import org.jboss.metadata.javaee.spec.ServiceReferenceHandlerMetaData;
import org.jboss.metadata.javaee.spec.ServiceReferenceHandlersMetaData;
import org.jboss.metadata.javaee.spec.ServiceReferenceMetaData;
import org.jboss.switchboard.javaee.environment.Handler;
import org.jboss.switchboard.javaee.environment.PortComponent;
import org.jboss.switchboard.javaee.environment.ServiceRefType;

/**
 * ServiceReference
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ServiceReference extends JavaEEResource implements ServiceRefType
{

   private ServiceReferenceMetaData delegate;

   private List<Handler> handlers;
   
   private Collection<PortComponent> portComponents;
   
   public ServiceReference(ServiceReferenceMetaData delegate)
   {
      super(delegate.getLookupName(), delegate.getMappedName(), InjectionTargetConverter.convert(delegate.getInjectionTargets()));
      this.delegate = delegate;
      this.initHandlers();
      this.initPortComponents();
   }
   
   @Override
   public List<Handler> getHandlerChain()
   {
      return this.handlers;
   }

   @Override
   public Collection<Handler> getHandlers()
   {
      return this.handlers;
   }

   @Override
   public String getMappingFile()
   {
      return this.delegate.getJaxrpcMappingFile();
   }

   @Override
   public Collection<PortComponent> getPortComponents()
   {
      return this.portComponents;
   }

   @Override
   public QName getQName()
   {
      return this.delegate.getServiceQname();
   }

   @Override
   public String getServiceInterface()
   {
      return this.delegate.getServiceInterface();
   }

   @Override
   public String getType()
   {
      return this.delegate.getServiceRefType();
   }

   @Override
   public String getWsdlFile()
   {
      return this.delegate.getWsdlFile();
   }

   @Override
   public String getName()
   {
      return this.delegate.getServiceRefName();
   }
   
   private void initHandlers()
   {
      if (this.delegate.getHandlers() == null)
      {
         return;
      }
      ServiceReferenceHandlersMetaData serviceHandlers = this.delegate.getHandlers();
      this.handlers = new ArrayList<Handler>(serviceHandlers.size());
      for (ServiceReferenceHandlerMetaData serviceHandler : serviceHandlers)
      {
         this.handlers.add(new ServiceHandler(serviceHandler));
      }
   }
   
   private void initPortComponents()
   {
      if (this.delegate.getPortComponentRef() == null)
      {
         return;
      }
      List<? extends PortComponentRef> portComponentRefs = this.delegate.getPortComponentRef();
      this.portComponents = new ArrayList<PortComponent>(portComponentRefs.size());
      for (PortComponentRef portCompRef : portComponentRefs)
      {
         this.portComponents.add(new ServicePortComponent(portCompRef));
      }
   }

}
