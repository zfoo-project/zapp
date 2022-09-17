import { appConstant } from '@/constant/constant.js';
import { isBlank } from '@/util/stringUtils.js';
import store from '@/store/store.js';

// 判断图片是否存在
export function checkImgUrl(imgUrl) {
    return new Promise(function(resolve, reject) {
        const imgObj = new Image();
        imgObj.onload = function() {
            console.log(imgObj.width + '==', imgObj.height + '==');
            resolve('load image success');
        };
        imgObj.onerror = function() {
            console.log('error');
            reject('Could not load image at ' + imgUrl);
        };
        imgObj.src = imgUrl;
    });
}

export function toSimpleAvatarUrl(imgUrl) {
    if (isBlank(imgUrl)) {
        return toHighImgUrl(appConstant.defaultAvatar);
    }
    if (_.startsWith(imgUrl, appConstant.unionAvatarUrl)) {
        return toLowImgUrl(imgUrl);
    }
    if (_.startsWith(imgUrl, appConstant.groupAvatarUrl)) {
        return toLowImgUrl(imgUrl);
    }
    return _.startsWith(imgUrl, appConstant.imgUrl) ? toLowImgUrl(imgUrl) : imgUrl;
}

export function toBackground(background) {
    if (isBlank(background)) {
        return toHighImgUrl(appConstant.defaultBackground);
    }
    return toHighImgUrl(background);
}

export function toGroupBackground(background) {
    if (isBlank(background)) {
        return toHighImgUrl(appConstant.defaultGroupBackground);
    }
    return toHighImgUrl(background);
}

export function toVideoPoster(poster) {
    if (isBlank(poster)) {
        return toHighImgUrl(appConstant.defaultVideoPoster);
    }
    if (_.startsWith(poster, appConstant.ossPolicyEnum.video.prefix) || _.startsWith(poster, appConstant.testVideoUrl)) {
        return poster;
    }
    return toHighImgUrl(poster);
}

export function toLowImgUrl(url) {
    if (isBlank(url)) {
        return toHighImgUrl(appConstant.defaultUrlEmpty);
    }
    const mobile = store.state.app.mobile;
    if (mobile) {
        return url + '!low';
    }
    return url + '!middle';
}

export function toHighImgUrl(url) {
    if (isBlank(url)) {
        return toHighImgUrl(appConstant.defaultUrlEmpty);
    }
    const mobile = store.state.app.mobile;
    if (mobile) {
        return url + '!middle';
    }
    return url + '!high';
}


/**
 * 获取文件后缀名称
 */
export function toFileType(fileName) {
    if (isBlank(fileName)) {
        return '';
    }
    const start = fileName.lastIndexOf('.');
    if (start < 0) {
        return '';
    }
    const end = fileName.length;
    return fileName.substring(start, end);
}

export function download(data, strFileName, strMimeType, progressCallback, endCallback) {
    const self = window; // this script is only for browsers anyway...
    const defaultMime = 'application/octet-stream'; // this default mime also triggers iframe downloads
    let mimeType = strMimeType || defaultMime;
    let payload = data;
    const url = !strFileName && !strMimeType && payload;
    const anchor = document.createElement('a');
    const toString = function(a) {
        return String(a);
    };
    let MyBlob = (self.Blob || self.MozBlob || self.WebKitBlob || toString);
    let fileName = strFileName || 'download';
    let reader;
    MyBlob = MyBlob.call ? MyBlob.bind(self) : Blob;

    if (String(this) === 'true') { // reverse arguments, allowing download.bind(true, "text/xml", "export.xml") to act as a callback
        payload = [payload, mimeType];
        mimeType = payload[0];
        payload = payload[1];
    }


    if (url && url.length < 2048) { // if no filename and no mime, assume a url was passed as the only argument
        fileName = url.split('/').pop().split('?')[0];
        anchor.href = url; // assign href prop to temp anchor
        if (anchor.href.indexOf(url) !== -1) { // if the browser determines that it's a potentially valid url path:
            const ajax = new XMLHttpRequest();
            ajax.open('GET', url, true);
            ajax.responseType = 'blob';
            ajax.onload = function(event) {
                download(event.target.response, fileName, defaultMime);
            };
            ajax.onprogress = function(progressEvent) {
                if (_.isNil(progressCallback)) {
                    return;
                }

                if (progressEvent.lengthComputable) {
                    const complete = progressEvent.loaded / progressEvent.total * 100 | 0;
                    progressCallback(complete);
                }
            };
            ajax.onloadend = function(event) {
                if (_.isNil(endCallback)) {
                    return;
                }
                endCallback();
            };
            setTimeout(() => ajax.send(), 0); // allows setting custom ajax headers using the return:
            return ajax;
        } // end if valid url?
    } // end if url?


    // go ahead and download dataURLs right away
    if (/^data:([\w+-]+\/[\w+.-]+)?[,;]/.test(payload)) {
        if (payload.length > (1024 * 1024 * 1.999) && MyBlob !== toString) {
            payload = dataUrlToBlob(payload);
            mimeType = payload.type || defaultMime;
        } else {
            return navigator.msSaveBlob // IE10 can't do a[download], only Blobs:
                ? navigator.msSaveBlob(dataUrlToBlob(payload), fileName)
                : saver(payload); // everyone else can save dataURLs un-processed
        }
    } else { // not data url, is it a string with special needs?
        if (/([\x80-\xff])/.test(payload)) {
            let i = 0;
            const tempUiArr = new Uint8Array(payload.length);
            const mx = tempUiArr.length;
            for (i; i < mx; ++i) tempUiArr[i] = payload.charCodeAt(i);
            payload = new MyBlob([tempUiArr], { type: mimeType });
        }
    }
    const blob = payload instanceof MyBlob
        ? payload
        : new MyBlob([payload], { type: mimeType });


    function dataUrlToBlob(strUrl) {
        const parts = strUrl.split(/[:;,]/);
        const type = parts[1];
        const decoder = parts[2] === 'base64' ? atob : decodeURIComponent;
        const binData = decoder(parts.pop());
        const mx = binData.length;
        let i = 0;
        const uiArr = new Uint8Array(mx);

        for (i; i < mx; ++i) uiArr[i] = binData.charCodeAt(i);

        return new MyBlob([uiArr], { type: type });
    }

    function saver(url, winMode) {
        if ('download' in anchor) { // html5 A[download]
            anchor.href = url;
            anchor.setAttribute('download', fileName);
            anchor.className = 'download-js-link';
            anchor.innerHTML = 'downloading...';
            anchor.style.display = 'none';
            document.body.appendChild(anchor);
            setTimeout(function() {
                anchor.click();
                document.body.removeChild(anchor);
                if (winMode === true) {
                    setTimeout(() => self.URL.revokeObjectURL(anchor.href), 250);
                }
            }, 66);
            return true;
        }

        // handle non-a[download] safari as best we can:
        if (/(Version)\/(\d+)\.(\d+)(?:\.(\d+))?.*Safari\//.test(navigator.userAgent)) {
            if (/^data:/.test(url)) url = 'data:' + url.replace(/^data:([\w\/\-\+]+)/, defaultMime);
            if (!window.open(url)) { // popup blocked, offer direct download:
                if (confirm('Displaying New Document\n\nUse Save As... to download, then click back to return to this page.')) {
                    location.href = url;
                }
            }
            return true;
        }

        // do iframe dataURL download (old ch+FF):
        const f = document.createElement('iframe');
        document.body.appendChild(f);

        if (!winMode && /^data:/.test(url)) { // force a mime that will download:
            url = 'data:' + url.replace(/^data:([\w\/\-\+]+)/, defaultMime);
        }
        f.src = url;
        setTimeout(function() {
            document.body.removeChild(f);
        }, 333);
    }// end saver


    if (navigator.msSaveBlob) { // IE10+ : (has Blob, but not a[download] or URL)
        return navigator.msSaveBlob(blob, fileName);
    }

    if (self.URL) { // simple fast and modern way using Blob and URL:
        saver(self.URL.createObjectURL(blob), true);
    } else {
        // handle non-Blob()+non-URL browsers:
        if (typeof blob === 'string' || blob.constructor === toString) {
            try {
                return saver('data:' + mimeType + ';base64,' + self.btoa(blob));
            } catch (y) {
                return saver('data:' + mimeType + ',' + encodeURIComponent(blob));
            }
        }

        // Blob but not URL support:
        reader = new FileReader();
        reader.onload = function(e) {
            saver(this.result);
        };
        reader.readAsDataURL(blob);
    }
    return true;
} /* end download() */
