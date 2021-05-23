# 一、linux安装node

```
把node下载到/usr/local目录下，在/usr/local下新建文件夹nodejs
```

- tar -xvf node-v12.18.3-linux-x64.tar.xz -C /usr/local
- rename node-v12.18.3-linux-x64 nodejs node-v12.18.3-linux-x64

- vim /etc/profile

```
NODE_HOME=/usr/local/nodejs
NODE_PATH=$NODE_HOME/lib/node_modules
PATH=$NODE_HOME/bin:$PATH
export NODE_HOME NODE_PATH PATH
```

- source /etc/profile，加载环境变量

```
- node -v
- npm -v
判断nodejs是否安装(显示版本号等信息，说明已经安装)
```

- 安装puppeteer

```
npm install puppeteer --unsafe-perm=true --allow-root
yum install pango.x86_64 libXcomposite.x86_64 libXcursor.x86_64 libXdamage.x86_64 libXext.x86_64 libXi.x86_64 libXtst.x86_64 cups-libs.x86_64 libXScrnSaver.x86_64 libXrandr.x86_64 GConf2.x86_64 alsa-lib.x86_64 atk.x86_64 gtk3.x86_64 ipa-gothic-fonts xorg-x11-fonts-100dpi xorg-x11-fonts-75dpi xorg-x11-utils xorg-x11-fonts-cyrillic xorg-x11-fonts-Type1 xorg-x11-fonts-misc
```
