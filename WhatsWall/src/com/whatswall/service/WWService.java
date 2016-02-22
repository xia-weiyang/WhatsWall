package com.whatswall.service;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;

import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.whatswall.base.C;
import com.whatswall.entity.Favorite;
import com.whatswall.tools.DisposeFile;
import com.whatswall.tools.DisposeImg;
import com.whatswall.tools.Download;
import com.whatswall.tools.Show;
import com.whatswall.ui.RoomPublishActivity;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;

public class WWService extends Service {

	// 上传用户头像
	public final int UPLOAD_USERIMG = 0;

	private final String TAG = "WWService";

	@Override
	public IBinder onBind(Intent intent) {

		return new GetToBinder();
	}

	@Override
	public void onCreate() {

		super.onCreate();

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {

		return super.onUnbind(intent);
	}

	/**
	 * 获取图片(先从本地，本地没有则从网络获取)
	 * 
	 * @param avFile
	 * @param path
	 * @param filename
	 * @param downloadCallBack
	 *            回调 不能为空!
	 */
	public void getBitmap(final boolean isWhole, final String avfile,
			final String path, final String filename,
			final DownloadCallBack downloadCallBack, final int reqWidth) {

		
		Bitmap bitmap = null;
		File file = new File(path + filename);
		if (file.exists()) {

			bitmap = DisposeImg.decodeSampledBitmapFromFile(path + filename, reqWidth);
			if (isWhole)
				if (bitmap.getWidth() <= C.BITMAP_SIZE) {
					Show.logInfo(
							getApplication(),
							TAG,
							"width=" + bitmap.getWidth() + " height="
									+ bitmap.getHeight() + " BitmapMaxSize="
									+ C.BITMAP_SIZE);
					bitmap = null;
				}
		}
		if (bitmap == null) {

			AVQuery<AVObject> avQuery = new AVQuery<>("_File");
			avQuery.whereEqualTo("objectId", avfile);
			avQuery.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> arg0, AVException e) {

					if (null == e) {
						
							AVFile avFile = AVFile.withAVObject(arg0.get(0));
							String url = avFile.getThumbnailUrl(true,
									C.BITMAP_SIZE, C.BITMAP_SIZE, 90,
									C.BITMAP_FORMAT);
							if (isWhole)
								url = avFile.getUrl();
			
							downloadBitmap(url, path, filename, downloadCallBack,reqWidth);
							Show.logInfo(getApplication(), TAG, "From network !  "
									+ path + filename);
							Show.logInfo(getApplication(), TAG, "url= "+
									url);
						
					} else {
						downloadCallBack.failed(e);
					}
				}
			});

		} else {
			bitmap = RoomPublishActivity.degreeBitmap(path + filename, bitmap);
			downloadCallBack.done(bitmap);
			Show.logInfo(getApplication(), TAG, "From cache !  " + path
					+ filename);
		}
	}

	/**
	 * 下载图片到指定目录
	 * 
	 * @param url
	 * @param path
	 * @param filename
	 * @param downloadCallBack
	 *            回调，可以为null
	 */
	public void downloadBitmap(String url, String path, String filename,
			final DownloadCallBack downloadCallBack, final int reqWidth) {

		Download download = new Download(url);
		download.dowmSdInBackground(path, filename,
				download.new DoneCallBack() {

					@Override
					public void done(String path) {

						if (downloadCallBack != null) {

							Bitmap bitmap = DisposeImg.decodeSampledBitmapFromFile(path, reqWidth);
							bitmap = RoomPublishActivity.degreeBitmap(path, bitmap);
							downloadCallBack.done(bitmap);
						}

					}
				}, download.new ProgressCallback() {

					@Override
					public void done(int progress) {

						if (downloadCallBack != null) {
							downloadCallBack.progress(progress);
						}
					}
				}, download.new FailedCallback() {

					@Override
					public void failed(Exception e) {

						if (downloadCallBack != null) {
							downloadCallBack.failed(e);
						}
					}
				});

	}

	/**
	 * 保存用户头像到服务器
	 * 
	 * @param bitmap
	 * @param name
	 * @param saveCallBack
	 *            回调 此回调可传null
	 */
	public void saveUserImg(Bitmap bitmap, String name,
			final SaveCallBack saveCallBack) {
		String path = DisposeFile.PATH_SDCARD_IMG_USER;
		uploadBitmap(bitmap, name, path, UPLOAD_USERIMG, new UploadCallBack() {

			@Override
			public void progress(int progress) {

			}

			@Override
			public void failed(AVException e) {

			}

			@Override
			public void done(AVFile avFile) {

				AVUser currentUser = AVUser.getCurrentUser();
				if (currentUser != null) {
					currentUser.put(C.USERIMG, avFile);
					currentUser.saveInBackground(new SaveCallback() {

						@Override
						public void done(AVException e) {

							if (null == e) {
								if (saveCallBack != null)
									saveCallBack.done();
							} else {
								Show.disposeError(getApplication(), TAG, e);
							}
						}
					});
				} else {
					// currentUser为null Error!
				}
			}
		});
	}

	/**
	 * 上传Bitmap
	 * 
	 * @param bitmap
	 * @param name
	 *            存在服务器端的图片名字
	 * @param path
	 *            路径，保存到本地的路径
	 * @param type
	 *            类型，上传图片的类型 UPLOAD_USERIMG
	 * @param uploadCallBack
	 *            回调 此回调不能为null
	 */
	public void uploadBitmap(final Bitmap bitmap, final String name,
			final String path, final int type,
			final UploadCallBack uploadCallBack) {

		byte[] bytes = DisposeImg.getPngBitmapBytes(bitmap);
		final AVFile avFile = new AVFile(name, bytes);
		avFile.addMetaData("width", bitmap.getWidth());
		avFile.addMetaData("height", bitmap.getHeight());
		avFile.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {

				if (e == null) {
					uploadCallBack.done(avFile);
					try {
						DisposeFile.saveJpgFile(bitmap, avFile.getObjectId()
								+ C.BITMAP_FORMAT, path);
					} catch (IOException e1) {

						Show.showToast(getApplication(), "保存图片到本地失败!");
						Show.disposeError(getApplication(), TAG, e1);
					}
				} else {
					uploadCallBack.failed(e);
					switch (type) {
					case UPLOAD_USERIMG:
						Show.showToast(getApplication(), "上传头像失败!");
						break;

					default:
						Show.showToast(getApplication(), "上传图片失败!");
						break;
					}
					Show.disposeError(getApplication(), TAG, e);
				}
			}
		}, new ProgressCallback() {

			@Override
			public void done(Integer progress) {

				uploadCallBack.progress(progress);
			}
		});
	}

	public void test(final int i) {

		AVQuery<AVObject> query = new AVQuery<>(C.ClASS_CONTENT);
		query.whereEqualTo(C.COMMENT_CONTENTID, i);

		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(final List<AVObject> content, AVException arg1) {

				if (arg1 == null) {
					if (content.size() == 1) {
						if (content.get(0).getInt(C.CONTENT_CONTENTTYPE) == C.CONTENT_TYPE_ONLYIMG
								|| content.get(0).getInt(C.CONTENT_CONTENTTYPE) == C.CONTENT_TYPE_TEXTANDIMG) {
							
							AVRelation<AVObject> relation = content.get(0).getRelation(C.CONTENT_IMGRELATION);
							AVQuery<AVObject> query = relation.getQuery();
							query.findInBackground(new FindCallback<AVObject>() {
								
								@Override
								public void done(List<AVObject> arg0, AVException arg1) {
									
									if(arg1==null){
										if(arg0.size() == 1){
											System.out.println("id="+i+"已找到图片");
											AVFile avFile = AVFile.withAVObject(arg0.get(0));
											int width = (int) avFile.getMetaData("width");
											int height = (int) avFile.getMetaData("height");
											JSONArray array = new JSONArray();
											array.add(width);
											array.add(height);
											content.get(0).put(C.CONTENT_IMGWIDTHHEIGHT, array);
											content.get(0).saveInBackground();
										}
									}
								}
							});
						}
					}
				}
			}
		});
	}

	public abstract class SaveCallBack {
		public abstract void done();
	}

	public abstract class UploadCallBack {
		public abstract void done(AVFile avFile);

		public abstract void failed(AVException e);

		public abstract void progress(int progress);
	}

	public class GetToBinder extends Binder {

		public WWService getGetToService() {
			return WWService.this;
		}
	}

	public abstract class DownloadCallBack {
		public abstract void done(Bitmap bitmap);

		public abstract void failed(Exception e);

		public abstract void progress(int progress);
	}

	/**
	 * 提交举报到服务器
	 * 
	 * @param type
	 * @param contentObjectId
	 */
	public void saveReportContent(final String type, String contentObjectId , final String note) {

		AVQuery<AVObject> query = new AVQuery<>(C.ClASS_CONTENT);
		query.getInBackground(contentObjectId, new GetCallback<AVObject>() {

			@Override
			public void done(AVObject content, AVException e) {

				if (e == null) {
					AVObject report = new AVObject(C.CLASS_REPORT);
					report.put(C.REPORT_TYPE, type);
					report.put(C.REPORT_CONTENT, content);
					report.put(C.REPORT_NOTE, note);
					AVUser currentUser = AVUser.getCurrentUser();
					if (currentUser != null)
						report.put(C.REPORT_USER, currentUser);
					report.saveInBackground();
					Show.showToast(getApplication(), "您的举报已提交!");
				}
			}
		});
	}

	/**
	 * 提交收藏备注信息到服务器
	 * 
	 * @param favorite
	 * @param note
	 */
	public void saveFavoriteNote(Favorite favorite, final String note) {

		AVUser currentUser = AVUser.getCurrentUser();
		if (currentUser == null)
			return;
		AVQuery<AVObject> query = new AVQuery<>(C.CLASS_FAVORITE);
		query.whereEqualTo(C.FAVROITE_USER, currentUser);
		query.whereEqualTo(C.FAVORITE_ROOMNUMBER, favorite.getNumber());
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					if (arg0.size() == 1) {

						arg0.get(0).put(C.FAVROITE_NOTE, note);
						arg0.get(0).saveInBackground();
					}
				}
			}
		});
	}

	/**
	 * 提交反馈信息到服务器
	 * 
	 * @param info
	 */
	public void saveFeedBackInfo(String info) {

		AVObject avObject = new AVObject(C.CLASS_FEEDBACK);
		AVUser currentUser = AVUser.getCurrentUser();
		if (currentUser != null) {
			avObject.put(C.FEEDBACK_USER, currentUser);
		}
		avObject.put(C.FEEDBACK_CONTENT, info);
		avObject.saveInBackground();
		Show.showToast(getApplication(), "您的建议已发送!");
	}
}
