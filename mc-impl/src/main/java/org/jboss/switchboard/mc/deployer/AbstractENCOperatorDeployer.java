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
package org.jboss.switchboard.mc.deployer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.DemandMetaData;
import org.jboss.beans.metadata.spi.DependencyMetaData;
import org.jboss.beans.metadata.spi.SupplyMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.deployer.helpers.AbstractRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.metadata.ejb.jboss.JBossMetaData;
import org.jboss.metadata.javaee.spec.Environment;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.reloaded.naming.deployers.javaee.JavaEEComponentInformer;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.reloaded.naming.spi.JavaEEModule;
import org.jboss.switchboard.impl.ENCOperator;
import org.jboss.switchboard.impl.JndiEnvironmentProcessor;
import org.jboss.switchboard.jbmeta.javaee.environment.JndiEnvironmentMetadata;
import org.jboss.switchboard.mc.SwitchBoardImpl;
import org.jboss.switchboard.spi.Barrier;
import org.jboss.switchboard.spi.JndiEnvironment;
import org.jboss.switchboard.spi.Resource;

/**
 * AbstractJavaEEComponentDeployer
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public abstract class AbstractENCOperatorDeployer extends AbstractRealDeployer
{
   /** Logger */
   private static Logger logger = Logger.getLogger(AbstractENCOperatorDeployer.class);

   protected JavaEEComponentInformer informer;

   protected JndiEnvironmentProcessor jndiEnvProcessor;

   public AbstractENCOperatorDeployer(JavaEEComponentInformer informer)
   {
      this.informer = informer;
      //setInputs(informer.getRequiredAttachments());
      setOutput(BeanMetaData.class);
      addOutput(Barrier.class);
   }

   @Inject
   public void setJNDIEnvironmentProcessor(JndiEnvironmentProcessor processor)
   {
      this.jndiEnvProcessor = processor;
   }

   protected String getApplicationName(DeploymentUnit deploymentUnit)
   {
      return informer.getApplicationName(deploymentUnit);
   }

   protected String getComponentName(DeploymentUnit deploymentUnit)
   {
      return informer.getComponentName(deploymentUnit);
   }

   protected String getModuleName(DeploymentUnit deploymentUnit)
   {
      return informer.getModuleName(deploymentUnit);
   }

   protected void process(DeploymentUnit unit, Collection<Environment> environments)
   {
      Map<String, Resource> resources = new HashMap<String, Resource>();
      for (Environment env : environments)
      {
         JndiEnvironment jndiEnv = this.convert(env);
         resources.putAll(this.jndiEnvProcessor.process(unit, jndiEnv));
      }
      if (resources.isEmpty())
      {
         return;
      }
      // A ENCOperator might already exist for the unit. For example,
      // for EJBs and servlets deployed in a single .war (component), there will be 
      // a ENCOperator which might have been created and added when processing JBossMetaData (for EJBs)
      // and now we might be processing JBossWebMetaData (for servlets), which both share the same
      // (JavaEE)component.  
      Barrier barrier = unit.getAttachment(Barrier.class);
      if (barrier == null)
      {
         // create a new one and attach it to unit
         ENCOperator encOperator = new ENCOperator(resources);
         SwitchBoardImpl switchBoard = new SwitchBoardImpl(this.getSwitchBoardMCBeanName(unit), encOperator);
         this.attachBarrier(unit, switchBoard);
         
         BeanMetaData switchBoardBMD = this.createSwitchBoardBMD(unit, switchBoard);
         this.attachSwitchBoardBMD(unit, switchBoardBMD);
      }
      else
      {
         // HACK! 
         // TODO: Think about a better way of doing this (maybe making available the methods in Barrier)
         if (barrier instanceof SwitchBoardImpl)
         {
            SwitchBoardImpl switchboard = (SwitchBoardImpl) barrier;
            switchboard.addENCBinding(resources);
            
            // recreate and attach SwitchBoard BMD, since the 
            // new Resource binding might have additional dependencies
            BeanMetaData switchBoardBMD = this.createSwitchBoardBMD(unit, switchboard);
            this.attachSwitchBoardBMD(unit, switchBoardBMD);

         }
      }
   }

   private JndiEnvironment convert(Environment environment)
   {
      return new JndiEnvironmentMetadata(environment);
   }

   protected BeanMetaData createSwitchBoardBMD(DeploymentUnit unit, SwitchBoardImpl switchBoard)
   {
      String mcBeanName = switchBoard.getId();
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder(mcBeanName, switchBoard.getClass().getName());
      builder.setConstructorValue(switchBoard);

      AbstractInjectionValueMetaData namingContextInjection = this.getNamingContextInjectionMetaData(unit);
      builder.addPropertyMetaData("context", namingContextInjection);

      for (DependencyMetaData dependency : switchBoard.getDependencies())
      {
         builder.addDependency(dependency);
      }
      
      // log the switchboard installation 
      if (logger.isDebugEnabled())
      {
         logger.debug("Installing SwitchBoard: " + mcBeanName + " for unit: " + unit.getSimpleName());
         Set<DependencyMetaData> dependencies = builder.getBeanMetaData().getDepends();
         if (dependencies != null)
         {
            logger.debug("with dependencies:");
            for (DependencyMetaData dependency : dependencies)
            {
               logger.debug(dependency);
            }
         }
         Set<DemandMetaData> demands = builder.getBeanMetaData().getDemands();
         if (demands != null)
         {
            logger.debug("demands:");
            for (DemandMetaData demand : demands)
            {
               logger.debug(demand);
            }
         }
         Set<SupplyMetaData> supplies = builder.getBeanMetaData().getSupplies();
         if (supplies != null)
         {
            logger.debug("supplies:");
            for (SupplyMetaData supply : supplies)
            {
               logger.debug(supply);
            }
         }
      }
      // return the switchboard BMD
      return builder.getBeanMetaData();
   }
   

   protected String getSwitchBoardMCBeanName(DeploymentUnit unit)
   {
      StringBuilder sb = new StringBuilder("jboss-switchboard:");
      String appName = this.getApplicationName(unit);
      if (appName != null)
      {
         sb.append("appName=");
         sb.append(appName);
         sb.append(",");
      }
      String moduleName = this.getModuleName(unit);
      sb.append("module=");
      sb.append(moduleName);
      if (this.informer.isJavaEEComponent(unit) && !this.isSharedENC(unit))
      {
         String componentName = this.getComponentName(unit);
         sb.append(",name=");
         sb.append(componentName);
      }

      return sb.toString();
   }
   
   /**
    * Returns the  MC bean name of either {@link JavaEEComponent} or a {@link JavaEEModule}
    * depending on the deployment unit
    * @param deploymentUnit
    * @return
    */
   protected String getENCContextMCBeanName(DeploymentUnit deploymentUnit)
   {
      String applicationName = this.getApplicationName(deploymentUnit);
      String moduleName = this.getModuleName(deploymentUnit);

      final StringBuilder builder = new StringBuilder("jboss.naming:");
      if (applicationName != null)
      {
         builder.append("application=").append(applicationName).append(",");
      }
      builder.append("module=").append(moduleName);
      if (!this.isSharedENC(deploymentUnit))
      {
         String componentName = this.getComponentName(deploymentUnit);
         builder.append(",component=").append(componentName);
      }
      return builder.toString();
   }
   
   protected boolean isSharedENC(DeploymentUnit deploymentUnit)
   {
      JBossMetaData jbossMetaData = deploymentUnit.getAttachment(JBossMetaData.class);
      JBossWebMetaData jbosswebMetaData = deploymentUnit.getAttachment(JBossWebMetaData.class);
      if (jbossMetaData != null && jbosswebMetaData != null)
      {
         return true;
      }
      return false;
   }
   
   
   protected AbstractInjectionValueMetaData getNamingContextInjectionMetaData(DeploymentUnit deploymentUnit)
   {
      String encCtxMCBeanName = this.getENCContextMCBeanName(deploymentUnit);
      AbstractInjectionValueMetaData namingContextInjectionMetaData = new AbstractInjectionValueMetaData(encCtxMCBeanName);
      return namingContextInjectionMetaData;
   }
   
   protected abstract void attachSwitchBoardBMD(DeploymentUnit deploymentUnit, BeanMetaData switchBoardBMD);
   
   protected abstract void attachBarrier(DeploymentUnit deploymentUnit, Barrier switchBoard);
   
   

}
