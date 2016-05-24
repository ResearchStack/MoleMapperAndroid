package org.researchstack.molemapper.ui.fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.NotificationHelper;
import org.researchstack.backbone.storage.database.TaskNotification;
import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.molemapper.BodyMapActivity;
import org.researchstack.molemapper.Database;
import org.researchstack.molemapper.MoleMapperDataProvider;
import org.researchstack.molemapper.MoleMeasurementActivity;
import org.researchstack.molemapper.PhotoCaptureActivity;
import org.researchstack.molemapper.R;
import org.researchstack.molemapper.models.Measurement;
import org.researchstack.molemapper.models.Mole;
import org.researchstack.molemapper.models.MoleDetail;
import org.researchstack.molemapper.task.FollowupTask;
import org.researchstack.molemapper.ui.EmptyItemDecoration;
import org.researchstack.molemapper.ui.MoleEditDialog;
import org.researchstack.molemapper.ui.MoleGrowthDialog;
import org.researchstack.molemapper.ui.view.MoleDetailView;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.notification.TaskAlertReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MoleMapperFragment extends Fragment implements StorageAccessListener
{
    private static final int REQUEST_CODE_FOLLOWUP = 1;

    private MoleRecyclerAdapter adapter;
    private View                emptyView;
    private View                emptyInstructionView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_mole_mapper, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Database appDatabase = (Database) StorageAccess.getInstance().getAppDatabase();

        adapter = new MoleRecyclerAdapter(getActivity(), null);
        adapter.getMolePublishSubject()
                .asObservable()
                .map(appDatabase:: loadMole)
                .compose(ObservableUtils.applyDefault())
                .subscribe(this :: showDetails);
        adapter.getSurveyPublishSubject().asObservable().subscribe(o -> {
            startFollowUpSurvey();
        });

        emptyView = view.findViewById(R.id.empty);
        emptyInstructionView = view.findViewById(R.id.empty_instruction);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mole_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new EmptyItemDecoration(LinearLayoutManager.VERTICAL, 1));

        RxView.clicks(view.findViewById(R.id.body_fab)).subscribe(v -> {
            openBodyMap();
        });
    }

    private void openBodyMap()
    {
        startActivity(new Intent(getContext(), BodyMapActivity.class));
    }

    @Override
    public void onDataReady()
    {
        loadMoleData();
    }

    @Override
    public void onDataFailed()
    {
        // Ignore
    }

    @Override
    public void onDataAuth()
    {
        // Ignore
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE_FOLLOWUP && resultCode == Activity.RESULT_OK)
        {
            final TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

            Observable.create(subscriber -> {
                StorageAccess.getInstance().getAppDatabase().saveTaskResult(taskResult);
                ((MoleMapperDataProvider) DataProvider.getInstance()).uploadFollowup(getContext(),
                        taskResult);
                subscriber.onNext(null);
            }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
                loadMoleData();
            });

            Boolean moleRemovedResult = ((StepResult<Boolean>) taskResult.getStepResult(FollowupTask.FOLLOWUP_FORM)
                    .getResultForIdentifier(FollowupTask.MOLE_REMOVED)).getResult();

            if(moleRemovedResult != null && moleRemovedResult)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity(),
                        R.style.Theme_MoleMapper_Dialog).setTitle(R.string.update_removed_dialog_title)
                        .setMessage(R.string.update_removed_dialog_body)
                        .setPositiveButton(R.string.go_to_body_map,
                                (dialog, which) -> openBodyMap())
                        .setNegativeButton(R.string.later, (dialog, which) -> dialog.dismiss())
                        .show();

                Button button = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                button.setTextColor(ContextCompat.getColor(getActivity(),
                        R.color.text_color_mole_inactive));
            }

            // Create Task Notification if notifications are enabled
            if(AppPrefs.getInstance(getActivity()).isTaskReminderEnabled())
            {
                Observable.create(subscriber -> {
                    // Save TaskNotification to DB, cron-time is first of the month @ 7:00 PM
                    TaskNotification notification = new TaskNotification();
                    notification.endDate = taskResult.getEndDate();
                    notification.chronTime = "0 19 1 * *";
                    NotificationHelper.getInstance(getActivity())
                            .saveTaskNotification(notification);
                    subscriber.onNext(notification);
                })
                        .compose(ObservableUtils.applyDefault())
                        .map(o -> (TaskNotification) o)
                        .subscribe(notification -> {
                            // Add notification to Alarm Manager
                            Intent intent = new Intent(TaskAlertReceiver.ALERT_CREATE);
                            intent.putExtra(TaskAlertReceiver.KEY_NOTIFICATION, notification);
                            getActivity().sendBroadcast(intent);
                        });
            }
        }
        else if(requestCode == PhotoCaptureActivity.REQUEST_CODE_MOLE &&
                resultCode == Activity.RESULT_OK)
        {
            int moleId = data.getIntExtra(PhotoCaptureActivity.KEY_ID, 0);

            Intent intent = MoleMeasurementActivity.newIntent(getActivity(),
                    moleId,
                    data.getStringExtra(PhotoCaptureActivity.KEY_PATH));
            startActivityForResult(intent, MoleMeasurementActivity.REQUEST_CODE);
        }
        else if(requestCode == MoleMeasurementActivity.REQUEST_CODE)
        {
            String tempMeasurementPhoto = data.getStringExtra(MoleMeasurementActivity.KEY_RESULT_TEMP_PHOTO);

            if(resultCode == Activity.RESULT_OK)
            {
                Measurement measurement = (Measurement) data.getSerializableExtra(
                        MoleMeasurementActivity.KEY_RESULT_MEASUREMENT);

                Observable.fromCallable(() -> {
                    // Create the measurement in the database
                    ((Database) StorageAccess.getInstance().getAppDatabase()).saveMeasurement(
                            measurement);

                    // Rename temp image to official image
                    String imagePath = "/mole_measurements/measurement_" + measurement.id;
                    StorageAccess.getInstance()
                            .getFileAccess()
                            .moveData(getActivity(), tempMeasurementPhoto, imagePath);

                    // Save the measurement again with the new photo url
                    measurement.measurementPhoto = imagePath;
                    ((Database) StorageAccess.getInstance().getAppDatabase()).saveMeasurement(
                            measurement);

                    return measurement;
                }).compose(ObservableUtils.applyDefault()).subscribe(modified -> {
                    // Update zone
                    loadMoleData();

                    // Upload result
                    Observable.fromCallable(() -> {
                        ((MoleMapperDataProvider) DataProvider.getInstance()).uploadMeasurement(
                                getActivity(),
                                modified);
                        return null;
                    }).compose(ObservableUtils.applyDefault()).subscribe();
                });
            }
            else
            {
                // Remove the measurement photo since it wont be used
                Observable.fromCallable(() -> {
                    StorageAccess.getInstance()
                            .getFileAccess()
                            .clearData(getActivity(), tempMeasurementPhoto);
                    return Observable.empty();
                }).compose(ObservableUtils.applyDefault()).subscribe();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startFollowUpSurvey()
    {
        Task task = FollowupTask.create(getActivity());
        startActivityForResult(ViewTaskActivity.newIntent(getActivity(), task),
                REQUEST_CODE_FOLLOWUP);
    }

    private void showDetails(Mole mole)
    {
        MoleGrowthDialog.showGrowthDialog(getActivity(),
                mole,
                new MoleGrowthDialog.MoleGrowthCallbacks()
                {
                    @Override
                    public void onEditMole()
                    {
                        showEditDialog(mole);
                    }

                    @Override
                    public void onUpdateMole()
                    {
                        Intent intent = PhotoCaptureActivity.newIntent(getActivity(),
                                PhotoCaptureActivity.MOLE,
                                mole.id);
                        intent.putExtra(PhotoCaptureActivity.KEY_INSTRUCTION,
                                getString(R.string.photo_cap_inst_mole));
                        startActivityForResult(intent, PhotoCaptureActivity.REQUEST_CODE_MOLE);
                    }
                });
    }

    private void showEditDialog(Mole mole)
    {
        MoleEditDialog.newInstance(getContext(), mole, false, new MoleEditDialog.MoleEditCallbacks()
        {
            @Override
            public void onCancel()
            {
                showDetails(mole);
            }

            @Override
            public void onDeleteMole()
            {
                throw new RuntimeException("Should not be able to delete mole on mole list");
            }

            @Override
            public void onUpdateMoleName(String newName)
            {
                Observable.create(subscriber -> {
                    mole.moleName = newName;
                    ((Database) StorageAccess.getInstance().getAppDatabase()).saveMole(mole);
                    LogExt.i(MoleEditDialog.class, "New title saved: " + mole.moleName);

                    loadMoleData();
                }).compose(ObservableUtils.applyDefault()).subscribe();
            }
        }).show();
    }

    private void loadMoleData()
    {
        ((Database) StorageAccess.getInstance().getAppDatabase()).loadMolesObservable()
                .flatMap(Observable:: from)
                .map(mole -> new MoleDetail(mole))
                .toSortedList((mdl, mdr) -> {

                    // Use state ordinal for order
                    int order = mdl.state.compareTo(mdr.state);

                    if(order == 0)
                    {
                        // If both need update, compare date and show the oldest first
                        if(mdl.state == MoleDetail.State.NEEDS_UPDATE &&
                                mdr.state == MoleDetail.State.NEEDS_UPDATE)
                        {
                            return mdl.lastMeasuredDate.compareTo(mdr.lastMeasuredDate);
                        }

                        // If both items are up-to-date, compare the last-measure date and show
                        // the latest first
                        else if(mdl.state == MoleDetail.State.UP_TO_DATE &&
                                mdr.state == MoleDetail.State.UP_TO_DATE)
                        {
                            return - mdl.lastMeasuredDate.compareTo(mdr.lastMeasuredDate);
                        }
                    }

                    return order;
                })
                .compose(ObservableUtils.applyDefault())
                .subscribe(this :: setData, error -> {
                    emptyView.setVisibility(View.VISIBLE);
                });
    }

    private void setData(List<MoleDetail> sortedMoleItems)
    {
        Observable.defer(() -> {
            // Load last task result for follow survey
            return Observable.just(StorageAccess.getInstance()
                    .getAppDatabase()
                    .loadLatestTaskResult(FollowupTask.TASK_FOLLOW_UP));
        }).compose(ObservableUtils.applyDefault()).subscribe(result -> {

            // Create our list with first header
            List<Object> items = new ArrayList<>();

            // Check to see if the followup survey for this month has been completed. If it hasnt, add
            // to our items
            boolean addSurveyItem = true;

            if(result != null)
            {
                Calendar today = Calendar.getInstance();
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(result.getEndDate());
                if(today.get(Calendar.MONTH) == endDate.get(Calendar.MONTH) &&
                        today.get(Calendar.YEAR) == endDate.get(Calendar.YEAR))
                {
                    addSurveyItem = false;
                }
            }
            else if(AppPrefs.getInstance(getContext()).skippedOnboarding())
            {
                addSurveyItem = false;
            }

            boolean addTodoHeader = false;

            // Don't show monthly survey until they have at least one mole recorded
            if(addSurveyItem && sortedMoleItems.size() != 0)
            {
                addTodoHeader = true;
                items.add(new MoleSurvey());
            }

            // Iterate over our sortedMoleItems
            int itemsDoneHeaderIndex = - 1;

            for(int i = 0; i < sortedMoleItems.size(); i++)
            {
                MoleDetail item = sortedMoleItems.get(i);
                if(item.state == MoleDetail.State.UP_TO_DATE)
                {
                    if(itemsDoneHeaderIndex == - 1)
                    {
                        // Compensate for survey item if need be
                        itemsDoneHeaderIndex = i + (addSurveyItem ? 1 : 0);
                    }
                }
                else
                {
                    addTodoHeader = true;
                }
                items.add(item);
            }

            if(itemsDoneHeaderIndex > - 1)
            {
                items.add(itemsDoneHeaderIndex, new MoleHeader(getString(R.string.mole_done)));
            }

            if(addTodoHeader)
            {
                items.add(0, new MoleHeader(getString(R.string.mole_to_do)));
            }

            adapter.setItems(items);
            adapter.notifyDataSetChanged();

            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            emptyInstructionView.setVisibility(
                    adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }, error -> {
            emptyView.setVisibility(View.VISIBLE);
            emptyInstructionView.setVisibility(View.VISIBLE);
        });
    }

    private static class MoleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_ITEM   = 1;
        private static final int VIEW_TYPE_SURVEY = 2;

        private final LayoutInflater inflater;
        private       List<Object>   items;
        private PublishSubject<Integer> molePublishSubject   = PublishSubject.create();
        private PublishSubject<Object>  surveyPublishSubject = PublishSubject.create();


        public MoleRecyclerAdapter(Context context, List<Object> items)
        {
            super();
            this.inflater = LayoutInflater.from(context);
            this.items = items;

            if(this.items == null)
            {
                this.items = new ArrayList<>();
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            if(viewType == VIEW_TYPE_HEADER)
            {
                View view = inflater.inflate(R.layout.item_mole_header, parent, false);
                return new HeaderViewHolder(view);
            }
            else if(viewType == VIEW_TYPE_ITEM)
            {
                View view = inflater.inflate(R.layout.item_mole, parent, false);
                return new MoleViewHolder(view);
            }
            else if(viewType == VIEW_TYPE_SURVEY)
            {
                View view = inflater.inflate(R.layout.item_mole_survey, parent, false);
                return new SurveyViewHolder(view);
            }
            else
            {
                throw new RuntimeException("Item View type " + viewType + " not supported");
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position)
        {
            if(hldr instanceof MoleViewHolder)
            {
                MoleViewHolder holder = (MoleViewHolder) hldr;
                MoleDetail item = (MoleDetail) items.get(position);
                holder.moleDetailView.setModel(item, true);
                holder.itemView.setOnClickListener(v -> molePublishSubject.onNext(item.id));
            }
            else if(hldr instanceof HeaderViewHolder)
            {
                HeaderViewHolder holder = (HeaderViewHolder) hldr;
                MoleHeader header = (MoleHeader) items.get(position);
                holder.label.setText(header.label);
            }
            else if(hldr instanceof SurveyViewHolder)
            {
                hldr.itemView.setOnClickListener(v -> surveyPublishSubject.onNext(null));
            }
        }

        @Override
        public int getItemViewType(int position)
        {
            Object item = items.get(position);

            if(item instanceof MoleHeader)
            {
                return VIEW_TYPE_HEADER;
            }
            else if(item instanceof MoleDetail)
            {
                return VIEW_TYPE_ITEM;
            }
            else if(item instanceof MoleSurvey)
            {
                return VIEW_TYPE_SURVEY;
            }
            else
            {
                throw new RuntimeException("Item View type not supported");
            }
        }

        @Override
        public int getItemCount()
        {
            return items.size();
        }

        public void setItems(List<Object> items)
        {
            this.items = items;
        }

        public PublishSubject<Integer> getMolePublishSubject()
        {
            return molePublishSubject;
        }

        public PublishSubject<Object> getSurveyPublishSubject()
        {
            return surveyPublishSubject;
        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder
        {
            TextView label;

            public HeaderViewHolder(View itemView)
            {
                super(itemView);
                label = (TextView) itemView.findViewById(R.id.list_title);
            }
        }

        public static class SurveyViewHolder extends RecyclerView.ViewHolder
        {
            public SurveyViewHolder(View itemView)
            {
                super(itemView);
            }
        }

        public static class MoleViewHolder extends RecyclerView.ViewHolder
        {
            public MoleDetailView moleDetailView;

            public MoleViewHolder(View itemView)
            {
                super(itemView);
                moleDetailView = (MoleDetailView) itemView.findViewById(R.id.mole_detail);
            }
        }
    }

    public static class MoleHeader
    {
        public final String label;

        public MoleHeader(String label)
        {
            this.label = label;
        }
    }

    public static class MoleSurvey
    {
        public MoleSurvey()
        {
        }
    }
}
