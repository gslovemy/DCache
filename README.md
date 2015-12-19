###DCache
一个简单，轻量级的数据缓存类库！
###Features
* 使用简单，并且可配置缓存目录，缓存周期，缓存文件上限(还未实现，准备使用LruCache实现)
* 支持缓存数据类型：String字符串，Object，字节数组
###Usage
* 配置DCache:
<pre>DCache.get(this).cacheDir(file).cacheDuration(1000*5);</pre>
* 缓存和获取字符串数据：
<pre>DCache.get(this).putString("test1", "一支穿云箭，千军万马来相见！");
String data = DCache.get(MainActivity.this).getString("test1");</pre>
* 缓存和获取序列化对象数据：
<pre>DCache.get(this).putObject("Stu", new Stu("李晓俊", 25));
Stu data = (Stu) DCache.get(MainActivity.this).getObject("Stu");</pre>

