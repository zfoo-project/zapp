<template>
    <div>
        <admin-user-auth />
        <admin-time-delete />
        <admin-time-review />
    </div>
</template>

<script>
export default {
    name: 'Admin',
    components: {
        AdminUserAuth: () => import('./component/AdminUserAuth'),
        AdminTimeDelete: () => import('./component/AdminTimeDelete'),
        AdminTimeReview: () => import('./component/AdminTimeReview')
    }
};
</script>
