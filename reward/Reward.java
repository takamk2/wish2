package takamk2.local.wish2.reward;

import android.graphics.Bitmap;

import takamk2.local.wish2.R;

/**
 * Created by takamk2 on 17/01/19.
 * <p>
 * The Edit Fragment of Base Class.
 */

public class Reward {

    private String mTitle = null;
    private Long mPrice = null;
    private Priority mPriority = Priority.NONE;
    private String mDescription = null;
    private String mUrl = null;
    private Bitmap mImage = null;

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setPrice(Long price) {
        mPrice = price;
    }

    public Long getPrice() {
        return mPrice;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public Long calcAnotherPrice(long deposit) {
        return (mPrice != null) ? mPrice - deposit : deposit;
    }

    public enum Priority {
        NONE("None", R.color.priority_none),
        LOW("Low", R.color.priority_low),
        MIDDLE("Middle", R.color.priority_middle),
        HIGH("High", R.color.priority_high),
        ;

        private final String mName;
        private final int mColorId;

        Priority(String name, int colorId) {
            mName = name;
            mColorId = colorId;
        }

        public String getName() {
            return mName;
        }

        public int getColorId() {
            return mColorId;
        }
    }
}
