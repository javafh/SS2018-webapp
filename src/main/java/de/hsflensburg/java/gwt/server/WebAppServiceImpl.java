package de.hsflensburg.java.gwt.server;

import java.lang.reflect.Method;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
		return "This is the initial data";
	}
}
