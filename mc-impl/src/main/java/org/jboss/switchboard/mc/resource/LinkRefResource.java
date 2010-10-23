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
package org.jboss.switchboard.mc.resource;

import javax.naming.Context;
import javax.naming.LinkRef;
import javax.naming.NamingException;

import org.jboss.switchboard.mc.dependency.JNDIDependency;
import org.jboss.switchboard.spi.Resource;

/**
 * LinkRefResource
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class LinkRefResource implements Resource
{

   private LinkRef linkRef;
   
   private Context ctx;
   
   private JNDIDependency dependency;
   
   public LinkRefResource(Context ctx, LinkRef linkRef)
   {
      this.linkRef = linkRef;
      this.ctx = ctx;
      // initialize the dependency 
      this.createJNDIDependency();
      
   }
   
   @Override
   public Object getDependency()
   {
      return this.dependency;
   }

   @Override
   public Object getTarget()
   {
      return this.linkRef;
   }

   private void createJNDIDependency()
   {
      try
      {
         this.dependency = new JNDIDependency(ctx, this.linkRef.getLinkName());
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }
   
}
