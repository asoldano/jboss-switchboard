/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.switchboard.mc.resource.provider;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.metadata.serviceref.VirtualFileAdaptor;
import org.jboss.switchboard.impl.resource.IndependentResource;
import org.jboss.switchboard.javaee.environment.Address;
import org.jboss.switchboard.javaee.environment.Handler;
import org.jboss.switchboard.javaee.environment.HandlerChainType;
import org.jboss.switchboard.javaee.environment.PortComponent;
import org.jboss.switchboard.javaee.environment.ResponseType;
import org.jboss.switchboard.javaee.environment.ServiceRefType;
import org.jboss.switchboard.jbmeta.javaee.environment.JBossPortComponentReference;
import org.jboss.switchboard.jbmeta.javaee.environment.JBossServiceReference;
import org.jboss.switchboard.mc.spi.MCBasedResourceProvider;
import org.jboss.switchboard.spi.Resource;
import org.jboss.wsf.common.ResourceLoaderAdapter;
import org.jboss.wsf.spi.deployment.UnifiedVirtualFile;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedCallPropertyMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainsMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedInitParamMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedPortComponentRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedServiceRefMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedStubPropertyMetaData;

/**
 * 
 * @author alessio.soldano@jboss.com
 * @since 04-Nov-2010
 *
 */
public class ServiceRefResourceProvider implements MCBasedResourceProvider<ServiceRefType>
{

   @Override
   public Resource provide(DeploymentUnit deploymentUnit, ServiceRefType serviceRefType)
   {
      UnifiedVirtualFile uvf = getUnifiedVirtualFile(deploymentUnit);
      UnifiedServiceRefMetaData target = getUnifiedServiceRefMetaData(uvf, serviceRefType);
      return new IndependentResource(target);
   }

   @Override
   public Class<ServiceRefType> getEnvironmentEntryType()
   {
      return ServiceRefType.class;
   }

   private UnifiedVirtualFile getUnifiedVirtualFile(final DeploymentUnit deploymentUnit)
   {
      if (deploymentUnit instanceof VFSDeploymentUnit)
      {
         return new VirtualFileAdaptor(((VFSDeploymentUnit) deploymentUnit).getRoot());
      }
      else
      {
         return new ResourceLoaderAdapter(deploymentUnit.getClassLoader());
      }
   }
   
   private UnifiedServiceRefMetaData getUnifiedServiceRefMetaData(UnifiedVirtualFile vfsRoot, ServiceRefType sref)
   {
      UnifiedServiceRefMetaData result = new UnifiedServiceRefMetaData(vfsRoot);
      //result.setServiceRefName(sref.getServiceRefName()); //TODO!!
      result.setServiceRefType(sref.getType());
      result.setServiceInterface(sref.getServiceInterface());
      result.setWsdlFile(sref.getWsdlFile());
      result.setMappingFile(sref.getMappingFile());
      result.setServiceQName(sref.getQName());
      /**  //TODO!!
      result.setAddressingEnabled(sref.isAddressingEnabled());
      result.setAddressingRequired(sref.isAddressingRequired());
      result.setAddressingResponses(sref.getAddressingResponses());
      result.setMtomEnabled(sref.isMtomEnabled());
      result.setMtomThreshold(sref.getMtomThreshold());
      result.setRespectBindingEnabled(sref.isRespectBindingEnabled());
      **/
      result.setHandlerChain(sref.getHandlerChain());

      Collection<? extends PortComponent> pcRefs = sref.getPortComponents();
      if (pcRefs != null)
      {
         for (PortComponent pcRef : pcRefs)
         {
            UnifiedPortComponentRefMetaData upcRef = getUnifiedPortComponentRefMetaData(result, pcRef);
            if (upcRef.getServiceEndpointInterface() != null || upcRef.getPortQName() != null)
               result.addPortComponentRef(upcRef);
            else
               Logger.getLogger(this.getClass()).warn("Ignore <port-component-ref> without <service-endpoint-interface> and <port-qname>: " + upcRef);
         }
      }

      Collection<Handler> srHandlers = sref.getHandlers();
      if (srHandlers != null)
      {
         Iterator<Handler> it = srHandlers.iterator();
         while (it.hasNext())
         {
            Handler srHandlerMetaData = it.next();
            UnifiedHandlerMetaData uHandlerMetaData = getUnifiedHandlerMetaData(srHandlerMetaData);
            result.addHandler(uHandlerMetaData);
         }
      }

      List<HandlerChainType> srHandlerChains = sref.getHandlerChains();
      if (srHandlerChains != null)
      {
         UnifiedHandlerChainsMetaData uHandlerChains = new UnifiedHandlerChainsMetaData();
         for (HandlerChainType srHandlerChain : srHandlerChains)
         {
            UnifiedHandlerChainMetaData uHandlerChain = new UnifiedHandlerChainMetaData();
            uHandlerChain.setServiceNamePattern(srHandlerChain.getServiceNamePattern());
            uHandlerChain.setPortNamePattern(srHandlerChain.getPortNamePattern());
            uHandlerChain.setProtocolBindings(srHandlerChain.getProtocolBindings());
            List<Handler> srHandlerChainHandlers = srHandlerChain.getHandlers();
            for (Handler h : srHandlerChainHandlers)
            {
               UnifiedHandlerMetaData uHandlerMetaData = getUnifiedHandlerMetaData(h);
               uHandlerChain.addHandler(uHandlerMetaData);
            }
            uHandlerChains.addHandlerChain(uHandlerChain);
         }
         result.setHandlerChains(uHandlerChains);
      }
      if (sref instanceof JBossServiceReference)
      {
         JBossServiceReference jbRef = (JBossServiceReference)sref;
         if (jbRef.getServiceClass() != null)
         {
            result.setServiceImplClass(jbRef.getServiceClass());
         }
         result.setConfigName(jbRef.getConfigName());
         result.setConfigFile(jbRef.getConfigFile());
         result.setWsdlOverride(jbRef.getWsdlOverride());
         result.setHandlerChain(jbRef.getHandlerChain());
      }
      
      return result;
   }

   private UnifiedHandlerMetaData getUnifiedHandlerMetaData(Handler srhmd)
   {
      UnifiedHandlerMetaData uhmd = new UnifiedHandlerMetaData();
//      uhmd.setHandlerName(srhmd.getHandlerName()); //TODO!!
      uhmd.setHandlerClass(srhmd.getHandlerClass());
      Map<String, String> initParams = srhmd.getInitParams();
      if (initParams != null)
      {
         for (String k : initParams.keySet())
         {
            UnifiedInitParamMetaData param = new UnifiedInitParamMetaData();
            param.setParamName(k);
            param.setParamValue(initParams.get(k));
            uhmd.addInitParam(param);
         }
      }
      Collection<QName> soapHeaders = srhmd.getSoapHeaders();
      if (soapHeaders != null)
      {
         for (QName soapHeader : soapHeaders)
         {
            uhmd.addSoapHeader(soapHeader);
         }
      }
      Collection<String> soapRoles = srhmd.getSoapRoles();
      if (soapRoles != null)
      {
         for (String soapRole : soapRoles)
         {
            uhmd.addSoapRole(soapRole);
         }
      }
      Collection<String> portNames = srhmd.getPortNames();
      if (portNames != null)
      {
         for (String portName : portNames)
         {
            uhmd.addPortName(portName);
         }
      }
      return uhmd;
   }

   private UnifiedPortComponentRefMetaData getUnifiedPortComponentRefMetaData(UnifiedServiceRefMetaData usref, PortComponent pcref)
   {
      UnifiedPortComponentRefMetaData result = new UnifiedPortComponentRefMetaData(usref);
      result.setServiceEndpointInterface(pcref.getEndpointInterface());
      result.setMtomEnabled(pcref.isMtomEnabled());
      result.setMtomThreshold(pcref.getMtomThreshold());
      Address addressing = pcref.getAddress();
      if (addressing != null) 
      {
         result.setAddressingEnabled(addressing.isEnabled());
         result.setAddressingRequired(addressing.isRequired());
         if (addressing.getResponseType().equals(ResponseType.NON_ANONYMOUS))
         {
            result.setAddressingResponses("NON_ANONYMOUS");
         }
         else if (addressing.getResponseType().equals(ResponseType.ANONYMOUS))
         {
            result.setAddressingResponses("ANONYMOUS");
         }
         else
         {
            result.setAddressingResponses("ALL");
         }
      }
      result.setRespectBindingEnabled(pcref.isBindingRespected());
      result.setPortComponentLink(pcref.getLink());
      if (pcref instanceof JBossPortComponentReference)
      {
         JBossPortComponentReference jbpcref = (JBossPortComponentReference)pcref;
         result.setPortQName(jbpcref.getPortQName());
         result.setConfigName(jbpcref.getConfigName());
         result.setConfigFile(jbpcref.getConfigFile());
         Map<String, String> stubProps = jbpcref.getStubProperties();
         if (stubProps != null)
         {
            for (String key : stubProps.keySet())
            {
               UnifiedStubPropertyMetaData prop = new UnifiedStubPropertyMetaData();
               prop.setPropName(key);
               prop.setPropValue(stubProps.get(key));
               result.addStubProperty(prop);
            }
         }
         
         Map<String, String> callProps = jbpcref.getCallProperties();
         if (callProps != null)
         {
            for (String key : callProps.keySet())
            {
               UnifiedCallPropertyMetaData prop = new UnifiedCallPropertyMetaData();
               prop.setPropName(key);
               prop.setPropValue(callProps.get(key));
               result.addCallProperty(prop);
            }
         }
      }
      return result;
   }

}
