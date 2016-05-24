package org.researchstack.molemapper.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.molemapper.MoleMapperDataProvider;
import org.researchstack.molemapper.R;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.model.SectionModel;
import org.researchstack.skin.model.TaskModel;
import org.researchstack.skin.task.SmartSurveyTask;

import java.util.ArrayList;
import java.util.List;

import rx.subjects.PublishSubject;

public class MoleLearnActivity extends PinCodeActivity
{
    private static final int REQUEST_FEEDBACK = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LearnAdapter adapter = new LearnAdapter(this, loadSections());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        adapter.getPublishSubject().subscribe(sectionRow -> onRowClicked(sectionRow));
    }

    private void onRowClicked(SectionModel.SectionRow sectionRow)
    {
        if(sectionRow.getDetails().equals("ohsu"))
        {
            String url = "http://www.ohsu.edu/xd/health/services/dermatology/war-on-melanoma/melanoma-community-registry.cfm";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
        else if(sectionRow.getDetails().equals("feedback"))
        {

            TaskModel model = ResourceManager.getInstance().getTask("app_feedback").create(this);
            SmartSurveyTask task = new SmartSurveyTask(this, model);
            Intent intent = ViewTaskActivity.newIntent(this, task);
            startActivityForResult(intent, REQUEST_FEEDBACK);
        }
        else
        {
            String path = ResourceManager.getInstance()
                    .generateAbsolutePath(ResourcePathManager.Resource.TYPE_HTML,
                            sectionRow.getDetails());
            Intent intent = ViewWebDocumentActivity.newIntentForPath(this,
                    sectionRow.getTitle(),
                    path);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_FEEDBACK && resultCode == RESULT_OK)
        {
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            ((MoleMapperDataProvider) DataProvider.getInstance()).uploadFeedback(this, result);
            Toast.makeText(MoleLearnActivity.this, R.string.thanks_feedback, Toast.LENGTH_SHORT)
                    .show();
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private SectionModel loadSections()
    {
        return ResourceManager.getInstance().getLearnSections().create(this);
    }

    public static class LearnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_ITEM   = 1;

        private PublishSubject<SectionModel.SectionRow> publishSubject = PublishSubject.create();
        private List<Object>   items;
        private LayoutInflater inflater;

        public LearnAdapter(Context context, SectionModel sections)
        {
            super();
            items = new ArrayList<>();
            for(SectionModel.Section section : sections.getSections())
            {
                items.add(section.getTitle());
                items.addAll(section.getItems());

            }
            this.inflater = LayoutInflater.from(context);
        }

        public PublishSubject<SectionModel.SectionRow> getPublishSubject()
        {
            return publishSubject;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            if(viewType == VIEW_TYPE_HEADER)
            {
                View view = inflater.inflate(R.layout.preference_category_material, parent, false);
                return new HeaderViewHolder(view);
            }
            else
            {
                View view = inflater.inflate(R.layout.item_learn, parent, false);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position)
        {
            if(hldr instanceof ViewHolder)
            {
                ViewHolder holder = (ViewHolder) hldr;
                Context context = holder.itemView.getContext();

                //Offset for header
                SectionModel.SectionRow item = (SectionModel.SectionRow) items.get(position);

                holder.title.setText(item.getTitle());

                holder.itemView.setOnClickListener(v -> {
                    publishSubject.onNext(item);
                });
            }
            else
            {
                HeaderViewHolder holder = (HeaderViewHolder) hldr;
                String title = (String) items.get(position);
                holder.title.setText(title);
            }
        }

        @Override
        public int getItemViewType(int position)
        {
            Object item = items.get(position);
            return item instanceof String ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount()
        {
            // Size of items + header
            return items.size();
        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder
        {

            TextView title;

            public HeaderViewHolder(View itemView)
            {
                super(itemView);
                title = ((TextView) itemView);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder
        {
            AppCompatTextView title;

            public ViewHolder(View itemView)
            {
                super(itemView);
                title = (AppCompatTextView) itemView.findViewById(R.id.learn_item_title);
            }
        }
    }
}
