# Architecture decisions

## User recognition and placement

A PoseNet TensorFlow Lite model is used to detect the user.
All the detection code is based on the 
[official TensorFlow Lite PoseNet example](https://github.com/tensorflow/examples/tree/master/lite/examples/posenet/android).  

Whilst the model returns the key point coordinates of a person relative to the captured picture it's 
more suitable for our use case to center the person. 
[`Stickman.from()`](../app/src/main/java/com/github/markushi/posenet/domain/Stickman.kt) takes care 
of exactly that. The center hip is defined as the reference point and the x location is transformed to `0.5f` within the fixed range of `[0..1f]`.