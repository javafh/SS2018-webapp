package de.hsflensburg.java.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class Command implements IsSerializable
{
	private String	sName;
	private String	sData;

	public Command(String sName, String sData)
	{
		this.sName = sName;
		this.sData = sData;
	}

	/********************************************
	 * Default constructor for GWT serialization.
	 */
	Command()
	{

	}

	public String getData()
	{
		return sData;
	}

	public String getName()
	{
		return sName;
	}

	@Override
	public String toString()
	{
		return sName;
	}
}
