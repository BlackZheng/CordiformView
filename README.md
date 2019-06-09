CordiformView
===============

A cordiform android view which hold three progress bar for overview of underway plan,especially for healthy imformation, imitating the widget from Samsung Health(Tizen version)

![CordiformView](https://raw.github.com/BlackZheng/CordiformView/master/screenshot1.jpg) 
![CordiformView](https://raw.github.com/BlackZheng/CordiformView/master//screenshot2.gif)

Gradle
------
```
dependencies {
    ...
    implementation 'com.blakezheng:cordiformView:1.0.0'
}
```

Usage
-----
```xml
<com.blakezheng.widget.cordiformview.CordiformView
        android:layout_width="match_parent"
        android:layout_height="300dp"
        app:strokeWidth="30dp"
        app:leftInnerText="HeartRate"
        app:rightInnerText="Steps"
        app:bottomInnerText="Sleep"
        app:innerTextSize="15sp"
        app:outerTextSize="15sp"
        app:outerTextOffset="4dp"
        app:leftOuterText="68bpm"
        app:rightOuterText="5600"
        app:bottomOuterText="7hrs"/>
```

Limitations
-----------
Stroke width can't be too large because that will expose the drawing trick and mess up the view.
Therefore if stroke width provided exceed max value(max stroke width depends on view size), it will be ignored.

Attributes
----------
        <attr name="leftArcColor" format="color" />
        <attr name="rightArcColor" format="color" />
        <attr name="bottomLineColor" format="color" />
        <attr name="leftProgress" format="integer" />
        <attr name="rightProgress" format="integer" />
        <attr name="bottomProgress" format="integer" />
        <attr name="strokeWidth" format="dimension" />
        <attr name="shadowColor" format="color" />
        <attr name="shadowRadius" format="dimension" />
        <attr name="shadowDx" format="dimension" />
        <attr name="shadowDy" format="dimension" />
        <attr name="leftInnerText" format="string|reference"/>
        <attr name="rightInnerText" format="string|reference"/>
        <attr name="bottomInnerText" format="string|reference"/>
        <attr name="leftOuterText" format="string|reference"/>
        <attr name="rightOuterText" format="string|reference"/>
        <attr name="bottomOuterText" format="string|reference"/>
        <attr name="innerTextSize" format="dimension" />
        <attr name="outerTextSize" format="dimension" />
        <attr name="outerTextOffset" format="dimension" />
        <attr name="innerTextColor" format="color"/>
        
License
-------

    Copyright 2019 Blake Zheng

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.