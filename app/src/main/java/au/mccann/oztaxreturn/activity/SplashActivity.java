package au.mccann.oztaxreturn.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.RealmDbHelper;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.BlockResponse;
import au.mccann.oztaxreturn.model.UpdateResponse;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/12/18.
 */

public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void resumeData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUpdate();
            }
        }, 200);
    }

    private void checkUpdate() {
        LogUtils.d(TAG, "checkUpdate data Token : " + UserManager.getUserToken());
        ApiClient.getApiService().checkUpdate(UserManager.getUserToken(), getString(R.string.device_type), Utils.getCurrentVersion(this)).enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                LogUtils.d(TAG, "checkUpdate data code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "checkUpdate data body : " + response.body());
                    UpdateResponse updateResponse = response.body();
                    if (updateResponse != null && updateResponse.isForceUpdate()) {
                        DialogUtils.showOkDialog(SplashActivity.this, getString(R.string.update_title), getString(R.string.content_update), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {
                                updateVerSion();
                            }
                        });
                    } else {
                        if (updateResponse.isRecommendUpdate()) {
                            showUpdateDialog();
                        } else {
                            try {
                                if (UserManager.checkLogin())
                                    checkBlockUser();
                                else {
                                    startActivity(MainActivity.class, TransitionScreen.FADE_IN);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Realm.deleteRealm(RealmDbHelper.getRealmConfig(SplashActivity.this));
                                checkUpdate();
                            }

                        }
                    }

                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "checkUpdate error : " + error.message());
                        DialogUtils.showOkDialog(SplashActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable throwable) {
                LogUtils.d(TAG, "checkUpdate error : " + throwable.getMessage());
                DialogUtils.showRetryDialog(SplashActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        checkUpdate();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });

    }

    private void updateVerSion() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                    ("market://details?id=" + getPackageName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUpdateDialog() {
        DialogUtils.showReCommendUpdateDialog(SplashActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
            @Override
            public void onSubmit() {
                updateVerSion();
            }

            @Override
            public void onCancel() {
                if (UserManager.checkLogin())
                    checkBlockUser();
                else {
                    startActivity(LoginActivity.class, TransitionScreen.FADE_IN);
                    finish();
                }
            }
        });

    }

    private void checkBlockUser() {
        LogUtils.d(TAG, "checkBlockUser , Token : " + UserManager.getUserToken());
        ApiClient.getApiService().checkBlockUser(UserManager.getUserToken()).enqueue(new Callback<BlockResponse>() {
            @Override
            public void onResponse(Call<BlockResponse> call, Response<BlockResponse> response) {
                LogUtils.d(TAG, "checkBlockUser , code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "checkBlockUser , body : " + response.body());
                    BlockResponse blockResponse = response.body();
                    if (blockResponse.getStatus().equalsIgnoreCase(Constants.STATUS_USER_DEACTIVATE)) {
                        DialogUtils.showOkDialog(SplashActivity.this, getString(R.string.block_title), getString(R.string.temporally_locked), getString(R.string.block_logout), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {
                                logOut();
                            }
                        });
                    } else if (blockResponse.getStatus().equalsIgnoreCase(Constants.STATUS_USER_BLOCK)) {
                        DialogUtils.showOkDialog(SplashActivity.this, getString(R.string.block_title), getString(R.string.block_user), getString(R.string.block_logout), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {
                                logOut();
                            }
                        });
                    } else {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);

                        if (getIntent().hasExtra(Constants.NOTIFICATION_EXTRA)) {
                            intent.putExtra(Constants.NOTIFICATION_EXTRA, getIntent().getSerializableExtra(Constants.NOTIFICATION_EXTRA));
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent, TransitionScreen.FADE_IN);
                        finish();
                    }
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "checkBlockUser error : " + error.message());
                        DialogUtils.showOkDialog(SplashActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<BlockResponse> call, Throwable t) {
                DialogUtils.showRetryDialog(SplashActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        checkBlockUser();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void logOut() {
        LogUtils.d(TAG, "logOut , Token : " + UserManager.getUserToken());
        ApiClient.getApiService().logOut(UserManager.getUserToken()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                LogUtils.d(TAG, "logOut , code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_NO_CONTENT) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.deleteAll();
                    realm.commitTransaction();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null && error.message() != null) {
                        LogUtils.d(TAG, "logOut error : " + error.message());
                        DialogUtils.showOkDialog(SplashActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {
                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                DialogUtils.showRetryDialog(SplashActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        logOut();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

}
