package de.hsflensburg.java.gwt.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/***************************************
 * The RPC interface of the web application service.
 */
@RemoteServiceRelativePath("srv")
public interface WebAppService extends RemoteService
{
	/***************************************
	 * Send a text to the server.
	 *
	 * @param sText The text to send
	 *
	 * @return The response from the server
	 * @throws IllegalArgumentException
	 */
	String send(String sText);
}
