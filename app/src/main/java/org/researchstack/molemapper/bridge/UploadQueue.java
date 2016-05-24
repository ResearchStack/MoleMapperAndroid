package org.researchstack.molemapper.bridge;

/**
 * Created by bradleymcdermott on 3/8/16.
 */
public interface UploadQueue
{
    UploadRequest loadNextUploadRequest();

    void saveUploadRequest(UploadRequest request);

    void deleteUploadRequest(UploadRequest request);
}
