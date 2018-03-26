package de.hsflensburg.java.gwt.server;

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
	 * {@inheritDoc}
	 */
	@Override
	public String send(String sText)
	{
		String sUserAgent = getThreadLocalRequest().getHeader("User-Agent");
		String sServerInfo = getServletContext().getServerInfo();

		// Escape data from the client to prevent cross-site scripting
		sText = escapeHtml(sText);
		sUserAgent = escapeHtml(sUserAgent);

		return String.format(
			"<b>Your Text</b>: %s<br><b>User Agent</b>: %s<br><b>Server</b>: %s",
			sText, sUserAgent, sServerInfo);
	}

	/***************************************
	 * Escape an HTML string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 *
	 * @param sHtml the HTML string to escape
	 *
	 * @return the escaped string
	 */
	private String escapeHtml(String sHtml)
	{
		if (sHtml != null)
		{
			sHtml = sHtml.replaceAll("&", "&amp;").replaceAll("<",
				"&lt;").replaceAll(">", "&gt;");
		}

		return sHtml;
	}
}
