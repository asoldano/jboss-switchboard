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
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.jboss.dependency.plugins.AbstractDependencyItem;
import org.jboss.dependency.spi.Controller;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.reloaded.naming.CurrentComponent;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.switchboard.mc.SwitchBoardImpl;

/**
 * JNDIDependencyItem
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class JNDIDependencyItem extends AbstractDependencyItem
{

   private SwitchBoardImpl switchBoard;

   private String jndiName;
   
   private Context globalContext;

   public JNDIDependencyItem(SwitchBoardImpl switchBoard, String jndiName)
   {
      super(jndiName, jndiName, ControllerState.CREATE, ControllerState.INSTALLED);
      this.switchBoard = switchBoard;
      this.jndiName = jndiName;
      
      
   }

   @Override
   public boolean resolve(Controller controller)
   {
      JavaEEComponent javaeeComp = this.switchBoard.getJavaEEComponent();
      if (javaeeComp == null)
      {
         // mark as unresolved
         this.setResolved(false);
         return this.isResolved();
      }
      CurrentComponent.push(javaeeComp);
      try
      {
         this.getGlobalContext().lookup(jndiName);
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
               + " for JavaEEComponent " + javaeeComp.getName(), ne);
      }
      finally
      {
         CurrentComponent.pop();
      }

      // return the resolution status
      return this.isResolved();
   }

   private Context getGlobalContext() throws NamingException
   {
      if (this.globalContext == null)
      {
         this.globalContext = new InitialContext();
      }
      return this.globalContext;
   }
}
