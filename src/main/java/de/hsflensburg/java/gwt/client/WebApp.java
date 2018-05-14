package de.hsflensburg.java.gwt.client;

import java.util.function.Consumer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

import de.hsflensburg.java.gwt.shared.WebAppService;
import de.hsflensburg.java.gwt.shared.WebAppServiceAsync;

/***************************************
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebApp implements EntryPoint
{
	// The web application service
	private final WebAppServiceAsync aWebAppService = GWT.create(
		WebAppService.class);

	private TextArea	aResponseData	= new TextArea();
	private Label		aErrorLabel		= new Label();

	/***************************************
	 * The entry point method that will be invoked when the application is
	 * invoked in the client browser.
	 */
	@Override
	public void onModuleLoad()
	{
		FlowPanel aMainPanel = new FlowPanel();

		aErrorLabel.addStyleName("error");
		aResponseData.setReadOnly(true);

		aMainPanel.add(new Label("Data:"));
		aMainPanel.add(aErrorLabel);
		aMainPanel.add(aResponseData);

		aMainPanel.getElement().getStyle().setProperty("display", "grid");

		RootPanel.get("webapp-ui").add(aMainPanel);

		aWebAppService.executeCommand(WebAppService.COMMAND_GET_INITIAL_DATA,
			null, new CommandCallback(this::displayBlockNumber));
	}

	private void displayBlockNumber(String sResult)
	{
		JSONValue aJsonResult = JSONParser.parseStrict(sResult);

		if (aJsonResult instanceof JSONString)
		{
			String sValue = ((JSONString) aJsonResult).stringValue();

			if (sValue.startsWith("0x"))
			{
				aResponseData.setText("Current Ethereum block: "
					+ Integer.valueOf(sValue.substring(2), 16).toString());
			}
		}
	}

	private void handleServerError(Throwable rCaught)
	{
		aErrorLabel.setText("ERROR: " + rCaught.getMessage());
	}

	private class CommandCallback implements AsyncCallback<String>
	{
		private Consumer<String> fSuccessHandler;

		public CommandCallback(Consumer<String> rSuccessHandler)
		{
			fSuccessHandler = rSuccessHandler;
		}

		@Override
		public void onFailure(Throwable rCaught)
		{
			handleServerError(rCaught);

		}

		@Override
		public void onSuccess(String sResult)
		{
			fSuccessHandler.accept(sResult);
		}
	}
}
