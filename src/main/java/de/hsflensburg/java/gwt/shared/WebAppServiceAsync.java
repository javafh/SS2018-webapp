package de.hsflensburg.java.gwt.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

/***************************************
 * The asynchronous variant of {@link WebAppService}.
 */
public interface WebAppServiceAsync
{

	void executeCommand(Command rCommand, AsyncCallback<String> callback);
}
