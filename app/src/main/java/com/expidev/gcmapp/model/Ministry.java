package com.expidev.gcmapp.model;

import java.io.Serializable;

/**
 * Created by William.Randall on 1/9/2015.
 */
public class Ministry extends Base implements Serializable
{
    private static final long serialVersionUID = 0L;

    private String ministryId;
    private String name;

    public String getMinistryId()
    {
        return ministryId;
    }

    public void setMinistryId(String ministryId)
    {
        this.ministryId = ministryId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
