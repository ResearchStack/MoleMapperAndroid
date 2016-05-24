package org.researchstack.molemapper.bridge;

import java.util.List;

/**
 * Created by bradleymcdermott on 3/1/16.
 */
public class MoleRemoved
{
    public static final String FILENAME = "removedMoleData.json";
    public static final String ITEM     = "removedMoleData";
    public static final int    REVISION = 1;

    public String       moleID;
    public List<String> diagnoses;

    public MoleRemoved(String moleID, List<String> diagnoses)
    {
        this.moleID = moleID;
        this.diagnoses = diagnoses;
    }
}
