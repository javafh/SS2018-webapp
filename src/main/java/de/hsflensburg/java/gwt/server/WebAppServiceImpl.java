package de.hsflensburg.java.gwt.server;

import de.esoco.lib.json.Json;
import de.esoco.lib.json.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.hsflensburg.java.gwt.shared.WebAppService;

/***************************************
 * The server-side implementation of the RPC service.
 */
public class WebAppServiceImpl extends RemoteServiceServlet
	implements WebAppService
{
	private static final long serialVersionUID = 1L;

	private static final String JSON_RPC_RESULT_TOKEN = "\"result\":";

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
	 * Handles the command {@link WebAppService#COMMAND_GET_INITIAL_DATA}.
	 */
	public String handleGetInitialData(String sIgnored) throws Exception
	{
		try
		{
			URL aNodeUrl = new URL("https://mainnet.infura.io/");
			HttpURLConnection aConnection = (HttpURLConnection) aNodeUrl.openConnection();

			aConnection.setDoOutput(true);
			aConnection.setRequestMethod("POST");
			aConnection.setRequestProperty("Content-Type", "application/json");

			try (OutputStream rOutput = aConnection.getOutputStream())
			{

				rOutput.write(
					"{\"jsonrpc\": \"2.0\", \"id\": 1, \"method\":\"eth_blockNumber\",\"params\": []}".getBytes(
						StandardCharsets.US_ASCII));
				rOutput.flush();
			}

			try (InputStream rInput = aConnection.getInputStream())
			{
				LineNumberReader aResponseReader = new LineNumberReader(
					new InputStreamReader(rInput));
				StringBuilder aResponse = new StringBuilder();
				String sLine;

				do
				{
					sLine = aResponseReader.readLine();

					if (sLine != null)
					{
						aResponse.append(sLine).append('\n');
					}
				}
				while (sLine != null);

				JsonObject aJsonResponse = Json.parseObject(
					aResponse.toString());

				return Json.toJson(aJsonResponse.get("result"));
			}
		}
		catch (

		Exception e)
		{
			e.printStackTrace();
			return "ERROR: " + e;
		}
	}
}
