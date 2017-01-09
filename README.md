# FrameAnimation

[![Download](https://api.bintray.com/packages/jingweiwang/maven/FrameAnimation/images/download.svg)](https://bintray.com/jingweiwang/maven/FrameAnimation/_latestVersion)
[![Build Status](https://travis-ci.org/JingweiWang/FrameAnimation.svg?branch=master)](https://travis-ci.org/JingweiWang/FrameAnimation)
[![Coverage Status](https://coveralls.io/repos/github/JingweiWang/FrameAnimation/badge.svg)](https://coveralls.io/github/JingweiWang/FrameAnimation)

FrameAnimation is a powerful library for displaying frame animation in Android applications.

### *Requirements*

FrameAnimation can be included in any Android application.

FrameAnimation supports Android 4.0 and later.

### *Using FrameAnimation in your application*

If you are building with Gradle, simply add the following line to the `dependencies` section of your `build.gradle` file:

```groovy
compile 'io.github.jingweiwang.library:FrameAnimation:0.2.0'
```

For full details, visit the documentation on our web site, available in Chinese:

[开始使用](https://jingweiwang.github.io/FrameAnimation/)

### *Attention Please*

FrameAnimation now longer has a direct dependency on the Android Support library. Instead, it expects certain part of the support library to be provided by the including application. If you do not include the Android Support Library yourself already, you will have to add the following line to your build.gradle file:

```groovy
compile 'com.android.support:appcompat-v7:25.1.0'
```

### *Join the FrameAnimation community*

Please use our [issues page](https://github.com/JingweiWang/FrameAnimation/issues) to let us know of any problems.

Welcom to pull requests.

### *Donate*

If you willing to spend 10 Yuan on me lunch, please take out your mobile phone, open Alipay or WeChat to scan QR code. I will be appreciate you.

![Donate](./donate.png)

### *License*

FrameAnimation is released under the [Apache 2.0 license](LICENSE).

```
Copyright 2017 JingweiWang

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