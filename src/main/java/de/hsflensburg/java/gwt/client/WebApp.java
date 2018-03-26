package de.hsflensburg.java.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.hsflensburg.java.gwt.shared.WebAppService;
import de.hsflensburg.java.gwt.shared.WebAppServiceAsync;

/***************************************
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebApp implements EntryPoint
{
	// The message displayed to the user when the server cannot be reached or
	// returns an error.
	private static final String SERVER_ERROR = "An error occurred while "
		+ "attempting to contact the server. Please check your network "
		+ "connection and try again.";

	// The web application service
	private final WebAppServiceAsync aWebAppService = GWT.create(
		WebAppService.class);

	private TextBox	aRequestField	= new TextBox();
	private Label	aErrorLabel		= new Label();
	private Button	aSendButton		= new Button("Send");

	/***************************************
	 * The entry point method that will be invoked when the application is
	 * invoked in the client browser.
	 */
	@Override
	public void onModuleLoad()
	{
		aRequestField.getElement().setPropertyString("placeholder", "Request");
		aSendButton.addStyleName("sendButton");

		RootPanel.get("nameFieldContainer").add(aRequestField);
		RootPanel.get("sendButtonContainer").add(aSendButton);
		RootPanel.get("errorLabelContainer").add(aErrorLabel);

		// Focus the cursor on the name field when the app loads
		aRequestField.setFocus(true);
		aRequestField.selectAll();

		aSendButton.addClickHandler(e -> sendTextToServer());
		aRequestField.addKeyUpHandler(e -> {
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				sendTextToServer();
			}
		});
	}

	/***************************************
	 * Send the input from the text to the server and wait for a response.
	 */
	private void sendTextToServer()
	{
		String sTextToServer = aRequestField.getText();

		if (sTextToServer.length() >= 4)
		{
			ResponseDialogBox aDialog = new ResponseDialogBox(aSendButton,
				sTextToServer);

			aErrorLabel.setText("");
			aSendButton.setEnabled(false);

			aWebAppService.send(sTextToServer, new AsyncCallback<String>()
			{
				@Override
				public void onFailure(Throwable caught)
				{
					aDialog.display("Remote Procedure Call - Failure",
						SERVER_ERROR, true);
				}

				@Override
				public void onSuccess(String sResponse)
				{
					aDialog.display("Remote Procedure Call", sResponse, false);
				}
			});
		}
		else
		{
			aErrorLabel.setText("Please enter at least four characters");
		}

	}

	static class ResponseDialogBox extends DialogBox
	{
		private static final String ERROR_RESPONSE_STYLE = "errorResponse";

		VerticalPanel	aDialogPanel	= new VerticalPanel();
		Label			aRequestLabel	= new Label();
		HTML			aResponseLabel	= new HTML();
		Button			aCloseButton	= new Button("Close");

		ResponseDialogBox(FocusWidget rEnableWidget, String sRequest)
		{
			setAnimationEnabled(true);

			aRequestLabel.setText(sRequest);

			aCloseButton.getElement().setId("closeButton");
			aCloseButton.addClickHandler(e -> {
				hide();
				rEnableWidget.setEnabled(true);
				rEnableWidget.setFocus(true);
			});

			aDialogPanel.addStyleName("dialogPanel");
			aDialogPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);

			aDialogPanel.add(new HTML("<b>Sending request to the server:</b>"));
			aDialogPanel.add(aRequestLabel);
			aDialogPanel.add(new HTML("<br><b>Server replies:</b>"));
			aDialogPanel.add(aResponseLabel);
			aDialogPanel.add(aCloseButton);

			setWidget(aDialogPanel);
		}

		/***************************************
		 * Display the dialog with the given message.
		 *
		 * @param sTitle The dialog title
		 * @param sMessage The response text to display
		 * @param bError TRUE if an error occurred
		 */
		void display(String sTitle, String sMessage, boolean bError)
		{
			setText(sTitle);
			aResponseLabel.setHTML(sMessage);

			if (bError)
			{
				aResponseLabel.addStyleName(ERROR_RESPONSE_STYLE);
			}
			else
			{
				aResponseLabel.removeStyleName(ERROR_RESPONSE_STYLE);
			}

			center();
			aCloseButton.setFocus(true);

		}
	}

}
