import Vue from 'vue';
import Vuex from 'vuex';
// Modules
import app from '@/store/modules/app.js';
import user from '@/store/modules/user.js';
import friend from '@/store/modules/friend.js';
import group from '@/store/modules/group.js';

Vue.use(Vuex);

const store = new Vuex.Store({
    modules: {
        app,
        user,
        friend,
        group
    }
});

export default store;
