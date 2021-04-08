package com.ur.thph.modbus_urcap.impl;

import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;

import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;

public class DaemonInstallationNodeContribution implements InstallationNodeContribution {
	
	private final ModbusDaemonService modbusDaemonService;
	private ModbusDaemonInterface modbusDaemonInterface;
	
	private final DaemonInstallationNodeView view;
	
	private Timer uiTimer;
	private boolean pauseTimer;
	private DataModel model;
	
	private static final int PORT = 40408;
	private static final String HOST = "127.0.0.1";
	
	private static final String ENABLED_KEY = "enabled";
	private static final String XMLRPC_VARIABLE = "modbus_xmlrpc";
	
	

	public DaemonInstallationNodeContribution(InstallationAPIProvider apiProvider, DaemonInstallationNodeView view,
			DataModel model, CreationContext context, ModbusDaemonService modbusDaemonService) {
		
		this.modbusDaemonService = modbusDaemonService;
		this.modbusDaemonInterface = new ModbusDaemonInterface(HOST, PORT);
		this.pauseTimer = false;
		this.model = model;
		this.view = view;
	
		applyDesiredDaemonStatus();
	}

	private boolean getCB() {
		return Boolean.valueOf((model.get(ENABLED_KEY, true)));
	}

	@Override
	public void openView() {
		if (getCB() && (DaemonContribution.State.STOPPED == this.modbusDaemonService.getDaemon().getState())) {
			this.modbusDaemonService.getDaemon().start();
		} else if (getCB() == false) {
			this.modbusDaemonService.getDaemon().stop();
		}

		// UI updates from non-GUI threads must use EventQueue.invokeLater (or
		// SwingUtilities.invokeLater)
		uiTimer = new Timer(true);
		uiTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateUI();
					}
				});
			}
		}, 0, 1000);

	}

	@Override
	public void closeView() {
		if (uiTimer != null) {
			uiTimer.cancel();
		}

	}

	@Override
	public void generateScript(ScriptWriter writer) {
		writer.assign(XMLRPC_VARIABLE, "rpc_factory(\"xmlrpc\", \"http://127.0.0.1:40408/RPC2\")");
		
		writer.appendLine("isConnected = modbus_xmlrpc.reachable()");
		writer.appendLine("if ( isConnected != True):");
		writer.appendLine("popup(\"Modbus xmlrpc is not available!\")");
		writer.appendLine("end");
		
		//Modbus init method: ex --> init_modbus(65)
		writer.appendLine("def init_tool_modbus(address):");
		writer.appendLine("local response = modbus_xmlrpc.init_modbus_communication(address)");
		writer.appendLine("return response");
		writer.appendLine("end");
		
		//Modbus read method: ex --> tool_modbus_write((0, 511)
		writer.appendLine("def tool_modbus_write(register_address, data):");
		writer.appendLine("local response = modbus_xmlrpc.tool_modbus_write(register_address, data)");
		writer.appendLine("return response");
		writer.appendLine("end");

		//Modbus write method: ex --> tool_modbus_read(258)
		writer.appendLine("def tool_modbus_read(register_address):");
		writer.appendLine("local response = modbus_xmlrpc.tool_modbus_read(register_address)");
		writer.appendLine("return response");
		writer.appendLine("end");
	}
	
	private void updateUI() {
		DaemonContribution.State state = getDaemonState();

		if (state == DaemonContribution.State.RUNNING || state == DaemonContribution.State.ERROR) {
			view.setStartButtonEnabled(false);
			view.setStopButtonEnabled(true);
		} else {
			view.setStartButtonEnabled(true);
			view.setStopButtonEnabled(false);
		}

		String text = "";
		switch (state) {
		case RUNNING:
			text = "My Daemon Swing runs";
			break;
		case STOPPED:
			text = "My Daemon Swing stopped";
			break;
		case ERROR:
			text = "My Daemon Swing failed";
			break;
		}

		view.setStatusLabel(text);
	}
	
	
	private DaemonContribution.State getDaemonState() {
		return this.modbusDaemonService.getDaemon().getState();

	}
	private Boolean isDaemonEnabled() {
		return  Boolean.valueOf(model.get(ENABLED_KEY, true)); // This daemon is enabled by default
	}

	public void onStartClick() {
		model.set(ENABLED_KEY, true);
		applyDesiredDaemonStatus();
	}

	public void onStopClick() {
		model.set(ENABLED_KEY, false);
		applyDesiredDaemonStatus();
	}


	public ModbusDaemonInterface getXmlRpcDaemonInterface() {
		return this.modbusDaemonInterface;
	}

	private void applyDesiredDaemonStatus() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (isDaemonEnabled().booleanValue()) {
					// Download the daemon settings to the daemon process on initial start for
					// real-time preview purposes
					System.out.println("Starting daemon");
					try {
						pauseTimer = true;
						awaitDaemonRunning(5000);
						boolean test = modbusDaemonInterface.isReachable();
						if(test) {
							System.out.println("Daemon is running");
						}else {
							System.out.println("Daemon is not running");
						}
					} catch (Exception e) {
						System.err.println("Could not reach the daemon process.");
					} finally {
						pauseTimer = false;
					}
				} else {
					modbusDaemonService.getDaemon().stop();
				}
			}
		}).start();
	}

	private void awaitDaemonRunning(long timeOutMilliSeconds) throws InterruptedException {
		this.modbusDaemonService.getDaemon().start();
		long endTime = System.nanoTime() + timeOutMilliSeconds * 1000L * 1000L;
		while (System.nanoTime() < endTime
				&& (this.modbusDaemonService.getDaemon().getState() != DaemonContribution.State.RUNNING
						|| !modbusDaemonInterface.isReachable())) {

			Thread.sleep(100);

		}
	}

	public String getXMLRPCVariable() {
		return XMLRPC_VARIABLE;
	}
}