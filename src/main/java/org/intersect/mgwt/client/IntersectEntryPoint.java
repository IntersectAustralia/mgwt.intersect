package org.intersect.mgwt.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;
import com.googlecode.mgwt.ui.client.util.SuperDevModeUtil;

/**
 * @author Gabriel Gasser Noblia
 */
public class IntersectEntryPoint implements EntryPoint {

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
				SuperDevModeUtil.showDevMode();

				readConfigAndStart();

			}
		}.schedule(1);
	}

	/**
	 * 
	 */
	private void readConfigAndStart() {
		final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, "experimentsConfig.json");
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("failed getting configuration file", exception);
				}

				public void onResponseReceived(Request request, Response response) {
					String configString = response.getText();
					try {
						JSONValue jsonValue = JSONParser.parseStrict(configString);
						
						Map<Integer, Experiment> experimentsMap = buildExperiments(jsonValue.isArray());
						
						setUpMGWT();
						showExperimentSelectionScreen(experimentsMap);
					} catch (Exception e) {
						VerticalPanel errorPanel = new VerticalPanel();
						errorPanel.setStyleName("errorPanel");
						Label errorText = new Label("ERROR:");
						errorText.setStyleName("errorText");
						Label errorMessage = new Label(" Error starting application. Please verify the configuration file.");
						errorMessage.setStyleName("errorMessage");
						errorPanel.add(errorText);
						errorPanel.add(errorMessage);
						
						RootPanel.get().add(errorPanel);
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("failed getting movie list", e);
		}
	}

	/**
	 * 
	 */
	private void setUpMGWT() {
		ViewPort viewPort = new MGWTSettings.ViewPort();
		viewPort.setTargetDensity(DENSITY.MEDIUM);
		viewPort.setUserScaleAble(false).setMinimumScale(1.0).setMinimumScale(1.0).setMaximumScale(1.0);

		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewPort);
		settings.setIconUrl("logo.png");
		settings.setAddGlosToIcon(true);
		settings.setFullscreen(true);
		settings.setPreventScrolling(true);

		MGWT.applySettings(settings);
	}

	/**
	 * 
	 * @param JSONArray
	 */
	private Map<Integer, Experiment> buildExperiments(JSONArray jsonArray) {

		Map<Integer, Experiment> experimentsMap = new HashMap<Integer, Experiment>();

		if (jsonArray != null) {
			JSONValue jsonValue;
			JSONObject jsExperiment;
			JSONString jsName, jsBkImage, jsFgImage;
			JSONNumber jsExperimentNo, jsTop, jsLeft, jsDelay;

			for (int x = 0; x < jsonArray.size(); x++) {
				// Validate the content of the JSON file
				if ((jsExperiment = jsonArray.get(x).isObject()) == null) continue;
				
				// Experiment number
				if ((jsonValue = jsExperiment.get("experiment_no")) == null) continue;
				if ((jsExperimentNo = jsonValue.isNumber()) == null) continue;
				
				// Name
				if ((jsonValue = jsExperiment.get("name")) == null) continue;
				if ((jsName = jsonValue.isString()) == null) continue;
								
				// Background Image
				if ((jsonValue = jsExperiment.get("bk-image")) == null) continue;
				if ((jsBkImage = jsonValue.isString()) == null) continue;

				// Foreground Image
				if ((jsonValue = jsExperiment.get("fg-image")) == null) continue;
				if ((jsFgImage = jsonValue.isString()) == null) continue;

				// Top
				if ((jsonValue = jsExperiment.get("top")) == null) continue;
				if ((jsTop = jsonValue.isNumber()) == null) continue;
				
				// Left
				if ((jsonValue = jsExperiment.get("left")) == null) continue;
				if ((jsLeft = jsonValue.isNumber()) == null) continue;
				
				// Delay in ms
				if ((jsonValue = jsExperiment.get("delay_in_milliseconds")) == null) continue;
				if ((jsDelay = jsonValue.isNumber()) == null) continue;
				
				
				Experiment experiment = new Experiment();
				experiment.setId((int) jsExperimentNo.doubleValue());
				experiment.setName(jsName.stringValue());
				experiment.setBackgroudImage(jsBkImage.stringValue());
				experiment.setForegroundImage(jsFgImage.stringValue());
				experiment.setTop((int) jsTop.doubleValue());
				experiment.setLeft((int) jsLeft.doubleValue());
				experiment.setDelay((int) jsDelay.doubleValue());
				
				if (!experimentsMap.containsKey(experiment.getId())) {
					experimentsMap.put(experiment.getId(), experiment);					
				} else {
					Window.alert("ERROR:\n\nExperiment number " + experiment.getId() + " already exists. \nExperiment number must be unique, please verify the configuration file.");
				}
			}

		}

		return experimentsMap;
		
	}

	/**
	 * 
	 * @param experimentsMap
	 */
	private void showExperimentSelectionScreen(final Map<Integer, Experiment> experimentsMap) {
		final VerticalPanel mainSelectorPanel = new VerticalPanel();
		mainSelectorPanel.setStyleName("selectionPanel");
		
		final ListBox experimentSelector = new ListBox();
		experimentSelector.setStyleName("selector");
		experimentSelector.addItem("-- select -- ", "NON");
		for (Experiment anExperiment : experimentsMap.values()) {
			experimentSelector.addItem(anExperiment.getName(), String.valueOf(anExperiment.getId()));
		}
		
		Button startExperiment = new Button("Start Experiment");
		startExperiment.setStyleName("startExperiment");
		startExperiment.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String strSelectedValue = experimentSelector.getValue(experimentSelector.getSelectedIndex());
				if (!"NON".equals(strSelectedValue)) {
					Integer selectedExperimentId = Integer.parseInt(strSelectedValue);
					RootPanel.get().remove(mainSelectorPanel);
					showExperiment(experimentsMap.get(selectedExperimentId));					
				} else {
					Window.alert("Please select an experiment.");
				}
				
			}
		});
		
		
		Label label = new Label("Select an experiment");
		label.setStyleName("label");
		
		mainSelectorPanel.add(label);
		mainSelectorPanel.add(experimentSelector);
		mainSelectorPanel.add(startExperiment);
		
		RootPanel.get().add(mainSelectorPanel);
	}
	
	/**
	 * 
	 */
	private void showExperiment(Experiment experiment) {

		SimplePanel mainPanel = new SimplePanel();
		mainPanel.setStyleName("mainPanel");
		mainPanel.getElement().getStyle().setBackgroundImage("url('images/" + experiment.getBackgroudImage() + "')");
		
		
		final SimplePanel floatPanel = new SimplePanel();
		floatPanel.setStyleName("floatPanel");
		floatPanel.getElement().getStyle().setBackgroundImage("url('images/" + experiment.getForegroundImage() + "')");
		floatPanel.getElement().getStyle().setTop(experiment.getTop(), Unit.PX);
		floatPanel.getElement().getStyle().setLeft(experiment.getLeft(), Unit.PX);
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
		}.schedule(experiment.getDelay());

		mainPanel.setWidget(floatPanel);

		RootPanel.get().add(mainPanel);
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
					trace.append(stackTrace[i].getClassName() + "." + stackTrace[i].getMethodName() + "(" + stackTrace[i].getFileName() + ":"
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
