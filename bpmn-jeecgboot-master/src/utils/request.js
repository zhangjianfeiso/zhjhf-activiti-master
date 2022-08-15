import axios from 'axios';
import router from '../router';
import { message , Modal} from 'ant-design-vue';
import store from '@/store';

// 创建axios实例
const service = axios.create({
    // baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
    // withCredentials: true, // 跨域请求时发送cookie
    //baseURL: config.BASE_API,
    timeout: 30000 // 请求超时
});

// 请求拦截器
let loadingInstance = null;
service.interceptors.request.use(
    (config) => {
        let xtoken = localStorage.getItem('token');
        if (xtoken != null) {
            config.headers['token'] = xtoken;
            config.headers['Content-Type'] = 'application/json';
        }
        if (config.responseType === 'blob') {  // blob类型 延长超时时间
            config.timeout = 60000;
            message.loading({content:'Loading',duration:0})
        }
        if (config.url.includes('add') || config.url.includes('edit') || config.url.includes('update') || config.url.includes('create') || config.url.includes('save')) {
            message.loading({content:'Loading',duration:0})
        }
        return config;
    },
    (error) => {
        // 处理请求错误
        console.log(error); // for debug
        return Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    /**
     * 如果您想获得http信息，例如头信息或状态信息
     * 请返回 response => response
     */
    /**
     * 通过自定义代码确定请求状态
     * 这里只是一个例子
     * 还可以通过HTTP状态代码来判断状态
     */
    (response) => {
        if (response.config.responseType === 'blob') {  // 下载时直接return 返回blob
            message.destroy();
            return response.data;
        }
        if (response.config.url.includes('add') || response.config.url.includes('edit') || response.config.url.includes('update') || response.config.url.includes('create') || response.config.url.includes('save')) {
            message.destroy();;
        }
        const res = response.data;
        console.info('res__',res);
        // 如果状态码不是0，则判断为错误。
        if (res.status !== 0 && res.status !== 200) {
            if (res.status == '400') {
                message.destroy(); // 关闭之前的弹出信息
                message.error(res.message || 'Error');
            }

            // 50008: 非法的令牌; 50012: 其他客户端登录; 50014: 令牌过期;
            if (res.code === 401 || res.code === 50012 || res.code === 50014) {
                // 重新登陆
                Modal.warning({
                    title: '提示',
                    content: '很抱歉，登录已过期，请重新登录...',
                    onOk() {
                        store.dispatch('user/resetToken').then(() => {
                            location.reload();
                        });
                    }
                });
            }
            return Promise.reject(res);
        } else {
            return res;
        }
    },
    (error) => {
        console.log('err___' + error.request.response); // for debug
        if (!error.request.response) {
            message.error('服务连接失败');
            return Promise.reject(error);
        }
        const err = JSON.parse(error.request.response);
        message.destroy();
        if (error.request &&
            error.request.response &&
            JSON.parse(error.request.response) &&
            JSON.parse(error.request.response).status === 600 &&
            JSON.parse(error.request.response).message) {
            return Promise.reject(error);
        }
        if (err.status === 401) {
            // 重新登陆
            Modal.warning({
                title: '提示',
                content: '很抱歉，登录已过期，请重新登录...',
                onOk() {
                    localStorage.removeItem('token');
                    router.push({ path: '/login' });
                }
            });
            return Promise.reject(error);
        }

        if (
            error.request &&
            error.request.response &&
            JSON.parse(error.request.response) &&
            JSON.parse(error.request.response).status == 400 &&
            JSON.parse(error.request.response).message
        ) {
            message.error(JSON.parse(error.request.response).message);
            return Promise.reject(error);
        }
        if (error.message.indexOf('timeout') !== -1) {
            message.error('请检查网络后重试');
            return Promise.reject(error);
        } else {
            message.error(JSON.parse(error.request.response).message);
            return Promise.reject(error);
        }
    }
);
export default service;
