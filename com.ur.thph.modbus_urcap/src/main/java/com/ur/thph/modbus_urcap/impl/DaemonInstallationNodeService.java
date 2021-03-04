package com.ur.thph.modbus_urcap.impl;

import java.util.Locale;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.data.DataModel;

public class DaemonInstallationNodeService
		implements SwingInstallationNodeService<DaemonInstallationNodeContribution, DaemonInstallationNodeView> {
	
	private final ModbusDaemonService modbusDaemonService;

	public DaemonInstallationNodeService(ModbusDaemonService modbusDaemonService) {

		this.modbusDaemonService = modbusDaemonService;
	}

	@Override
	public void configureContribution(ContributionConfiguration configuration) {

	}

	@Override
	public String getTitle(Locale locale) {
		return "Modbus Daemon";
	}

	@Override
	public DaemonInstallationNodeView createView(ViewAPIProvider apiProvider) {
		return new DaemonInstallationNodeView(apiProvider);
	}

	@Override
	public DaemonInstallationNodeContribution createInstallationNode(InstallationAPIProvider apiProvider,
			DaemonInstallationNodeView view, DataModel model, CreationContext context) {
		return new DaemonInstallationNodeContribution(apiProvider, view, model, context,this.modbusDaemonService);
	}

}