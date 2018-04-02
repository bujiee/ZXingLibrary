# zing library

@(zxing  )[	QRCode]

**Android**项目开发中经常会用到二维码扫描,例如登陆、支付等谷歌方面已经有了一个开源（https://github.com/zxing/zxing), 里面的内容还是比较多的，如果想深入学习这方面的内容，还是不错的。当然了还是要根据实际需求去选择，所以我们就选择类库中的核心Jar包就行了（地址：https://github.com/ASCN-BJ/ZhaoYun/blob/master/qrcodelibrary/libs/core-3.3.1.jar）。
**Google zxing**中的原装的类库有一个问题就是竖屏的时候会导致无法识别，原因是竖屏的时候没有对相机的预览效果进行处理，所以在项目中，进行了处理，使得竖屏的时候的识别能够成功。所以正好看了一下，就当学习了。

- **二维码扫描** ：识别二维码
- **二维码生成** ：将字符串生成所需要的二维码
- **二维码本地识别** ：识别本地图片中的二维码（例如相册）

-------------------

[TOC]

## zxing简介

> zxing是google的一个二维码识别类库，项目地址：https://github.com/zxing/zxing
## 功能、修改的问题
> 1.类似微信的扫描框，动画
> 2.手势识别，双击放大、缩小，根据手指移动放大、缩小
> 3.根据环境的量暗程度显示手电筒*（手电筒的图标实在难看ε=(´ο｀*)))）

## 二维码扫描
```
  //开启方式
  Intent intent = new Intent(this, activity);
  //预览框的宽高
  intent.putExtra(QRCodeIntent.FRAME_WIDTH, 200);
  intent.putExtra(QRCodeIntent.FRAME_HEIGHT, 180);
  //是否返回结果
  intent.putExtra(QRCodeIntent.SET_RESULT, true);
  startActivityForResult(intent, 10);
```
![](https://github.com/ASCN-BJ/ZXingLibrary/blob/master/pic1.jpg){:height="50%" width="50%"}
## 二维码生成
![enter image description here](https://github.com/ASCN-BJ/ZXingLibrary/blob/master/pic2.png)
## 二维码识别本地图片
![enter image description here](https://github.com/ASCN-BJ/ZXingLibrary/blob/master/pic3.png)
## 反馈与建议
- 邮箱：<bj_email@yeah.net>


---------





