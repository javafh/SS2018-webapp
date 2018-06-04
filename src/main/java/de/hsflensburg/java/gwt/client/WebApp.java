package de.hsflensburg.java.gwt.client;

import java.util.function.Consumer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

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

	private Label	aBlockNumberLabel	= new Label();
	private Label	aErrorLabel			= new Label();

	/***************************************
	 * The entry point method that will be invoked when the application is
	 * invoked in the client browser.
	 */
	@Override
	public void onModuleLoad()
	{
		FlowPanel aMainPanel = new FlowPanel();

		aErrorLabel.addStyleName("error");

		aMainPanel.add(new Label("Latest Ethereum Block:"));
		aMainPanel.add(aBlockNumberLabel);
		aMainPanel.add(aErrorLabel);

		RootPanel.get("webapp-ui").add(aMainPanel);

		aWebAppService.executeCommand(
			WebAppService.COMMAND_GET_LATEST_BLOCK_NUMBER, null,
			new CommandResultHandler(this::updateBlockNumber));
	}

	private void updateBlockNumber(String sResponse)
	{
		JSONValue aJsonResult = JSONParser.parseStrict(sResponse);
		String sBlockNumber;

		if (aJsonResult instanceof JSONObject)
		{
			aJsonResult = ((JSONObject) aJsonResult).get("result");
		}

		if (aJsonResult instanceof JSONString)
		{
			sBlockNumber = ((JSONString) aJsonResult).stringValue();

			sBlockNumber = Integer.valueOf(sBlockNumber.substring(2),
				16).toString();

			aBlockNumberLabel.setText(sBlockNumber);
		}
		else
		{
			aErrorLabel.setText("Unknown block number format: " + aJsonResult);
		}

	}

	private void handleServerError(Throwable rCaught)
	{
		aErrorLabel.setText("ERROR: " + rCaught.getMessage());
	}

	private class CommandResultHandler implements AsyncCallback<String>
	{
		private Consumer<String> fProcessResponse;

		public CommandResultHandler(Consumer<String> fProcessResponse)
		{
			this.fProcessResponse = fProcessResponse;
		}

		@Override
		public void onFailure(Throwable rCaught)
		{
			handleServerError(rCaught);

		}

		@Override
		public void onSuccess(String sResponse)
		{
			fProcessResponse.accept(sResponse);

		}
	}
}
