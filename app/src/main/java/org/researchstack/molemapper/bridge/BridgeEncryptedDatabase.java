package org.researchstack.molemapper.bridge;
import android.content.Context;
import android.text.format.DateUtils;

import net.sqlcipher.database.SQLiteDatabase;

import org.researchstack.backbone.storage.database.sqlite.SqlCipherDatabaseHelper;
import org.researchstack.backbone.storage.database.sqlite.UpdatablePassphraseProvider;
import org.researchstack.backbone.utils.LogExt;

import java.sql.SQLException;
import java.util.List;

import co.touchlab.squeaky.dao.Dao;
import co.touchlab.squeaky.db.sqlcipher.SQLiteDatabaseImpl;
import co.touchlab.squeaky.table.TableUtils;

/**
 * Created by bradleymcdermott on 3/8/16.
 */
public class BridgeEncryptedDatabase extends SqlCipherDatabaseHelper implements UploadQueue
{
    public BridgeEncryptedDatabase(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version, UpdatablePassphraseProvider passphraseProvider)
    {
        super(context, name, cursorFactory, version, passphraseProvider);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        super.onCreate(sqLiteDatabase);
        try
        {
            TableUtils.createTables(new SQLiteDatabaseImpl(sqLiteDatabase), UploadRequest.class);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        super.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        // handle future db upgrades here
    }

    public void saveUploadRequest(UploadRequest uploadRequest)
    {
        LogExt.d(this.getClass(), "saveUploadRequest() id: " + uploadRequest.id);

        try
        {
            this.getDao(UploadRequest.class).createOrUpdate(uploadRequest);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public UploadRequest loadNextUploadRequest()
    {
        try
        {
            // don't get spam the same request, ignore requests tried less than a minute ago
            // also grab random one so we don't get stuck on one failing request for some reason
            long aMinAgo = System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS;
            Dao<UploadRequest> dao = this.getDao(UploadRequest.class);
            List<UploadRequest> requests = dao.query("uploadDate <= ?", new String[] {
                    String.valueOf(aMinAgo)
            }).orderBy("RANDOM()").limit(1).list();

            if(requests.isEmpty())
            {
                return null;
            }
            else
            {
                UploadRequest request = requests.get(0);
                // set attempted upload time so explained above
                request.uploadDate = System.currentTimeMillis();
                dao.update(request);

                return request;
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void deleteUploadRequest(UploadRequest request)
    {

        LogExt.d(this.getClass(), "deleteUploadRequest() id: " + request.id);

        try
        {
            this.getDao(UploadRequest.class).delete(request);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
