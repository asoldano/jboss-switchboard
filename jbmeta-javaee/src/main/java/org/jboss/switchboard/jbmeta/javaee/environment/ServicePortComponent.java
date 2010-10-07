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

import org.jboss.metadata.javaee.spec.PortComponentRef;
import org.jboss.metadata.javaee.spec.RespectBinding;
import org.jboss.switchboard.javaee.environment.Address;
import org.jboss.switchboard.javaee.environment.PortComponent;

/**
 * ServicePortComponent
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ServicePortComponent implements PortComponent
{

   private PortComponentRef delegate;
   
   private ServiceAddress address;
   
   public ServicePortComponent(PortComponentRef delegate)
   {
      this.delegate = delegate;
      if (delegate.getAddressing() != null)
      {
         this.address = new ServiceAddress(delegate.getAddressing());
      }
   }
   
   @Override
   public Address getAddress()
   {
      return this.address;
   }

   @Override
   public String getEndpointInterface()
   {
      return this.delegate.getServiceEndpointInterface();
   }

   @Override
   public String getLink()
   {
      return this.delegate.getPortComponentLink();
   }

   @Override
   public Integer getMtomThreshold()
   {
      return this.delegate.getMtomThreshold();
   }

   @Override
   public boolean isBindingRespected()
   {
      RespectBinding respectBinding = this.delegate.getRespectBinding();
      return respectBinding == null ? false : respectBinding.isEnabled();
   }

   @Override
   public boolean isMtomEnabled()
   {
      return this.delegate.isEnableMtom();
   }

}
