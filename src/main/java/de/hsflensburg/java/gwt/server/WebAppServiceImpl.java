package de.hsflensburg.java.gwt.server;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
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
					"{\"jsonrpc\": \"2.0\", \"id\": 1, \"method\": \"eth_blockNumber\",\"params\": []}".getBytes(
						StandardCharsets.US_ASCII));
				rOutput.flush();
			}

			try (InputStream rInput = aConnection.getInputStream())
			{
				Reader aResponseReader = new InputStreamReader(rInput);
				StringBuilder aResponse = new StringBuilder();
				int nChar;

				do
				{
					nChar = aResponseReader.read();

					if (nChar >= 0)
					{
						aResponse.append((char) nChar);
					}
				}
				while (nChar >= 0);

				String sData = aResponse.toString();

				int nResultIndex = sData.indexOf(JSON_RPC_RESULT_TOKEN);

				if (nResultIndex >= 0)
				{
					sData = sData.substring(
						nResultIndex + JSON_RPC_RESULT_TOKEN.length(),
						sData.lastIndexOf('}')).trim();
				}
				else
				{
					throw new IllegalStateException(
						"Could not parse as JSON RPC result: " + sData);
				}

				return sData;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "ERROR: " + e;
		}
	}
}
