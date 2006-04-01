package org.ops4j.pax.wicket;

public class ResourceInfo 
{
	private String m_Alias;
	private String m_Name;

	public ResourceInfo( String alias, String name )
	{
		m_Alias = alias;
		m_Name = name;
	}
	
	public String getAlias() 
	{
		return m_Alias;
	}

	public void setAlias(String alias) 
	{
		m_Alias = alias;
	}

	public String getName() 
	{
		return m_Name;
	}

	public void setName(String name) 
	{
		m_Name = name;
	}

}
