package com.qzc.mobiledlsearch.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.qzc.mobiledlsearch.CameraClassifier;
import com.qzc.mobiledlsearch.ModelClasses;
import com.qzc.mobiledlsearch.PytorchActivity;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.TensorFlowImageCameraClassifier;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_OK;
import static com.qzc.mobiledlsearch.PytorchActivity.fetchModelFile;

public class TestDetailCameraPytorchFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;
    private ImageView btn_back;

    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView image;
    private Button buttonLoadImage;
    private Button detectButton;

    public static TestDetailCameraPytorchFragment createFor(String text) {
        TestDetailCameraPytorchFragment fragment = new TestDetailCameraPytorchFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_detail_camera_pytorch, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = view.findViewById(R.id.fragment_text);
        fragmentText.setText(text);

        // show backwards
        btn_back = view.findViewById(R.id.btn_back);
        // back function
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        image = (ImageView) view.findViewById(R.id.image);


        buttonLoadImage = (Button) view.findViewById(R.id.button);
        detectButton = (Button) view.findViewById(R.id.detect);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                TextView textView = view.findViewById(R.id.result_text);
                textView.setText("");
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);


            }
        });

        detectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Bitmap bitmap = null;
                Module module = null;

                //Getting the image from the image view
                ImageView imageView = (ImageView) view.findViewById(R.id.image);

                try {
                    //Read the image as Bitmap
                    bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                    //Here we reshape the image into 400*400
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                    //Loading the model file.
                    module = Module.load(fetchModelFile(TestDetailCameraPytorchFragment.this.getActivity(), "resnet18_traced.pt"));
                } catch (IOException e) {
                    TestDetailCameraPytorchFragment.this.getActivity().finish();
                }

                //Input Tensor
                final Tensor input = TensorImageUtils.bitmapToFloat32Tensor(
                        bitmap,
                        TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                        TensorImageUtils.TORCHVISION_NORM_STD_RGB
                );

                //Calling the forward of the model to run our input
                final Tensor output = module.forward(IValue.from(input)).toTensor();


                final float[] score_arr = output.getDataAsFloatArray();

                // Fetch the index of the value with maximum score
                float max_score = -Float.MAX_VALUE;
                int ms_ix = -1;
                for (int i = 0; i < score_arr.length; i++) {
                    if (score_arr[i] > max_score) {
                        max_score = score_arr[i];
                        ms_ix = i;
                    }
                }

                //Fetching the name from the list based on the index
                String detected_class = ModelClasses.MODEL_CLASSES[ms_ix];

                //Writing the detected class in to the text view of the layout
                TextView textView = view.findViewById(R.id.result_text);
                textView.setText(detected_class);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //This functions return the selected image from gallery
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = TestDetailCameraPytorchFragment.this.getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            image.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            //Setting the URI so we can read the Bitmap from the image
            image.setImageURI(null);
            image.setImageURI(selectedImage);

        }
    }

    public static String fetchModelFile(Context context, String modelName) throws IOException {
        File file = new File(context.getFilesDir(), modelName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(modelName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }


}
