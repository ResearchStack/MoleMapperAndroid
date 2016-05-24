package org.researchstack.molemapper.bridge;
import java.util.List;
import java.util.Map;

public class BridgeErrorResponse
{
    public String                    message;
    public Map<String, List<String>> errors;

    public BridgeErrorResponse()
    {
    }
}
