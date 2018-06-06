package au.mccann.oztaxreturn.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.networking.ApiClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/24/2017.
 */

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();
    public final static String OUTPUT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MAXIMYZ";
    public static File futureStudioIconFile;
    private static FileUtils fileUtils;

    public static FileUtils getInstance() {
        if (fileUtils == null) {
            fileUtils = new FileUtils();
        }
        init();
        return fileUtils;
    }

    private static void init() {
        File folder = new File(OUTPUT_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    //delete all temp file after used
    public static void deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }

        }
        path.delete();
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public static String getMaximyzDirectory() {
        return OUTPUT_DIR + File.separator;
    }

    public static void downloadFile(final Activity context, final String url, final String fileName) {
        ProgressDialogUtils.showProgressDialog(context);
        ApiClient.getApiService().downloadFileUrlAsync(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    LogUtils.d(TAG, "downloadFile code" + response.code());
                    LogUtils.d(TAG, "downloadFile body" + response.code());
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            final boolean writtenToDisk = writeResponseBodyToDisk(response.body(), url, fileName);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (writtenToDisk) {
                                        ProgressDialogUtils.dismissProgressDialog();
                                        String path = futureStudioIconFile.getPath();
                                        DialogUtils.showOkDialog(context, context.getString(R.string.download_successfully), path, context.getString(R.string.view_download), new AlertDialogOk.AlertDialogListener() {
                                            @Override
                                            public void onSubmit() {
                                                Uri path = Uri.fromFile(futureStudioIconFile);
                                                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                                                pdfIntent.setDataAndType(path, FileUtils.getMimeType(url));
                                                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                try {
                                                    context.startActivity(pdfIntent);
                                                } catch (ActivityNotFoundException e) {
                                                    Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                                        ProgressDialogUtils.dismissProgressDialog();
                                    }
                                }
                            });
                            return null;
                        }
                    }.execute();

                } else {
                    ProgressDialogUtils.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                LogUtils.e(TAG, "doSaveReview onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(context, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        downloadFile(context, url, fileName);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    public static boolean writeResponseBodyToDisk(ResponseBody body, String url, String fileName) {
        init();
        String extension = url.substring(url.lastIndexOf("."));
        if (extension.length() > 5) return false;
        try {
            // todo change the file location/name according to your needs
            futureStudioIconFile = new File(getMaximyzDirectory() + System.currentTimeMillis() + fileName + extension);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[1024 * 1024];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
