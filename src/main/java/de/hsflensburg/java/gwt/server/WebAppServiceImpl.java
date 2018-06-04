package de.hsflensburg.java.gwt.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

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
	 * Handles the command {@link WebAppService#COMMAND_GET_INITIAL_DATA}.
	 */
	public String handleGetInitialData(String sIgnored)
	{
		URL aUrl;
		try
		{
			aUrl = new URL("https://mainnet.infura.io/");

			URLConnection rConnection = aUrl.openConnection();

			if (rConnection instanceof HttpURLConnection)
			{
				HttpURLConnection rHttpConnection = (HttpURLConnection) rConnection;

				rHttpConnection.setDoOutput(true);
				rHttpConnection.setRequestMethod("POST");
				rHttpConnection.setRequestProperty("Content-Type",
					"application/json");

				String sRequest = "{\"jsonrpc\": \"2.0\", \"id\": 1, "
					+ "\"method\": \"eth_blockNumber\",\"params\": []}";

				try (OutputStream rOutput = rHttpConnection.getOutputStream())
				{
					rOutput.write(sRequest.getBytes(StandardCharsets.US_ASCII));
				}

				try (InputStream rInput = rHttpConnection.getInputStream())
				{
					LineNumberReader aResponseReader = new LineNumberReader(
						new BufferedReader(new InputStreamReader(rInput)));
					StringBuilder aResponse = new StringBuilder();
					String sLine;

					do
					{
						sLine = aResponseReader.readLine();

						if (sLine != null)
						{
							aResponse.append(sLine);
							aResponse.append('\n');
						}

					}
					while (sLine != null);

					return aResponse.toString();
				}

			}
			else
			{
				throw new ServiceException("No HTTP connection for " + aUrl);
			}

		}
		catch (Exception e)
		{
			throw new ServiceException(e);
		}
	}
}
