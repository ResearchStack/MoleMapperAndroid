# Introduction

MoleMapper Android is an open source port of the [Mole Mapper](https://github.com/Sage-Bionetworks/MoleMapper) ResearchKit(TM) melanoma research study to ResearchStack.

This implementation of MoleMapper on Android was created to serve as an open source example of best practices for creating a production-ready ResearchStack application. Initial funding was generous provided by a grant from the Robert Wood Johnson Foundation.

Oregon Health & Science University will be posting their public fork of Mole Mapper for Android in the near future, which will more closely follow the Mole Mapper Android app (being released soon by OHSU on Google Play). We will prove links to that project and the app on Google Play as they become available.

# About MoleMapper

MoleMapper is a personalized tool to help you map, measure, and monitor the moles on your skin. Using a familiar Maps-like interface, you can measure the size of a mole using the camera and a common reference object like a coin.

If you are 18 or older, MoleMapper also gives you the option to participate in a research study developed in partnership with researchers at Oregon Health & Science University and Sage Bionetworks, a 501 (c)(3) nonprofit research organization to better understand skin biology and melanoma risks.

The MoleMapper research study was created in partnership with researchers at Oregon Health & Science University and Sage Bionetworks. Unless otherwise noted, it should be assumed that all images, logos, and trademarks are copyrighted and should be used only with permission of the original copyright owners.

### 3<sup>rd</sup>-party library disclosures

<b>
com.android.support:appcompat-v7<br />
com.android.support:cardview-v7<br />
com.android.support:design
</b>

- Used for theming and styling views within the app. Libraries also  provide backward-compatible versions of Android framework APIs (e.g. vector icon support)

<b>com.android.support:support-v13:23.2.0</b>

- Support is a set of code libraries that provide backward-compatible versions of Android framework APIs. We use support for FragmentCompat and its use in receiving the results for permission requests.

<b>com.android.support:multidex</b>

- The Android MultiDex support library enables us to go past the default 65K method limit for an android project.

<b>com.davemorrissey.labs:subsampling-scale-image-view</b>

- Used within the MoleMeasurementActivity, allows user to zoom and pan an image with subsampling for low impact on memory

<b>co.touchlab.squeaky:squeaky-processor:0.4.0.0</b>

-  Annotation processor for the Squeaky ORMLite database library. The library creates auto-generated code at compile time for our database pojos (see Mole or Measurement classes)

<b>
pl.charmas.android:android-reactive-location<br />
com.google.android.gms:play-services-location<br />
</b>
- play-services-location allows the app to get the user's location while being conscious of battery life. Android-reactive-location library wraps the location services API’s in Rx Observables.

<b>com.crashlytics.sdk.android:crashlytics</b>

- Crash / Logging library being used during the QA period. This library will be removed at v1.0 release.
