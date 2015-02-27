package com.expidev.gcmapp.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by William.Randall on 1/9/2015.
 */
public class Ministry extends Base implements Serializable
{
    private static final long serialVersionUID = 0L;

    public enum Mcc {
        UNKNOWN(null), SLM("slm"), LLM("llm"), DS("ds"), GCM("gcm");

        @Nullable
        public final String raw;

        private Mcc(final String raw) {
            this.raw = raw;
        }

        @NonNull
        public static Mcc fromRaw(@Nullable final String raw) {
            switch (raw != null ? raw.toLowerCase(Locale.US) : "") {
                case "slm":
                    return SLM;
                case "llm":
                    return LLM;
                case "ds":
                    return DS;
                case "gcm":
                    return GCM;
                default:
                    return UNKNOWN;
            }
        }
    }

    public static final String INVALID_ID = "";

    @NonNull
    private String ministryId = INVALID_ID;
    private String name;

    @NonNull
    public String getMinistryId()
    {
        return ministryId;
    }

    public void setMinistryId(@NonNull final String ministryId)
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
