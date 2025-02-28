import { createRouter, createWebHashHistory,createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    redirect: '/welcom/detail',
  },
  {
    name: 'notFound',
    path: '/:path(.*)+',
    redirect: '/welcom/detail',
  },
  {
    name: 'welcom',
    path: '/welcom',
    meta: {
      title: '任务列表',
    },
    redirect: '/welcom/list',
    children: [
      {
        name: 'list',
        path: 'list',
        component: () => import('./view/task/list.vue'),
        meta: {
          title: '任务列表',
        },
      }, {
        name: 'detail',
        path: 'detail',
        component: () => import('./view/task/detail.vue'),
        meta: {
          title: '任务详情',
        },
      }, {
        name: 'receiverList',
        path: 'receiverList',
        component: () => import('./view/task/receiverList.vue'),
        meta: {
          title: '接受客户列表',
        },
      }, 
      {
        name: 'todoDetail',
        path: 'todoDetail',
        component: () => import('./view/task/sidebarDetail.vue'),
        meta: {
          title: '代办任务详情',
        },
      },
    ]
  },
];

const router = createRouter({
  routes,
  history: createWebHistory(),
});

router.beforeEach((to, from, next) => {
  const title = to.meta && to.meta.title;
  if (title) {
    document.title = ' '//title;
  }
  next();
});

export { router };
