import store from '@/store/store.js';
import { groupConstant } from '@/constant/constant.js';

export function sortedGroupAuths(groupAuths) {
    return _.sortBy(Array.from(groupAuths), it => it.groupAuth).reverse();
}

export function remainGroupAuthsOfMember(groupVo, member) {
    const groupAuthIds = member.groupAuthIds;
    const groupAuthsOfMember = sortedGroupAuths(groupVo.groupAuths);
    for (const groupAuthId of groupAuthIds) {
        _.remove(groupAuthsOfMember, it => it.id === groupAuthId);
    }
    _.remove(groupAuthsOfMember, it => it.id === groupConstant.groupAuthDefaultId);
    return groupAuthsOfMember;
}

export function groupAuthIdsToGroupAuthVos(groupVo, groupAuthIds) {
    const groupAuthVos = [];
    for (const groupAuthVo of sortedGroupAuths(groupVo.groupAuths)) {
        if (_.findIndex(groupAuthIds, it => it === groupAuthVo.id) >= 0) {
            groupAuthVos.push(groupAuthVo);
        }
    }
    _.remove(groupAuthVos, it => it.id === groupConstant.groupAuthDefaultId);
    return groupAuthVos;
}

export function userHasGroupAuth(authEnum, groupVo, groupMemberSimpleVo) {
    if (_.isNil(groupVo) || _.isNil(groupMemberSimpleVo)) {
        return false;
    }
    if (groupVo.id !== groupMemberSimpleVo.groupId) {
        return false;
    }
    // 管理员拥有所有权限
    if (store.state.user.id === groupVo.adminId) {
        return true;
    }
    const groupAuthVos = [];
    for (const groupAuthVo of sortedGroupAuths(groupVo.groupAuths)) {
        if (_.findIndex(groupMemberSimpleVo.groupAuthIds, it => it === groupAuthVo.id) >= 0) {
            groupAuthVos.push(groupAuthVo);
        }
    }
    for (const groupAuthVo of groupAuthVos) {
        if (groupAuthVo.groupAuth >= authEnum.type) {
            return true;
        }
    }
    return false;
}

export function userHasChannelAuth(authEnum, channelId, groupVo, groupMemberSimpleVo) {
    if (_.isNil(groupVo) || _.isNil(groupMemberSimpleVo)) {
        return false;
    }
    if (groupVo.id !== groupMemberSimpleVo.groupId) {
        return false;
    }
    // 管理员拥有所有权限
    if (store.state.user.id === groupVo.adminId) {
        return true;
    }

    let channelVo = null;
    for (const channelBox of groupVo.channelBoxes) {
        for (const channel of channelBox.channels) {
            if (channel.id === channelId) {
                channelVo = channel;
            }
        }
    }
    if (_.isNil(channelVo)) {
        return false;
    }

    const channelAuthVos = [];
    for (const channelAuthVo of channelVo.channelAuths) {
        if (_.findIndex(groupMemberSimpleVo.groupAuthIds, it => it === channelAuthVo.id) >= 0) {
            channelAuthVos.push(channelAuthVo);
        }
    }
    for (const channelAuthVo of channelAuthVos) {
        if (channelAuthVo.channelAuth >= authEnum.type) {
            return true;
        }
    }
    return false;
}

export function updateAllGroupsRedPoint(groups, groupMemberSimpleVos) {
    for (const group of groups) {
        const groupMemberSimpleVo = _.find(groupMemberSimpleVos, it => it.groupId === group.id);
        updateGroupRedPoint(group, groupMemberSimpleVo);
    }
}

export function updateGroupRedPoint(groupVo, groupMemberSimpleVo) {
    if (_.isNil(groupVo) || _.isNil(groupMemberSimpleVo)) {
        return;
    }

    const groupTime = groupMemberSimpleVo.groupTime;

    let flag = false;
    for (const channelBox of groupVo.channelBoxes) {
        for (const channel of channelBox.channels) {
            const show = userHasChannelAuth(groupConstant.channelAuthEnum.baseAuth, channel.id, groupVo, groupMemberSimpleVo);
            if (!show) {
                channel.redPoint = false;
                continue;
            }

            const channelTime = _.find(groupTime.channelTimes, it => it.channelId === channel.id);
            if (_.isNil(channelTime)) {
                flag = true;
                channel.redPoint = true;
                continue;
            }

            if (_.toNumber(channel.refreshTime) > _.toNumber(channelTime.refreshTime)) {
                flag = true;
                channel.redPoint = true;
            } else {
                channel.redPoint = false;
            }
        }
    }

    if (flag) {
        groupVo.redPoint = true;
    } else {
        groupVo.redPoint = false;
    }
}

export function findChannelFromGroupVo(groupVo, channelId) {
    for (const channelBox of groupVo.channelBoxes) {
        for (const channel of channelBox.channels) {
            if (channel.id === channelId) {
                return channel;
            }
        }
    }
    return null;
}

export function findChannelTimeFromGroupMemberSimpleVo(groupMemberSimpleVo, channelId) {
    for (const channelTime of groupMemberSimpleVo.groupTime.channelTimes) {
        if (channelTime.channelId === channelId) {
            return channelTime;
        }
    }
    return null;
}
