package org.intersect.mgwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;
import com.googlecode.mgwt.ui.client.util.SuperDevModeUtil;

/**
 * @author Gabriel Gasser Noblia
 */
public class IntersectEntryPoint implements EntryPoint {

	private void start() {
		SuperDevModeUtil.showDevMode();

		ViewPort viewPort = new MGWTSettings.ViewPort();
		viewPort.setTargetDensity(DENSITY.MEDIUM);
		viewPort.setUserScaleAble(false).setMinimumScale(1.0)
				.setMinimumScale(1.0).setMaximumScale(1.0);

		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewPort);
		settings.setIconUrl("logo.png");
		settings.setAddGlosToIcon(true);
		settings.setFullscreen(true);
		settings.setPreventScrolling(true);

		MGWT.applySettings(settings);

		SimplePanel mainPanel = new SimplePanel();
		mainPanel.setStyleName("mainPanel");
		
		final SimplePanel floatPanel = new SimplePanel();
		floatPanel.setStyleName("floatPanel");
		floatPanel.setVisible(false);
		floatPanel.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				floatPanel.setVisible(false);
			}
		}, ClickEvent.getType());
		
		new Timer() {
			
			@Override
			public void run() {
				floatPanel.setVisible(true);
			}
		}.schedule(5000);
		
		
		mainPanel.setWidget(floatPanel);
		

		RootPanel.get().add(mainPanel);

	}

	@Override
	public void onModuleLoad() {

		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void onUncaughtException(Throwable e) {
				Window.alert("uncaught: " + e.getMessage());
				String s = buildStackTrace(e, "RuntimeExceotion:\n");
				Window.alert(s);
				e.printStackTrace();
			}
		});

		new Timer() {

			@Override
			public void run() {
				start();

			}
		}.schedule(1);

	}

	/**
	 * 
	 * @param t
	 * @param log
	 * @return
	 */
	private String buildStackTrace(Throwable t, String log) {
		if (t != null) {
			log += t.getClass().toString();
			log += t.getMessage();
			//
			StackTraceElement[] stackTrace = t.getStackTrace();
			if (stackTrace != null) {
				StringBuffer trace = new StringBuffer();

				for (int i = 0; i < stackTrace.length; i++) {
					trace.append(stackTrace[i].getClassName() + "."
							+ stackTrace[i].getMethodName() + "("
							+ stackTrace[i].getFileName() + ":"
							+ stackTrace[i].getLineNumber());
				}

				log += trace.toString();
			}
			//
			Throwable cause = t.getCause();
			if (cause != null && cause != t) {

				log += buildStackTrace(cause, "CausedBy:\n");

			}
		}
		return log;
	}

}
