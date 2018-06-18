package de.hsflensburg.java.gwt.shared;

public class JsonRpcCommand extends Command
{
	public static JsonRpcCommand call(String sMethod, String sParams)
	{
		return new JsonRpcCommand(sMethod, sParams);
	}

	private String sMethod;

	/********************************************
	 * Default constructor for GWT serialization.
	 */
	JsonRpcCommand()
	{
	}

	private JsonRpcCommand(String sMethod, String sData)
	{
		super(JsonRpcCommand.class.getSimpleName(), sData);

		this.sMethod = sMethod;
	}

	public String getMethod()
	{
		return sMethod;
	}

}
