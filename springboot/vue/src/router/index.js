import Vue from 'vue'
import VueRouter from 'vue-router'
import Manage from '../views/Manage.vue'
import Article from '../views/Article.vue'
import Index from '../views/index.vue'


Vue.use(VueRouter)

const routes = [

  {
      path:'/a',
      name:'Article',
      component:Article
  },
  {
        path:'/',
        name:'Index',
        component:Index
    },
    {
          path:'/login',
          name:'Login',
          component:()=>import('../views/Login.vue')
      },
      {
                path:'/register',
                name:'Register',
                component:()=>import('../views/Register.vue')
      },
      {
          path:"/write",
          name:'Write',
          component:()=>import('../views/Write.vue')
      },

      {
          path:"/articleDetail",
          name:'ArticleDetail',
          component:()=>import('../views/ArticleDetail.vue')
      },
      {
                path:"/xinghuo",
                name:'Xinghuo',
                component:()=>import('../views/Xinghuo.vue')
            },
      {
                path:'/test',
                name:'test',
                component:()=>import('../views/test.vue')
      },
      {
                path:'/resource',
                name:'resource',
                component:()=>import('../views/Resource.vue')
      },


]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
