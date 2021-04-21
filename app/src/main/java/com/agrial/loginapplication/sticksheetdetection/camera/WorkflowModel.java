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

package com.agrial.loginapplication.sticksheetdetection.camera;

import android.app.Application;
import android.content.Context;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.agrial.loginapplication.sticksheetdetection.PreferenceUtils;
import com.agrial.loginapplication.sticksheetdetection.objectdetection.DetectedObject;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/** View model for handling application workflow based on camera preview. */
public class WorkflowModel extends AndroidViewModel {

  /**
   * State set of the application workflow.
   */
  public enum WorkflowState {
    NOT_STARTED,
    DETECTING,
    DETECTED,
    CONFIRMING,
    CONFIRMED,
    REQUIRE_ZOOM,
    SEARCHING,
    SEARCHED,
    TAKE_PICTURE
  }

  public final MutableLiveData<WorkflowState> workflowState = new MutableLiveData<>();
  public final MutableLiveData<DetectedObject> detectedObject = new MutableLiveData<>();
  public final MutableLiveData<Object> searchedObject = new MutableLiveData<>();

  public final MutableLiveData<FirebaseVisionBarcode> detectedBarcode = new MutableLiveData<>();

  private final Set<Integer> objectIdsToSearch = new HashSet<>();

  private boolean isCameraLive = false;
  @Nullable private DetectedObject confirmedObject;

  public WorkflowModel(Application application) {
    super(application);
  }

  @MainThread
  public void setWorkflowState(WorkflowState workflowState) {
    if (!workflowState.equals(WorkflowState.CONFIRMED)
        && !workflowState.equals(WorkflowState.SEARCHING)
        && !workflowState.equals(WorkflowState.SEARCHED)) {
      confirmedObject = null;
    }
    this.workflowState.setValue(workflowState);
  }

  @MainThread
  public void confirmingObject(DetectedObject object, float progress) {
    boolean isConfirmed = (Float.compare(progress, 1f) == 0);
    if (isConfirmed) {
      confirmedObject = object;
//      if (PreferenceUtils.isAutoSearchEnabled(getContext())) {
//        setWorkflowState(WorkflowState.CONFIRMED);
//        triggerConfirmationDialog(object);
//      } else {
//        setWorkflowState(WorkflowState.CONFIRMED);
//      }
      this.detectedObject.setValue(object);
      setWorkflowState(WorkflowState.TAKE_PICTURE);
    } else {
      setWorkflowState(WorkflowState.CONFIRMING);
    }
  }

  private void triggerConfirmationDialog(DetectedObject detectedObject){
    this.detectedObject.setValue(detectedObject);
  }

  private void triggerSearch(DetectedObject object) {
    Integer objectId = checkNotNull(object.getObjectId());
    if (objectIdsToSearch.contains(objectId)) {
      // Already in searching.
      return;
    }

    objectIdsToSearch.add(objectId);
    detectedObject.setValue(object);
  }

  public void markCameraLive() {
    isCameraLive = true;
    objectIdsToSearch.clear();
  }

  public void markCameraFrozen() {
    isCameraLive = false;
  }

  public boolean isCameraLive() {
    return isCameraLive;
  }

//  @Override
//  public void onSearchCompleted(DetectedObject object, List<Product> products) {
//    if (!object.equals(confirmedObject)) {
//      // Drops the search result from the object that has lost focus.
//      return;
//    }
//
//    objectIdsToSearch.remove(object.getObjectId());
//    setWorkflowState(WorkflowState.SEARCHED);
//    searchedObject.setValue(
//        new SearchedObject(getContext().getResources(), confirmedObject, products));
//  }

  private Context getContext() {
    return getApplication().getApplicationContext();
  }
}
