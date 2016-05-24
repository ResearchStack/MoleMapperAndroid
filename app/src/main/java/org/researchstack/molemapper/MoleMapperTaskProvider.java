package org.researchstack.molemapper;
import android.content.Context;

import org.researchstack.backbone.task.Task;
import org.researchstack.molemapper.task.MoleMapperInitialTask;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.task.ConsentTask;
import org.researchstack.skin.task.SignInTask;
import org.researchstack.skin.task.SignUpTask;

import java.util.HashMap;

public class MoleMapperTaskProvider extends TaskProvider
{
    private HashMap<String, Task> map = new HashMap<>();

    public MoleMapperTaskProvider(Context context)
    {
        put(TASK_ID_INITIAL, MoleMapperInitialTask.create(context, TASK_ID_INITIAL));
        put(TASK_ID_CONSENT, ConsentTask.create(context, TASK_ID_CONSENT));
        put(TASK_ID_SIGN_IN, new SignInTask(context));
        put(TASK_ID_SIGN_UP, new SignUpTask(context));
    }

    @Override
    public Task get(String taskId)
    {
        return map.get(taskId);
    }

    @Override
    public void put(String id, Task task)
    {
        map.put(id, task);
    }

}
