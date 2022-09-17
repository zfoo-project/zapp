import request from '@/util/requestUtils.js';
import axios from 'axios';
import { appConstant } from '@/constant/constant.js';

/**
 * @param type 0头像，1图片，2视频
 */
export function policyApi(type) {
    return request({
        url: '/api/aliyun/oss/policy',
        method: 'get',
        params: { type }
    });
}

let count = 0;

export function uploadFileToOssApi(file, ossPolicy, progressCallback) {
    count++;

    const key = _.isEqual(ossPolicy.host, appConstant.unionUrl) || ossPolicy.dir.indexOf('avatar') !== -1 || ossPolicy.dir.indexOf('background') !== -1
        ? ossPolicy.dir
        : (ossPolicy.dir + count);

    const formData = new FormData();
    formData.append('key', key);
    formData.append('policy', ossPolicy.policy);
    formData.append('OSSAccessKeyId', ossPolicy.accessKeyId);
    formData.append('success_action_status', 200);
    formData.append('callback', '');
    formData.append('signature', ossPolicy.signature);
    formData.append('file', file);
    return {
        key,
        callback: axios({
            url: ossPolicy.host,
            method: 'post',
            data: formData,
            headers: { 'Content-Type': 'multipart/form-data' },
            onUploadProgress: (progressEvent) => {
                if (_.isNil(progressCallback)) {
                    return;
                }

                const complete = progressEvent.loaded / progressEvent.total * 100 | 0;
                progressCallback(file.name, complete);
            }
        })
    };
}
