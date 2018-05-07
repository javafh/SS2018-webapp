package de.hsflensburg.java.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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

	private Label	aDataLabel	= new Label();
	private Label	aErrorLabel	= new Label();

	/***************************************
	 * The entry point method that will be invoked when the application is
	 * invoked in the client browser.
	 */
	@Override
	public void onModuleLoad()
	{
		FlowPanel aMainPanel = new FlowPanel();

		aErrorLabel.addStyleName("error");

		aMainPanel.add(new Label("Data:"));
		aMainPanel.add(aErrorLabel);
		aMainPanel.add(aDataLabel);

		RootPanel.get("webapp-ui").add(aMainPanel);

		aWebAppService.executeCommand(WebAppService.COMMAND_GET_INITIAL_DATA,
			null, new InitialDataHandler());
	}

	private void handleServerError(Throwable rCaught)
	{
		aErrorLabel.setText("ERROR: " + rCaught.getMessage());
	}

	private class InitialDataHandler implements AsyncCallback<String>
	{

		@Override
		public void onFailure(Throwable rCaught)
		{
			handleServerError(rCaught);

		}

		@Override
		public void onSuccess(String sResponse)
		{
			aDataLabel.setText(sResponse);
		}
	}
}
