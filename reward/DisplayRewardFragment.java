package takamk2.local.wish2.reward;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import takamk2.local.wish2.R;
import takamk2.local.wish2.util.BitmapCache;
import takamk2.local.wish2.util.BitmapUtil;
import takamk2.local.wish2.util.DisableTouchListener;

public class DisplayRewardFragment extends Fragment {

    public static final int STATE_INITIAL = 0x0001;
    public static final int STATE_INITIALIZING = 0x0002;
    public static final int STATE_ACTIVE = 0x0004;
    public static final int STATE_ERROR = 0x0008;

    private static final String LOGTAG = DisplayRewardFragment.class.getSimpleName();

    private static final String KEY_ID = "key_id";
    private static final int REQUEST_SELECT_PHOTO = 0x00001;
    private static final int REQUEST_CROP = 0x00002;

    private long mId;
    private int mState = STATE_INITIAL;

    private Button mBtTmp; // Todo: DEBUG
    private ImageView mIvImage;
    private TextView mTvTitle;
    private TextView mTvPrice;
    private TextView mTvAnotherPrice;
    private TextView mTvPriority;
    private TextView mTvDescription;
    private TextView mTvUrl;
    private FrameLayout mFlLoading;

    private Reward mReward;

    // Todo: Will it move BaseFragment?
    private BitmapCache mBitmapCache = new BitmapCache();
    private File mFile;

    public static DisplayRewardFragment newInstance(long id) {
        DisplayRewardFragment fragment = new DisplayRewardFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public DisplayRewardFragment() {
        // NOP
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mId = args.getLong(KEY_ID, 0);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        new LoadDataTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {

                    // URIからbitmapを取得(uri)
                    Bitmap bitmap = BitmapUtil.getResizedBitmap(getActivity(), data.getData());
                    mIvImage.setImageBitmap(bitmap);

                    // bitmapのサイズをトリミングする(bitmap)
                    // トリミングした画像を保存(fileName)
                    // トリミングした画像を表示(ImageView)
                    // Fileに保存した画像を取得(fileName)

//                    try {
//                        Bitmap bitmap =
//                                MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
//
//                        Date mDate = new Date();
//                        SimpleDateFormat fileNameDate = new SimpleDateFormat("yyyyMMddHHmmss");
//                        String fileName = fileNameDate.format(mDate) + ".png";
//
//                        FileOutputStream outputStream;
//                        outputStream = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//
////                        FileOutputStream out = null;
////                        try {
////                            // openFileOutputはContextのメソッドなのでActivity内ならばthisでOK
////                            out = this.openFileOutput("image.png", Context.MODE_PRIVATE);
////                            image.compress(Bitmap.CompressFormat.PNG, 100, out);
////                        } catch (FileNotFoundException e) {
////                            // エラー処理
////                        } finally {
////                            if (out != null) {
////                                out.close();
////                                out = null;
////                            }
////                        }
//
//                        InputStream input = null;
//                        try {
//                            input = getActivity().openFileInput(fileName);
//                        } catch (FileNotFoundException e) {
//                            // エラー処理
//                        }
//                        Bitmap image = BitmapFactory.decodeStream(input);
//                        mIvImage.setImageBitmap(image);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
                break;

            case REQUEST_CROP:
                if (resultCode == Activity.RESULT_OK) {
                } else {
                }
                break;

            default:
                break;
        }
    }

    private void initializeViews(View parent) {
        mBtTmp = (Button) parent.findViewById(R.id.bt_tmp);
        mBtTmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_SELECT_PHOTO);
            }
        });

        mIvImage = (ImageView) parent.findViewById(R.id.iv_image);
        mTvTitle = (TextView) parent.findViewById(R.id.tv_title);
        mTvPrice = (TextView) parent.findViewById(R.id.tv_price);
        mTvAnotherPrice = (TextView) parent.findViewById(R.id.tv_another_price);
        mTvPriority = (TextView) parent.findViewById(R.id.tv_priority);
        mTvDescription = (TextView) parent.findViewById(R.id.tv_description);
        mTvUrl = (TextView) parent.findViewById(R.id.tv_url);
        mFlLoading = (FrameLayout) parent.findViewById(R.id.fl_loading);

        mFlLoading.setOnTouchListener(new DisableTouchListener());

        updateViews();
    }

    private void updateViews() {
        if (mState != STATE_ACTIVE) {
            mFlLoading.setVisibility(View.VISIBLE);
            return;
        }
        mFlLoading.setVisibility(View.GONE);

        if (mReward != null) {
            mTvTitle.setText(mReward.getTitle());
            mTvPrice.setText(mReward.getPrice() + " yen");
            mTvAnotherPrice.setText("Another " + mReward.calcAnotherPrice(12000L) + " yen");
            mTvPriority.setText(mReward.getPriority().getName());
            mTvDescription.setText(mReward.getDescription());
            mTvUrl.setText(mReward.getUrl());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display_reward, container, false);
    }

    class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mState = STATE_INITIALIZING;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Todo: Be loading the data from DB
//            try {
//                Thread.sleep(5000L); // DEBUG
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            mReward = new Reward();
            mReward.setTitle("PSVR");
            mReward.setPrice(44980L);
            mReward.setPriority(Reward.Priority.HIGH);
            mReward.setDescription("This is game!");
            mReward.setUrl("http://www.jp.playstation.com/psvr/");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mState = STATE_ACTIVE;
            updateViews();
        }
    }
}
