package org.researchstack.molemapper;
import android.text.TextUtils;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.model.ConsentSectionModel;
import org.researchstack.skin.model.SectionModel;
import org.researchstack.skin.model.StudyOverviewModel;
import org.researchstack.skin.model.TaskModel;

public class MoleMapperResourceManager extends ResourceManager
{
    private static final String BASE_PATH_HTML  = "html";
    private static final String BASE_PATH_JSON  = "json";
    private static final String BASE_PATH_PDF   = "pdf";
    private static final String BASE_PATH_VIDEO = "mp4";

    public static final int PEM = 4;

    @Override
    public Resource getStudyOverview()
    {
        return new Resource(Resource.TYPE_JSON,
                BASE_PATH_JSON,
                "study_overview",
                StudyOverviewModel.class);
    }

    @Override
    public Resource getConsentHtml()
    {
        return new Resource(Resource.TYPE_HTML, BASE_PATH_HTML, "app_consent_html");
    }

    @Override
    public Resource getConsentPDF()
    {
        return new Resource(Resource.TYPE_PDF, BASE_PATH_HTML, "app_consent_pdf");
    }

    @Override
    public Resource getConsentSections()
    {
        return new Resource(Resource.TYPE_JSON,
                BASE_PATH_JSON,
                "consent_section",
                ConsentSectionModel.class);
    }

    @Override
    public Resource getLearnSections()
    {
        return new Resource(Resource.TYPE_JSON, BASE_PATH_JSON, "learn", SectionModel.class);
    }

    @Override
    public Resource getPrivacyPolicy()
    {
        return new Resource(Resource.TYPE_HTML, BASE_PATH_HTML, "app_privacy_policy");
    }

    @Override
    public Resource getSoftwareNotices()
    {
        return new Resource(Resource.TYPE_HTML, BASE_PATH_HTML, "software_notices");
    }

    @Override
    public Resource getTasksAndSchedules()
    {
        return null;
    }

    @Override
    public Resource getTask(String taskFileName)
    {
        return new Resource(Resource.TYPE_JSON, BASE_PATH_JSON, taskFileName, TaskModel.class);
    }

    @Override
    public String generatePath(int type, String name)
    {
        String dir;
        switch(type)
        {
            default:
                dir = null;
                break;
            case Resource.TYPE_HTML:
                dir = BASE_PATH_HTML;
                break;
            case Resource.TYPE_JSON:
                dir = BASE_PATH_JSON;
                break;
            case Resource.TYPE_PDF:
                dir = BASE_PATH_PDF;
                break;
        }

        StringBuilder path = new StringBuilder();
        if(! TextUtils.isEmpty(dir))
        {
            path.append(dir).append("/");
        }

        return path.append(name).append(".").append(getFileExtension(type)).toString();
    }

    @Override
    public String getFileExtension(int type)
    {
        switch(type)
        {
            case PEM:
                return "pem";
            default:
                return super.getFileExtension(type);
        }
    }

    public static class PemResource extends ResourcePathManager.Resource
    {
        public PemResource(String name)
        {
            super(MoleMapperResourceManager.PEM, null, name);
        }
    }
}