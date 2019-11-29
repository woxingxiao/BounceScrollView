# BounceScrollView
[![](https://jitpack.io/v/woxingxiao/BounceScrollView.svg)](https://jitpack.io/#woxingxiao/BounceScrollView)  [![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14) [![License](http://img.shields.io/badge/License-Apache%202.0-brightgreen.svg?style=flat)](https://opensource.org/licenses/Apache-2.0)

![logo](https://github.com/woxingxiao/BounceScrollView/blob/master/app/src/main/res/mipmap-xxhdpi/ic_launcher.png)

:art:**An Android warpped `ScrollView`, which works just like the `UIScrollView` in iOS when the user tries to scroll it beyond its content.**:art:

****
## Features
- :spades:Maximum adaptability. It supports the **horizontal** and the **vertical** direction,  and extends the `NestScrollView`. So it not only can work just like the `ScrollView` and the `HorizontalScrollView`, but also can work properly with  *Nest Scrolling*.
- :hearts:Customizable **damping** coefficient (easier or harder to over scroll). The **incremental damping** or constant damping can be chose during the over-scrolling.
- :clubs:Customizable **bounce delay**, the duration of the restoring animation.
- :diamonds:Customizable **interpolator**, the rate of change of the restoring animation.
***
<p>
<img src="https://github.com/woxingxiao/BounceScrollView/blob/master/screenshot/demo1.gif" width="280"/> 
<img src="https://github.com/woxingxiao/BounceScrollView/blob/master/screenshot/demo2.gif" width="280"/> <img src="https://github.com/woxingxiao/BounceScrollView/blob/master/screenshot/demo3.gif" width="280"/> <img src="https://github.com/woxingxiao/BounceScrollView/blob/master/screenshot/demo4.gif" width="280"/> <img src="https://github.com/woxingxiao/BounceScrollView/blob/master/screenshot/demo5.gif" width="280"/>
</p>

## Download
root project:`build.gradle`
```groovy
  allprojects {
      repositories {
          ...
          maven { url "https://jitpack.io" }
      }
  }
```
app:`build.gradle`

Support:
```groovy
  dependencies {
     implementation 'com.github.woxingxiao:BounceScrollView:LATEST_VERSION'
  }
```
AndroidX:
```groovy
  dependencies {
     implementation 'com.github.woxingxiao:BounceScrollView:LATEST_VERSION-androidx'
  }
```

## Usage
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.xw.repo.widget.BounceScrollView
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.constraint.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

          <!-- The ConstraintLayout can be replaced by FrameLayout, LinearLayout, etc. -->
          <!-- Your content view goes here. -->

     </android.support.constraint.ConstraintLayout>
</com.xw.repo.widget.BounceScrollView>
```
Listener to the scrolling event of content of the `BounceScrollView`:
```java
mBounceScrollView.setOnScrollListener(new OnScrollListener() {
    @Override
    public void onScrolling(int scrollX, int scrollY) {

    }
});
```
Listener to the over-scrolling event of the `BounceScrollView`:
```java
mBounceScrollView.setOnOverScrollListener(new OnOverScrollListener() {
    @Override
    public void onOverScrolling(boolean fromStart, int overScrolledDistance) {

    }
});
```
## Attributes
Attr|Format|Descrption
----|----|----
damping|float|The damping coefficient. The greater the value, the hard it is to over scroll. Defalut: `2.0`
scrollOrientation|enum|The children layout direction. Default: `vertical`
incrementalDamping|boolean|Whether the damping coefficient growing with the distance or not. Default: `true`
bounceDelay|long|The duration of restoring animation. Default: `400(ms)`
triggerOverScrollThreshold|int|The length in pixel to trigger over-scrolling. Default: `20(px)`
disableBounce|boolean|Disable the bounce effect. Default: `false`
nestedScrollingEnabled|boolean|Whether nested scrolling is enabled. Default: `true`
***
>It took me tons of time to adjust the parameters pixel by pixel, in order to achieve the best experience I think.:beers:
***
:cake:If the attr *damping* is set to 2.0, the over-scrolling effort is almost the same as `UIScrollView` in iOS.  
:cake:The  interpolator of the restoring animation is customizable. You can do it by:

```java
mBounceScrollView.setBounceInterpolator(new YourCustomizedInterpolator());
```
Thanks to the repo [EasingInterpolator](https://github.com/MasayukiSuda/EasingInterpolator), there are multiple choices to make.
***
Most of the resources in the sample come from [gameofthrones.com](http://www.gameofthrones.com).

## License
```
   Copyright 2018 woxingxiao

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
