package com.ur.thph.modbus_urcap.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.ur.urcap.api.contribution.DaemonService;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;

/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("Activator says Modbus Service!");
		
		ModbusDaemonService modbusDaemonService = new ModbusDaemonService();
		DaemonInstallationNodeService installtionService = new DaemonInstallationNodeService(modbusDaemonService);
		
		bundleContext.registerService(DaemonService.class, modbusDaemonService, null);
		bundleContext.registerService(SwingInstallationNodeService.class,installtionService, null);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Activator says Modbus ServiceS!");
	}
}

