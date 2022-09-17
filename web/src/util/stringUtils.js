import vuetify from '@/plugin/vuetify.js';
import { groupConstant, emotions } from '@/constant/constant.js';

export function isBlank(str) {
    if (_.isNil(str)) {
        return true;
    }
    if (_.isUndefined(str)) {
        return true;
    }
    if (_.isEmpty(str.trim())) {
        return true;
    }
    return false;
}


export function numFormat(num) {
    if (num >= 10000) {
        return _.round(num / 10000, 2) + 'W';
    } else if (num >= 1000) {
        return _.round(num / 1000, 2) + 'K';
    }
    return num;
}

export function toTagOrName(userCache) {
    if (isBlank(userCache.tag)) {
        return userCache.name;
    }
    return userCache.tag;
}

export function contentToDisplay(content) {
    if (isBlank(content)) {
        return '';
    }
    let str = _.clone(content);

    // 匹配换行
    let reg = /(\r)*\n/g;
    str = str.replace(reg, '<br/>');

    // 匹配空白字符
    reg = /\s/g;
    str = str.replace(reg, '&nbsp;');

    // 匹配表情
    reg = /\[mdi-(.+?)\]/g;
    const theme = vuetify.framework.theme.isDark ? 'theme--dark' : 'theme--light';
    str = str.replace(reg, '<i aria-hidden="true" class="v-icon notranslate v-icon--dense mdi mdi-$1 ' + theme + ' ' + emotions.textColor + '"></i>');

    // 匹配http链接
    reg = /(http:\/\/|https:\/\/)((\w|=|\?|\.|\/|&|-)+)/g;
    str = str.replace(reg, '<a href="$1$2" class="body-1 blue--text">$1$2</a>');
    return str;
}


const emotionSet = new Set();
for (const emotionArray of emotions.normal) {
    emotionArray.forEach(it => emotionSet.add(it));
}

export function isUrl(content) {
    if (isBlank(content)) {
        return false;
    }
    // 匹配http链接
    const reg = /(http:\/\/|https:\/\/)((\w|=|\?|\.|\/|&|-)+)/g;
    return reg.test(content);
}

export function contentToDisplayList(content) {
    const list = [];

    let temp = '';
    for (let i = 0; i < content.length; i++) {
        const c = content.charAt(i);

        // 匹配空格
        if (c === ' ') {
            let tempSpace = '';
            let countSpace = 0;
            for (let jSpace = i; jSpace < content.length; jSpace++) {
                const cSpace = content.charAt(jSpace);
                if (cSpace !== ' ') {
                    break;
                }
                countSpace++;
                tempSpace = tempSpace + '&nbsp;';
            }

            if (countSpace > 1) {
                if (temp.length > 0) {
                    list.push({
                        type: groupConstant.textMessageEnum.text.type,
                        value: temp
                    });
                }

                list.push({
                    type: groupConstant.textMessageEnum.space.type,
                    value: tempSpace
                });
                i = i + countSpace - 1;
                temp = '';
                continue;
            }
        }

        // 匹配换行
        if (c === '\n' || c === '\r\n') {
            if (temp.length > 0) {
                list.push({
                    type: groupConstant.textMessageEnum.text.type,
                    value: temp
                });
            }

            list.push({
                type: groupConstant.textMessageEnum.ls.type
            });

            temp = '';
            continue;
        }

        // 匹配表情
        if (c === '[') {
            const subStr = content.substring(i, content.length);
            if (subStr.startsWith('[mdi-')) {
                let tempEmotion = '';
                let countEmotion = 0;
                for (let jEmotion = i + 1; jEmotion < content.length; jEmotion++) {
                    countEmotion++;
                    const cEmotion = content.charAt(jEmotion);
                    if (cEmotion === ']') {
                        break;
                    }
                    tempEmotion = tempEmotion + cEmotion;
                }

                if (emotionSet.has(tempEmotion)) {
                    if (temp.length > 0) {
                        list.push({
                            type: groupConstant.textMessageEnum.text.type,
                            value: temp
                        });
                    }
                    list.push({
                        type: groupConstant.textMessageEnum.emotion.type,
                        value: tempEmotion
                    });
                    i = i + countEmotion;
                    temp = '';
                    continue;
                }
            }
        }

        // 匹配http
        if (c === 'h') {
            const subStr = content.substring(i, content.length);
            if (subStr.startsWith('http://') || subStr.startsWith('https://')) {
                let tempHttp = '';
                let countHttp = 0;
                for (let jHttp = i; jHttp < content.length; jHttp++) {
                    const cHttp = content.charAt(jHttp);
                    if (!/(\w|=|\?|\.|\/|&|-|:)/g.test(cHttp)) {
                        break;
                    }
                    countHttp++;
                    tempHttp = tempHttp + cHttp;
                }

                if (tempHttp.length >= 10) {
                    if (temp.length > 0) {
                        list.push({
                            type: groupConstant.textMessageEnum.text.type,
                            value: temp
                        });
                    }
                    list.push({
                        type: groupConstant.textMessageEnum.http.type,
                        value: tempHttp
                    });
                    i = i + countHttp - 1;
                    temp = '';
                    continue;
                }
            }
        }

        temp = temp + c;
    }

    if (temp.length > 0) {
        list.push({
            type: groupConstant.textMessageEnum.text.type,
            value: temp
        });
    }

    return list;
}
