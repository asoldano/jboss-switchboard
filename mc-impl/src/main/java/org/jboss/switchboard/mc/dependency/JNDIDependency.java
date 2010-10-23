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
package org.jboss.switchboard.mc.dependency;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.jboss.dependency.plugins.AbstractDependencyItem;
import org.jboss.dependency.spi.Controller;

/**
 * JNDIDependency
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class JNDIDependency extends AbstractDependencyItem
{

   private Context ctx;
   
   private String jndiName;
   
   public JNDIDependency(Context ctx, String jndiName)
   {
      this.ctx = ctx;
      this.jndiName = jndiName;
   }
   
   @Override
   public boolean resolve(Controller controller)
   {
      try
      {
         Object obj = this.ctx.lookup(jndiName);
         // mark as resolved
         this.setResolved(true);
      }
      catch (NameNotFoundException nnfe)
      {
         // mark as unresolved
         this.setResolved(false);
      }
      catch (NamingException ne)
      {
         throw new RuntimeException("Exception during resolving jndi dependency for jndi name: " + this.jndiName
               + " in jndi context " + this.ctx, ne);
      }
      
      // return the resolution status
      return this.isResolved();
   }
   
}
