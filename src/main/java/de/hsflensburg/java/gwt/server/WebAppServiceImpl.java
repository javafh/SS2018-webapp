package de.hsflensburg.java.gwt.server;

import de.esoco.lib.comm.Endpoint;
import de.esoco.lib.comm.JsonRpcEndpoint.JsonRpcMethod;
import de.esoco.lib.json.Json;

import java.lang.reflect.Method;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.hsflensburg.java.gwt.shared.Command;
import de.hsflensburg.java.gwt.shared.JsonRpcCommand;
import de.hsflensburg.java.gwt.shared.ServiceException;
import de.hsflensburg.java.gwt.shared.WebAppService;

/***************************************
 * The server-side implementation of the RPC service.
 */
public class WebAppServiceImpl extends RemoteServiceServlet
	implements WebAppService
{
	private static final long serialVersionUID = 1L;

	/***************************************
	 * Escape an HTML string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 *
	 * @param sHtml the HTML string to escape
	 *
	 * @return the escaped string
	 */
	public static String escapeHtml(String sHtml)
	{
		if (sHtml != null)
		{
			sHtml = sHtml.replaceAll("&", "&amp;").replaceAll("<",
				"&lt;").replaceAll(">", "&gt;");
		}

		return sHtml;
	}

	/***************************************
	 * {@inheritDoc}
	 */
	@Override
	public String executeCommand(Command rCommand) throws Exception
	{
		String sMethod = "handle" + rCommand;
		Method rHandler = getClass().getMethod(sMethod, rCommand.getClass());

		if (rHandler == null)
		{
			throw new IllegalArgumentException(
				"Missing command handling method " + sMethod);
		}

		Object aResult = rHandler.invoke(this, rCommand);

		return aResult != null ? aResult.toString() : null;
	}

	/***************************************
	 * Handles {@link JsonRpcCommand} execution.
	 */
	public String handleJsonRpcCommand(JsonRpcCommand rCommand)
	{
		try
		{
			Endpoint aNode = Endpoint.at("json-rpc:https://mainnet.infura.io/");

			JsonRpcMethod<String,
				Object> fRpcCall = new JsonRpcMethod<>(rCommand.getMethod(),
					rCommand.getData(), sJson -> Json.parse(sJson));

			Object rResponse = fRpcCall.on(aNode).receive();

			return Json.toJson(rResponse);
		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}
	}
}
