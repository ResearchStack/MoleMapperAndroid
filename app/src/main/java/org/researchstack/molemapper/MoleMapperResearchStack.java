package org.researchstack.molemapper;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.database.sqlite.UpdatablePassphraseProvider;
import org.researchstack.backbone.storage.file.EncryptionProvider;
import org.researchstack.backbone.storage.file.FileAccess;
import org.researchstack.backbone.storage.file.PinCodeConfig;
import org.researchstack.backbone.storage.file.SimpleFileAccess;
import org.researchstack.backbone.storage.file.aes.AesProvider;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.PermissionRequestManager;
import org.researchstack.skin.ResearchStack;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.UiManager;
import org.researchstack.skin.notification.NotificationConfig;
import org.researchstack.skin.notification.SimpleNotificationConfig;

public class MoleMapperResearchStack extends ResearchStack
{

    @Override
    protected AppDatabase createAppDatabaseImplementation(Context context)
    {
        SQLiteDatabase.loadLibs(context);
        return new Database(context, new UpdatablePassphraseProvider());
    }

    @Override
    protected FileAccess createFileAccessImplementation(Context context)
    {
        return new SimpleFileAccess();
    }

    @Override
    protected PinCodeConfig getPinCodeConfig(Context context)
    {
        long autoLockTime = AppPrefs.getInstance(context).getAutoLockTime();
        return new PinCodeConfig(autoLockTime);
    }

    @Override
    protected EncryptionProvider getEncryptionProvider(Context context)
    {
        return new AesProvider();
    }

    @Override
    protected ResourceManager createResourceManagerImplementation(Context context)
    {
        return new MoleMapperResourceManager();
    }

    @Override
    protected UiManager createUiManagerImplementation(Context context)
    {
        return new MoleMapperUiManager();
    }

    @Override
    protected DataProvider createDataProviderImplementation(Context context)
    {
        return new MoleMapperDataProvider();
    }

    @Override
    protected TaskProvider createTaskProviderImplementation(Context context)
    {
        return new MoleMapperTaskProvider(context);
    }

    @Override
    protected NotificationConfig createNotificationConfigImplementation(Context context)
    {
        return new SimpleNotificationConfig();
    }

    @Override
    protected PermissionRequestManager createPermissionRequestManagerImplementation(Context context)
    {
        return new MoleMappePermissionRequestManager();
    }

}
