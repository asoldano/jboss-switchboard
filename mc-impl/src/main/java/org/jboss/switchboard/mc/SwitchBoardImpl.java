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
package org.jboss.switchboard.mc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.beans.metadata.spi.DependencyMetaData;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.reloaded.naming.spi.JavaEEModule;
import org.jboss.switchboard.impl.ENCOperator;
import org.jboss.switchboard.mc.dependency.SwitchBoardDependencyMetaData;
import org.jboss.switchboard.spi.Barrier;
import org.jboss.switchboard.spi.Resource;

/**
 * SwitchBoardImpl
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class SwitchBoardImpl implements Barrier
{

   private String id;
   
   private ENCOperator encOperator;
   
   private JavaEEComponent javaEEComponent;
   
   public SwitchBoardImpl(String barrierId, ENCOperator encOperator)
   {
      this.id = barrierId;
      this.encOperator = encOperator;
   }
   
   public void start() throws NamingException
   {
      this.encOperator.bind();
   }
   
   public void stop() throws NamingException
   {
      this.encOperator.unbind();
   }
   
   public JavaEEComponent getJavaEEComponent()
   {
      return this.javaEEComponent;
   }
   
   public void setContext(JavaEEComponent javaComp)
   {
      this.encOperator.setContext(javaComp.getContext());
      this.javaEEComponent = javaComp;
   }

   public void setContext(final JavaEEModule javaeeModule)
   {
      JavaEEComponent javaComp = new JavaEEComponent()
      {
         
         @Override
         public String getName()
         {
            return javaeeModule.getName();
         }
         
         @Override
         public JavaEEModule getModule()
         {
            return javaeeModule;
         }
         
         @Override
         public Context getContext()
         {
            return javaeeModule.getContext();
         }
      };
      this.setContext(javaComp);
   }

   
   @Override
   public String getId()
   {
      return this.id;
   }
   
   public void addENCBinding(Map<String, Resource> resources)
   {
      this.encOperator.addENCBinding(resources);
   }
   
   public Collection<DependencyMetaData> getDependencies()
   {
      Collection<DependencyMetaData> dependencies = new ArrayList<DependencyMetaData>();
      for (Resource encBinding : encOperator.getENCBindings().values())
      {
         Object dependency = encBinding.getDependency();
         if (dependency != null)
         {
            DependencyMetaData mcDependency = new SwitchBoardDependencyMetaData(this, dependency);
            dependencies.add(mcDependency);
         }
      }
      return dependencies;
   }
}
