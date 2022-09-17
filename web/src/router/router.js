import Vue from 'vue';
import Router from 'vue-router';
import VueMeta from 'vue-meta';
import i18n from '@/i18n/i18n.js';
import { getXToken } from '@/util/authUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { page } from '@/util/googleAnalyticsUtils.js';
import { groupConstant } from '@/constant/constant.js';

Vue.use(Router);
Vue.use(VueMeta);

const router = new Router({
    mode: 'history',
    base: '/',
    scrollBehavior: () => ({ x: 0, y: 0 }),
    routes: [
        {
            path: '/admin',
            name: 'Admin',
            meta: {
                auth: true,
                title: 'appUrl.admin'
            },
            component: () => import('@/view/admin/Admin.vue')
        },
        {
            path: '/register',
            name: 'Register',
            meta: {
                auth: false,
                title: 'appUrl.register'
            },
            component: () => import('@/view/user/Register.vue')
        },
        {
            path: '/search',
            name: 'Search',
            meta: {
                auth: false,
                title: 'appUrl.search',
                keepAlive: true
            },
            component: () => import('@/view/app/Search.vue')
        },
        {
            path: '/',
            name: 'Home',
            meta: {
                auth: false,
                title: 'appUrl.home',
                keepAlive: true
            },
            component: () => import('@/view/home/Home.vue')
        },
        {
            path: '/subscribe',
            name: 'Subscribe',
            meta: {
                auth: true,
                title: 'appUrl.subscribe',
                keepAlive: true
            },
            component: () => import('@/view/home/Subscribe.vue')
        },
        {
            path: '/creation',
            name: 'CreateTime',
            meta: {
                auth: true,
                title: 'appUrl.creation'
            },
            component: () => import('@/view/time/CreateTime.vue')
        },
        {
            path: '/recommit',
            name: 'RecommitTime',
            meta: {
                auth: true,
                title: 'appUrl.recommit'
            },
            component: () => import('@/view/time/RecommitTime.vue')
        },
        {
            path: '/edit',
            name: 'EditTime',
            meta: {
                auth: true,
                title: 'appUrl.edit'
            },
            component: () => import('@/view/time/EditTime.vue')
        },
        {
            path: '/review',
            name: 'Review',
            meta: {
                auth: true,
                title: 'appUrl.review'
            },
            component: () => import('@/view/time/Review.vue')
        },
        {
            path: '/time/:id',
            name: 'SingleTime',
            meta: {
                auth: false,
                title: 'appUrl.time'
            },
            component: () => import('@/view/time/SingleTime.vue')
        },
        {
            path: '/word/:id',
            name: 'Word',
            meta: {
                auth: false,
                title: 'appUrl.word'
            },
            component: () => import('@/view/word/Word.vue')
        },
        {
            path: '/word/edit/:id',
            name: 'WordEdit',
            meta: {
                auth: true,
                title: 'appUrl.wordEdit'
            },
            component: () => import('@/view/word/WordEdit.vue')
        },
        {
            path: '/category/:id',
            name: 'Category',
            meta: {
                auth: false,
                title: 'appUrl.category'
            },
            component: () => import('@/view/word/Category.vue')
        },
        {
            path: '/category/edit/:id',
            name: 'CategoryEdit',
            meta: {
                auth: true,
                title: 'appUrl.categoryEdit'
            },
            component: () => import('@/view/word/CategoryEdit.vue')
        },
        {
            path: '/location/:id',
            name: 'LocationFeed',
            meta: {
                auth: false,
                title: 'appUrl.location',
                keepAlive: true
            },
            component: () => import('@/view/feed/LocationFeed.vue')
        },
        {
            path: '/item/:id',
            name: 'ItemFeed',
            meta: {
                auth: false,
                title: 'appUrl.item',
                keepAlive: true
            },
            component: () => import('@/view/feed/ItemFeed.vue')
        },
        {
            path: '/person/:id',
            name: 'PersonFeed',
            meta: {
                auth: false,
                title: 'appUrl.person',
                keepAlive: true
            },
            component: () => import('@/view/feed/PersonFeed.vue')
        },

        {
            path: '/user/profile',
            name: 'MyProfile',
            meta: {
                auth: true,
                title: 'appUrl.userProfile',
                keepAlive: true
            },
            component: () => import('@/view/user/MyProfile.vue')
        },
        {
            path: '/user/edit',
            name: 'MyProfileEdit',
            meta: {
                auth: true,
                title: 'appUrl.userProfileEdit'
            },
            component: () => import('@/view/user/MyProfileEdit.vue')
        },
        {
            path: '/user/creation',
            name: 'MyCreation',
            meta: {
                auth: true,
                title: 'appUrl.userCreation'
            },
            component: () => import('@/view/user/MyCreation.vue')
        },
        {
            path: '/user/star',
            name: 'MyStar',
            meta: {
                auth: true,
                title: 'appUrl.userStar'
            },
            component: () => import('@/view/user/MyStar.vue')
        },
        {
            path: '/user/security',
            name: 'AccountSecurity',
            meta: {
                auth: true,
                title: 'appUrl.userSecurity'
            },
            component: () => import('@/view/user/AccountSecurity.vue')
        },
        {
            path: '/user/info/:id',
            name: 'OtherProfile',
            meta: {
                auth: false,
                title: 'appUrl.userInfo',
                keepAlive: true
            },
            component: () => import('@/view/user/OtherProfile.vue')
        },
        {
            path: '/bind/error/:platform',
            name: 'BindError',
            meta: {
                auth: true,
                title: 'appUrl.bindError'
            },
            component: () => import('@/view/user/BindError.vue')
        },

        {
            path: '/terms',
            name: 'terms',
            meta: {
                auth: false,
                title: 'appUrl.terms',
                keepAlive: true
            },
            component: () => import('@/view/app/Terms.vue')
        },

        {
            path: '/license',
            name: 'License',
            meta: {
                auth: false,
                title: 'appUrl.license',
                keepAlive: true
            },
            component: () => import('@/view/app/License.vue')
        },

        {
            path: '/test',
            name: 'Test',
            meta: {
                auth: false,
                title: 'appUrl.terms'
            },
            component: () => import('@/view/Test.vue')
        },
        {
            path: '*',
            name: 'NotFoundPage404',
            meta: {
                auth: false,
                title: 'appUrl.notFound'
            },
            component: () => import('@/view/app/NotFoundPage404.vue')
        }
    ]
});

router.beforeEach((to, from, next) => {
    if (_.startsWith(to.path, groupConstant.invitePath)) {
        next({
            path: '/',
            query: {
                inviteCode: _.replace(to.path, groupConstant.invitePath, '')
            }
        });
        return;
    }
    // 验证权限
    const auth = !isBlank(getXToken());
    if (to.meta.auth && !auth) {
        next({ path: '/' });
        return;
    }
    // 修改标题
    document.title = i18n.t(to.meta.title);
    next();
});

// Bootstrap Analytics
// Set in .env
// https://github.com/MatteoGabriele/vue-analytics
if (process.env.VUE_APP_GOOGLE_ANALYTICS) {
    router.afterEach((to, from, next) => {
        // google统计
        page(document.title, to.path, window.location.href);
        // 百度统计
        window._hmt.push(['_trackPageview', to.fullPath]);
    });
}

export default router;
