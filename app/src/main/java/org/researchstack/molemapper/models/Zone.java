package org.researchstack.molemapper.models;
import java.io.Serializable;
import java.util.List;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.field.ForeignCollectionField;
import co.touchlab.squeaky.table.DatabaseTable;

/**
 * Created by bradleymcdermott on 2/4/16.
 */
@DatabaseTable
public class Zone implements Serializable
{
    @DatabaseField(id = true)
    public int id;

    @DatabaseField
    public String photo;

    @ForeignCollectionField(eager = true, foreignFieldName = "zone")
    public List<Mole> moles;

    public Zone()
    {
    }
}
