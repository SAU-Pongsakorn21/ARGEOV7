package sau.comsci.com.argeov7.adapter;


public class EndangeredItem {
    private String mName;
    private int mThumbnail;


    public EndangeredItem()
    {}

    public EndangeredItem(String name, int mThumbnail)
    {
        this.mName = name;
        this.mThumbnail = mThumbnail;
    }
    public String getnName() {
        return mName;
    }

    public void setnName(String mName) {
        this.mName = mName;
    }

    public int getmThumbnail() {
        return mThumbnail;
    }

    public void setmThumbnail(int mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

}
