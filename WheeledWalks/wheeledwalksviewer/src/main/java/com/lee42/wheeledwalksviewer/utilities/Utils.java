package com.lee42.wheeledwalksviewer.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.lee42.wheeledwalksviewer.models.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by David on 21/10/2015.
 * container class for common utility methods
 */
public class Utils {


    /**
     * Shows a toast if given a valid context and message
     *
     * @param context -
     * @param message -
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


//    /**
//     * returns a bitmap if given a valid context and file path, otherwise returns the
//     * default image from a resource
//     *
//     * @param context       -
//     * @param imageFilePath -
//     * @return bitmap from filepath or default from resource
//     */
//    public static Bitmap getBitmap(Context context, String imageFilePath) {
//        if (!imageFilePath.equals(Constants.EMPTY_STRING) && !imageFilePath.equals(Constants.DEFAULT_IMAGE_PATH)) {
//            File image = new File(imageFilePath);
//            if (image.exists()) {
//                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
//                bitmap = Bitmap.createScaledBitmap(bitmap, 180, 100, true);
//                return bitmap;
//            } else {
//                return BitmapFactory.decodeResource(context.getResources(), R.drawable.defaultcardimage);
//            }
//        } else {
//            return BitmapFactory.decodeResource(context.getResources(), R.drawable.defaultcardimage);
//        }
//    }

//    /**
//     * returns a thumbnail sized bitmap of the image at the given filepath
//     *
//     * @param imagePath -
//     * @return resized bitmap
//     */
//    public static Bitmap getResizedBitmap(String imagePath) {
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(imagePath, options);
//
//        int scale = 1;
//        while (options.outWidth / scale / 2 >= Constants.WALK_THUMBNAIL_REQUIRED_SIZE
//                && options.outHeight / scale / 2 >= Constants.WALK_THUMBNAIL_REQUIRED_SIZE) {
//            scale *= 2;
//        }
//        options.inSampleSize = scale;
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeFile(imagePath, options);
//    }


//    /**
//     * Save the given bitmap to the storage dirctory
//     *
//     * @param bitmapImage   -
//     * @param imageFileName -
//     * @return the absolute path to the saved image
//     */
//    public static String saveToInternalStorage(Bitmap bitmapImage, String imageFileName) {
//
//        File root = new File(Environment.getExternalStorageDirectory(),
//                Constants.IMAGE_FILE_DIRECTORY);
//        if (!root.exists()) {
//            if (root.mkdirs()) {
//                Log.w(Constants.DEBUG_TAG, "Image root did not exist. Created");
//            } else {
//                Log.e(Constants.DEBUG_TAG, "Image root did not exist. Creation failed!");
//            }
//        }
//
//        File imageFile = new File(root, imageFileName);
//
//        FileOutputStream imageFileOutputStream = null;
//        try {
//
//            imageFileOutputStream = new FileOutputStream(imageFile);
//
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, imageFileOutputStream);
//            imageFileOutputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return imageFile.getAbsolutePath();
//    }

    /**
     * sleeps the calling thread
     *
     * @param timeInMillis - time to sleep for
     */
    public static void sleepThread(int timeInMillis) {
        try {
            Thread.sleep(timeInMillis, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * deletes all files in the current directory and all subdirectories recursively
     *
     * @param fileOrDirectory - the root directory to empty out
     */
    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }


    /**
     * returns a formatted string file name for a walk log file or image
     *
     * @param name      - walk name
     * @param locality  - walk locality
     * @param extension - file extension
     * @return = the formatted file name
     */
    public static String getFileNameString(String name, String locality, String extension) {
        name = name.trim().replace(" ", "_");    //remove all leading and trailing spaces,
        // and replace all others with an underscore
        locality = locality.trim().replace(" ", "_");
        return (name + "_" + locality + extension);
    }

    public static String getFormattedDistanceString(Float distance) {
        String returnString;
        if (distance >= 2000) { //return in kilometers
            returnString = String.format("%.1fKM", distance / 1000);
        } else {                //return in meters (truncated)
            returnString = String.format("%.0fm", distance);
        }
        return returnString;
    }

    public static String getAccelAngleString(Float accelerometerValue) {

        //return (accelerometerValue.toString());
        return String.format("%.1f%c", Math.toDegrees(Math.acos((10 - accelerometerValue) / 10)),
                Constants.DEGREES_SYMBOL);
    }

    public static String getGradeString(int grade) {
        if (grade >= 0 && grade < 5) {
            return Constants.GRADE_STRINGS[grade];
        }
        return "invalid grade";
    }


    public static Float getMedian(ArrayList<Float> floatArrayList) {
        Collections.sort(floatArrayList);
        Float median;
        if (floatArrayList.size() % 2 == 0) {
            median = (floatArrayList.get(floatArrayList.size() / 2) + floatArrayList.get(floatArrayList.size() / 2 - 1)) / 2;
        } else {
            median = floatArrayList.get(floatArrayList.size() / 2);
        }
        return median;
    }

}
