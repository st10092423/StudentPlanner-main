package com.studentplanner.studentplanner.models;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;

import org.apache.commons.text.WordUtils;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public final class ImageHandler {

    private ImageHandler() {
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        try (var outputStream = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            Log.d("ERROR", Objects.requireNonNull(e.getMessage()));

        }
        return null;

    }

    public static Bitmap decodeBitmapByteArray(byte[] imgByte) {
        return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }

    public static void showImage(final byte[] image, ImageView imageView) {
        if (image == null) {
            imageView.setVisibility(View.GONE);
            return;
        }
        imageView.setImageBitmap(decodeBitmapByteArray(image));
        imageView.setVisibility(View.VISIBLE);
    }

    public static void openImageGallery(ActivityResultLauncher<Intent> imageActivityResultLauncher) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType( "image/*");
        intent = Intent.createChooser(intent, WordUtils.capitalizeFully("Select Image"));
        imageActivityResultLauncher.launch(intent);

    }

}
