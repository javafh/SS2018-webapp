package de.hsflensburg.java.gwt.shared;

public class ServiceException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ServiceException(String sMessage)
	{
		super(sMessage);
	}

	public ServiceException(Exception e)
	{
		super(e);
	}

	public ServiceException(String sMessage, Exception e)
	{
		super(sMessage, e);
	}
}
