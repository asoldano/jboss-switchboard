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

import org.jboss.beans.metadata.plugins.AbstractDependencyMetaData;
import org.jboss.beans.metadata.spi.MetaDataVisitor;
import org.jboss.switchboard.impl.resource.JNDIDependency;
import org.jboss.switchboard.mc.SwitchBoardImpl;

/**
 * SwitchBoardDependency
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class SwitchBoardDependencyMetaData extends AbstractDependencyMetaData
{

   private boolean isJNDIDependency;
   
   private JNDIDependency jndiDependency;
   
   private SwitchBoardImpl switchBoard;
   
   public SwitchBoardDependencyMetaData(SwitchBoardImpl switchBoard, Object dependency)
   {
      super(dependency);
      this.switchBoard = switchBoard;
      if (dependency instanceof JNDIDependency)
      {
         this.isJNDIDependency = true;
         this.jndiDependency = (JNDIDependency) dependency;
      }
   }
   
   @Override
   public void initialVisit(MetaDataVisitor visitor)
   {
      if (!this.isJNDIDependency)
      {
         super.initialVisit(visitor);
         return;
      }
      visitor.addDependency(new JNDIDependencyItem(switchBoard, this.jndiDependency.getJNDIName()));
      visitor.initialVisit(this);
   }
}
