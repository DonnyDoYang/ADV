#AdLockguardStudio

广告Module的说明
1、整个项目中添加了一个Common的module，用来存放多个平台的统一接口，所有广告平台的module都需要添加如下代码进行依赖
	compile project(':common')
2、每添加一个广告平台需要新建一个module，在每个平台中需要implements PlatformInterface接口，并实现initPlatform方法。在这个方法中会从主module中传入context和平台的appkey。
   这个方法作为每个平台的初始化入口方法。
3、在主module中添加对平台module的依赖：compile project(':youmi')
4、在主module中初始化广告平台的地方通过initPlatform方法初始化需要添加的广告平台。如：
	new YoumiPlatform().initPlatform(context, "abcedfghijklmn");