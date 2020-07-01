# FabCircleRevealLayout 
<!-- [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FABRevealLayout-green.svg?style=flat)](https://android-arsenal.com/details/1/2459) -->

A layout to transition between two views using a Floating Action Button as shown in many Material Design concepts

### Usage

![Sample 1](demo.gif)

`FabCircleRevealLayout` is very simple to use. You only need to include a `FloatingActionButton` from the Android Design Support Library and two views (namely main and secondary) within the layout. `FabCircleRevealLayout` will position your views accordingly and provide the transition between them automatically.

``` xml
 <com.hankim.fabreveallayout.FABRevealLayout
        android:id="@+id/fab_reveal_layout"
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:layout_weight="1">

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            app:backgroundTint="@color/fab"
            android:src="@drawable/ic_play_white"
            style="@style/FABStyle" />

        <!-- first view -->
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">
        </RelativeLayout>

        <!-- second view -->
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@color/white">

        </RelativeLayout>

    </com.hankim.fabreveallayout.FABRevealLayout>
```

## cunstom

in custom mode, you can set Fab position in xml or code:

xml:
```xml
<com.hankim.layout.FabCircleRevealLayout
                android:id="@+id/fab_reveal_layout"
                android:layout_width="match_parent"
                android:layout_height="763dp"
                app:fabMode="custom"
                app:fabX="520dp"
                app:fabY="600dp">
```

code:
```java
fab_reveal_layout.setFabPosition(point)
```

in normal mode, fab position can't change.



### Further animation

![Sample 2](art/fabrl_qotsa.gif)

If you want to animate the items inside the views of a `FABRevealLayout` or perform any other action when the transition is completed, you can register a listener.

``` java
private void configureFABReveal(FABRevealLayout fabRevealLayout) {
    fabRevealLayout.setOnRevealChangeListener(new OnRevealChangeListener() {

        @Override
        public void onViewStartChanged(FabCircleRevealLayout fabCircleRevealLayout, int viewPosition) {

        }

        @Override
        public void onMainViewAppeared(FABRevealLayout fabRevealLayout, View mainView) {}

        @Override
        public void onSecondaryViewAppeared(final FABRevealLayout fabRevealLayout, View secondaryView) {}
    });
}
```

Also, to trigger the reveal and hide animations programmatically, you can use the following methods:

``` java
fabRevealLayout.revealMainView();
fabRevealLayout.revealSecondaryView();
```

### Limitations

Currently, both main and secondary views inside `FABRevealLayout` should have the same height so that the animation works properly.

### Get it!

`FABRevealLayout` is available through JCenter. To be able to use this library in your project, add the following dependency to your `build.gradle` file:

```groovy
dependencies{
	implementation 'com.hankim.layout:FabCircleRevealLayout:0.0.2'
}
```

### Acknowledgements

`FabCircleRevealLayout` is inspire and based on [FABRevealLayout](https://github.com/truizlop/FABRevealLayout) by truizlop.

Thanks for your help.

## License


    Copyright 2015 Jinhaihan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
