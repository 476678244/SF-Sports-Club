package teamdivider.integration;

import java.io.File;

import teamdivider.util.ContextUtil;
import teamdivider.util.PropertyUtil;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

public class QiniuIntegrationManager {

  public static final String accessKey = "Hwzi1P_wX9X8fiwFnBz5UyyXUB8ZYNa17V_DlFMf";

  public static final String secretKey = "2E-t-bWLR4ftG1Az-J88VJ8P_KpKRwBWSgVZgTvL";

  public static final String avatarBucket = PropertyUtil.AVATAR_BUCKET_NAME;

  private Auth auth = Auth.create(accessKey, secretKey);

  private UploadManager uploadManager = new UploadManager();
  
  private BucketManager bucketManager = new BucketManager(auth);

  private String getUpToken() {
    return auth.uploadToken(avatarBucket);
  }

  public void deleteFile(String fileName) {
    if (ContextUtil.getContext().skipQiniuActions)
      return;
    try {
      bucketManager.delete(avatarBucket, fileName);
    } catch (QiniuException e) {
      Response r = e.response;
    }
  }

  public void uploadFile(File file, String fileName) {
    try {
      Response res = uploadManager.put(file, fileName, getUpToken());
    } catch (QiniuException e) {
      Response r = e.response;
    }
  }
  
  public String uploadFileToQiniu(File file, String newFileName,
      String existingFileName) {
    if (ContextUtil.getContext().skipQiniuActions)
      return "";
    try {
      bucketManager.delete(avatarBucket, existingFileName);
    } catch (QiniuException e) {
      Response r = e.response;
    }
    try {
      Response res = uploadManager.put(file, newFileName, getUpToken());
      return res.address;
    } catch (QiniuException e) {
      Response r = e.response;
    }
    return "";
  }
}
