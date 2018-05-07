package de.hsflensburg.java.gwt.shared;

import com.google.gwt.dev.protobuf.ServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/***************************************
 * The RPC interface of the web application service.
 */
@RemoteServiceRelativePath("srv")
public interface WebAppService extends RemoteService
{

	public static final String COMMAND_GET_INITIAL_DATA = "GetInitialData";

	/***************************************
	 * Executes a command in the service.
	 *
	 * @param rCommand The command to execute
	 * @param rData The data to be processed by the command
	 *
	 * @return The resulting data element (will be NULL for commands that do not
	 *         return a result)
	 *
	 * @throws ServiceException
	 */
	public String executeCommand(String sCommand, String sData)
		throws Exception;
}
