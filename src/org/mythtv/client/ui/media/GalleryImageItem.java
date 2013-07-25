package org.mythtv.client.ui.media;

/**
 * @author Espen A. Fossen
 */
public class GalleryImageItem {

    private int imageId;
    private String title;
    private String url;
    private boolean externalImage = false;

    public GalleryImageItem(int imageId, String title, String url, boolean externalImage) {
        this.imageId = imageId;
        this.title = title;
        this.url = url;
        this.externalImage = externalImage;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isExternalImage() {
        return externalImage;
    }

    public void setExternalImage(boolean externalImage) {
        this.externalImage = externalImage;
    }
}
