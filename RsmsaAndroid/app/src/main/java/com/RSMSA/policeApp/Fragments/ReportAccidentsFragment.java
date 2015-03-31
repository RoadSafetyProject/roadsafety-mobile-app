package com.RSMSA.policeApp.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.RSMSA.policeApp.AccidentReportFormActivity;
import com.RSMSA.policeApp.MainOffence;
import com.RSMSA.policeApp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ilakoze on 2/11/2015.
 */
public class ReportAccidentsFragment extends Fragment {
    private static final String TAG="ReportAccidentsFragment";
    private RelativeLayout previewLayout,cameraOptionsLayout, contentView;
    private RelativeLayout btnCapturePicture,btnCaptureVideo;
    private Uri fileUri;  //file url to store image/video

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "AccidentFile";
    private ImageView imgPreview;
    private VideoView videoPreview;
    private Button reportButton;
    private boolean isImage;
    private String imagePath,videoPath;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView=(RelativeLayout)inflater.inflate(R.layout.fragment_accident_reporter, container, false);
        btnCapturePicture = (RelativeLayout) contentView.findViewById(R.id.btnCapturePictureLayout);
        btnCaptureVideo = (RelativeLayout) contentView.findViewById(R.id.btnCaptureVideoLayout);
        imgPreview = (ImageView) contentView.findViewById(R.id.imgPreview);
        videoPreview = (VideoView) contentView.findViewById(R.id.videoPreview);
        previewLayout = (RelativeLayout) contentView.findViewById(R.id.preview);
        cameraOptionsLayout = (RelativeLayout) contentView.findViewById(R.id.cameraOptions);
        //mediaTitle=(TextView)contentView.findViewById(R.id.mediaTitle);
        //mediaTitle.setTypeface(MainOffence.Roboto_Bold);
        reportButton = (Button)contentView.findViewById(R.id.report);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), AccidentReportFormActivity.class);
                if(fileUri!=null) {
                    intent.putExtra("videoPath", videoPath);
                    intent.putExtra("imagePath", imagePath);
                    intent.putExtra("isImage", isImage);
                }
                getActivity().startActivity(intent);
            }
        });

        btnCapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
                //cameraOptionsLayout.setVisibility(View.GONE);
                //mediaTitle.setVisibility(View.VISIBLE);
            }
        });

        btnCaptureVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
                //cameraOptionsLayout.setVisibility(View.GONE);
                //mediaTitle.setVisibility(View.VISIBLE);
            }
        });
        return contentView;
    }

    /**
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Recording video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // successfully captured the image
                // display it in image view
                setFullImageFromFilePath(fileUri.getPath(),imgPreview);
                imagePath=fileUri.getPath();
                isImage=true;
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getActivity().getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
                cameraOptionsLayout.setVisibility(View.VISIBLE);
                //mediaTitle.setVisibility(View.GONE);
            } else {
                // failed to capture image
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                previewVideo();
                isImage=false;
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getActivity().getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

                cameraOptionsLayout.setVisibility(View.VISIBLE);
                //mediaTitle.setVisibility(View.GONE);
            } else {
                // failed to record video
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        //videoPreview.setVisibility(View.GONE);

        imgPreview.setVisibility(View.VISIBLE);
        // Get the dimensions of the View
        int targetW = 720;
        int targetH = 500;//previewLayout.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        //TODO change this
        //imageView.setImageBitmap(bitmap);

        ImageView imageDone=(ImageView)contentView.findViewById(R.id.image_done);
        imageDone.setVisibility(View.VISIBLE);

    }

    /**
     * Previewing recorded video
     */
    private void previewVideo() {
        try {
            // hide image preview
            //imgPreview.setVisibility(View.GONE);

            videoPath=fileUri.getPath();

            //TODO change this
            //videoPreview.setVisibility(View.VISIBLE);

            //videoPreview.setVideoPath(fileUri.getPath());
            // start playing
            videoPreview.start();
            ImageView videoDone=(ImageView)contentView.findViewById(R.id.video_done);
            videoDone.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;

        float scaleHeight = ((float) newHeight) / height;

// CREATE A MATRIX FOR THE MANIPULATION

        Matrix matrix = new Matrix();

// RESIZE THE BIT MAP

        matrix.postScale(scaleWidth, scaleHeight);

// RECREATE THE NEW BITMAP

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        imgPreview.setImageBitmap(resizedBitmap);
        return resizedBitmap;

    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            fileUri = savedInstanceState.getParcelable("file_uri");
        }catch (NullPointerException e){}
    }


}
