/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.agrial.loginapplication.sticksheetdetection.ui;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.agrial.loginapplication.MyApplication;
import com.agrial.loginapplication.sticksheetdetection.objectdetection.DetectedObject;
import com.google.android.material.chip.Chip;
import com.google.common.base.Objects;
import com.agrial.loginapplication.sticksheetdetection.PreferenceUtils;
import com.agrial.loginapplication.R;
import com.agrial.loginapplication.sticksheetdetection.Utils;
import com.agrial.loginapplication.sticksheetdetection.camera.CameraSource;
import com.agrial.loginapplication.sticksheetdetection.camera.CameraSourcePreview;
import com.agrial.loginapplication.sticksheetdetection.camera.GraphicOverlay;
import com.agrial.loginapplication.sticksheetdetection.camera.WorkflowModel;
import com.agrial.loginapplication.sticksheetdetection.camera.WorkflowModel.WorkflowState;
import com.agrial.loginapplication.sticksheetdetection.objectdetection.ProminentObjectProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/** Demonstrates the object detection and visual search workflow using camera preview. */
public class LiveObjectDetectionActivity extends AppCompatActivity implements OnClickListener, GuidelinesDialog.GuidelinesDialogListener, ImageConfirmationDialog.ImageConfirmationDialogListener, Camera.PictureCallback {

  private static final String TAG = "LiveObjectActivity";
  public static final String FILE_PATH = "image_uri";

  private CameraSource cameraSource;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private View helpButton;
  private View flashButton;
  private Chip promptChip;
  private AnimatorSet promptChipAnimator;
  private ProgressBar searchProgressBar;
  private WorkflowModel workflowModel;
  private WorkflowState currentWorkflowState;
  private GuidelinesDialog guidelinesDialog;
  private DetectedObject detectedObject;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_live_object);
    preview = findViewById(R.id.camera_preview);
    graphicOverlay = findViewById(R.id.camera_preview_graphic_overlay);
    graphicOverlay.setOnClickListener(this);
    cameraSource = new CameraSource(graphicOverlay);

    promptChip = findViewById(R.id.bottom_prompt_chip);
    promptChipAnimator =
        (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter);
    promptChipAnimator.setTarget(promptChip);

    searchProgressBar = findViewById(R.id.search_progress_bar);

    findViewById(R.id.close_button).setOnClickListener(this);
    flashButton = findViewById(R.id.flash_button);
    flashButton.setOnClickListener(this);
    helpButton = findViewById(R.id.help_button);
    helpButton.setOnClickListener(this);
    guidelinesDialog = new GuidelinesDialog(this,this);
    // show guidelines dialog only one time when app starts
    if (MyApplication.isFirstTime){
      guidelinesDialog.show();
      MyApplication.isFirstTime = false;
    }

    setUpWorkflowModel();
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (!Utils.allPermissionsGranted(this)) {
      Utils.requestRuntimePermissions(this);
    }

    workflowModel.markCameraFrozen();
    helpButton.setEnabled(true);
    currentWorkflowState = WorkflowState.NOT_STARTED;
    cameraSource.setFrameProcessor(new ProminentObjectProcessor(graphicOverlay, workflowModel));
    workflowModel.setWorkflowState(WorkflowState.DETECTING);
  }

  @Override
  protected void onPause() {
    super.onPause();
    currentWorkflowState = WorkflowState.NOT_STARTED;
    stopCameraPreview();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
      cameraSource = null;
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();

    if (id == R.id.close_button) {
      onBackPressed();

    } else if (id == R.id.flash_button) {
      if (flashButton.isSelected()) {
        flashButton.setSelected(false);
        cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF);
      } else {
        flashButton.setSelected(true);
        cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
      }

    } else if (id == R.id.help_button) {
      // Sets as disabled to prevent the user from clicking on it too fast.
      helpButton.setEnabled(false);
      guidelinesDialog.show();
      stopCameraPreview();

    }
  }

  private void startCameraPreview() {
    if (!workflowModel.isCameraLive() && cameraSource != null) {
      try {
        workflowModel.markCameraLive();
        preview.start(cameraSource);
      } catch (IOException e) {
        Log.e(TAG, "Failed to start camera preview!", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  private void stopCameraPreview() {
    if (workflowModel.isCameraLive()) {
      workflowModel.markCameraFrozen();
      flashButton.setSelected(false);
      preview.stop();
    }
  }

  private void setUpWorkflowModel() {
    workflowModel = ViewModelProviders.of(this).get(WorkflowModel.class);

    // Observes the workflow state changes, if happens, update the overlay view indicators and
    // camera preview state.
    workflowModel.workflowState.observe(
        this,
        workflowState -> {
          if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
            return;
          }

          currentWorkflowState = workflowState;
          Log.d(TAG, "Current workflow state: " + currentWorkflowState.name());

          if (PreferenceUtils.isAutoSearchEnabled(this)) {
            stateChangeInAutoSearchMode(workflowState);
          }

        });

    // Observes changes on the object to search, if happens, fire product search request.
    workflowModel.detectedObject.observe(this,
            detectedObject -> {
              if (detectedObject != null){
                this.detectedObject = detectedObject;
              }
            });

    // Observes changes on the object that has search completed, if happens, show the bottom sheet
    // to present search result.
    workflowModel.searchedObject.observe(
        this,
        searchedObject -> {
          if (searchedObject != null) {

          }
        });
  }

  private void stateChangeInAutoSearchMode(WorkflowState workflowState) {
    boolean wasPromptChipGone = (promptChip.getVisibility() == View.GONE);

    searchProgressBar.setVisibility(View.GONE);
    switch (workflowState) {
      case DETECTING:
      case DETECTED:
      case CONFIRMING:
        promptChip.setVisibility(View.VISIBLE);
        promptChip.setText(
            workflowState == WorkflowState.CONFIRMING
                ? R.string.prompt_hold_camera_steady
                : R.string.prompt_point_at_an_object);
        startCameraPreview();
        break;
      case CONFIRMED:
        promptChip.setVisibility(View.GONE);
        stopCameraPreview();
        break;
      case SEARCHING:
        searchProgressBar.setVisibility(View.VISIBLE);
        promptChip.setVisibility(View.VISIBLE);
        promptChip.setText(R.string.prompt_searching);
        stopCameraPreview();
        break;
      case REQUIRE_ZOOM:
        promptChip.setVisibility(View.VISIBLE);
        promptChip.setText(R.string.prompt_move_camera_closer);
        startCameraPreview();
        break;
      case TAKE_PICTURE:
        cameraSource.takePicture(this::onPictureTaken);
        promptChip.setVisibility(View.GONE);
        break;
      default:
        promptChip.setVisibility(View.GONE);
        break;
    }

    boolean shouldPlayPromptChipEnteringAnimation =
        wasPromptChipGone && (promptChip.getVisibility() == View.VISIBLE);
    if (shouldPlayPromptChipEnteringAnimation && !promptChipAnimator.isRunning()) {
      promptChipAnimator.start();
    }
  }

  @Override
  public void onGotItButtonClick() {
    startCameraPreview();
    helpButton.setEnabled(true);
  }

  @Override
  public void onDoneBtnClick(Bitmap bitmap) {

    Log.d("Bitmap Size: ",String.valueOf(bitmap.getByteCount()));

    String filePath = getIntent().getStringExtra(FILE_PATH);
    SaveBitmapAsyncTask saveBitmapAsyncTask = new SaveBitmapAsyncTask();
    saveBitmapAsyncTask.execute(filePath,bitmap);

  }

  @Override
  public void onCancelBtnClick() {
    startCameraPreview();
  }

  @Override
  public void onPictureTaken(byte[] bytes, Camera camera) {

    stopCameraPreview();

    if (detectedObject != null){
      Bitmap fullImageBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

      if (fullImageBitmap.getWidth() > fullImageBitmap.getHeight()){
        // picture is rotated. Rotate it to correct the orientation
        fullImageBitmap = Utils.rotateBimapFromFirebaseVisionMetaData(fullImageBitmap,detectedObject.getRotation());
      }

      float scaleX = (float) fullImageBitmap.getWidth()/ (float) detectedObject.getPreviewBitmap().getWidth();
      float scaleY = (float) fullImageBitmap.getHeight()/ (float) detectedObject.getPreviewBitmap().getHeight();

      Rect rect = detectedObject.getBoundingBox();

      Bitmap detectedBitmap = Bitmap.createBitmap(
              fullImageBitmap,
              Math.round(rect.left * scaleX),
              Math.round(rect.top * scaleY),
              Math.round(rect.width() * scaleX),
              Math.round(rect.height() * scaleY)
      );

      new ImageConfirmationDialog(this,this,detectedBitmap).show();
    }
  }

  private class SaveBitmapAsyncTask extends AsyncTask<Object,Void,Void>{

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      searchProgressBar.setVisibility(View.VISIBLE);
      promptChip.setVisibility(View.VISIBLE);
      promptChip.setText(R.string.saving_image);
    }

    @Override
    protected Void doInBackground(Object... objects) {
      String filePath = (String) objects[0];
      Bitmap bitmap = (Bitmap) objects[1];

      if (filePath != null){

        File file = new File(filePath);
        try {
          FileOutputStream fileOutputStream = new FileOutputStream(file);
          bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);

          fileOutputStream.close();
          bitmap.recycle();

          Log.d("File Size: ",String.valueOf(file.length()));

        } catch (IOException e) {
          e.printStackTrace();
          cancel(true);
        }

      }
      else {
        cancel(true);
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);

      searchProgressBar.setVisibility(View.GONE);
      promptChip.setVisibility(View.GONE);

      setResult(RESULT_OK);
      finish();

    }

    @Override
    protected void onCancelled() {
      super.onCancelled();
      searchProgressBar.setVisibility(View.GONE);
      promptChip.setVisibility(View.GONE);
      startCameraPreview();
    }
  }

}
