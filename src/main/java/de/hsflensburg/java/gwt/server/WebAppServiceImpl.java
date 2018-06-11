package de.hsflensburg.java.gwt.server;

import de.esoco.lib.comm.Endpoint;
import de.esoco.lib.comm.EndpointFunction;
import de.esoco.lib.comm.JsonRpcEndpoint;
import de.esoco.lib.json.Json;
import de.esoco.lib.json.JsonObject;

import java.lang.reflect.Method;
import java.net.URL;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
	public String executeCommand(String sCommand, String sData) throws Exception
	{
		String sMethod = "handle" + sCommand;
		Method rHandler = getClass().getMethod(sMethod, String.class);

		if (rHandler == null)
		{
			throw new IllegalArgumentException(
				"Missing command handling method " + sMethod);
		}

		Object aResult = rHandler.invoke(this, sData);

		return aResult != null ? aResult.toString() : null;
	}

	/***************************************
	 * Handles the command
	 * {@link WebAppService#COMMAND_GET_LATEST_BLOCK_NUMBER}.
	 */
	public String handleGetLatestBlockNumber(String sIgnored)
	{
		URL aUrl;
		try
		{
			Endpoint aNode = Endpoint.at("json-rpc:https://mainnet.infura.io/");

			EndpointFunction<?,
				JsonObject> fGetBlockNumber = JsonRpcEndpoint.call(
					"eth_blockNumber", JsonObject.class).on(aNode);

			JsonObject aJsonResponse = fGetBlockNumber.receive();

			return Json.toJson(aJsonResponse.get("result"));
		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}
	}
}
