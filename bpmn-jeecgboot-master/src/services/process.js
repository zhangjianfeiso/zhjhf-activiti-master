import request from '@/utils/request';

/**
 * 获取processXml
 * @param modelId
 * @returns {*}
 */
export function getProcessXml(modelId) {
    return request({
        url: '/process/getProcessXml/' + modelId,
        method: 'get'
    });
}
